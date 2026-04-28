package com.example.spotpark

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.json.JSONObject
import java.net.URL
import java.util.Locale

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapa: GoogleMap
    private lateinit var clienteUbicacion: FusedLocationProviderClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<android.widget.LinearLayout>
    private val listaPuntos = mutableListOf<LatLng>()
    private var ubicacionActual: LatLng? = null
    private var mostrandoRuta = false
    private val CODIGO_PERMISO = 1001
    private val API_KEY = "AIzaSyCThZOLA5GrE4OHNvRTsZuLUBCBXpVnOn4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val bottomSheet = findViewById<android.widget.LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = 120
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        val etBuscarDireccion = findViewById<EditText>(R.id.etBuscarDireccion)
        etBuscarDireccion.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                buscarYTrazarRuta(etBuscarDireccion.text.toString())
                true
            } else false
        }

        val btnUbicacion = findViewById<Button>(R.id.btnUbicacion)
        val btnParqueadero1 = findViewById<Button>(R.id.btnParqueadero1)
        val btnParqueadero2 = findViewById<Button>(R.id.btnParqueadero2)
        val btnBusquedaVoz = findViewById<Button>(R.id.btnBusquedaVoz)
        val btnNotificaciones = findViewById<Button>(R.id.btnNotificaciones)

        btnUbicacion.setOnClickListener {
            mostrandoRuta = false
            pedirPermisoYUbicar()
        }
        btnNotificaciones.setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
        }
        btnParqueadero1.setOnClickListener {
            val destino = LatLng(4.6275, -74.0650)
            trazarRuta(destino)
        }
        btnParqueadero2.setOnClickListener {
            val destino = LatLng(4.6310, -74.0665)
            trazarRuta(destino)
        }
        btnBusquedaVoz.setOnClickListener {
            startActivity(Intent(this, DetalleParqueaderoActivity::class.java))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap
        mapa.uiSettings.isZoomControlsEnabled = true
        mapa.uiSettings.isMyLocationButtonEnabled = true

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.view?.setPadding(0, 0, 0, 120)

        val javeriana = LatLng(4.6280, -74.0641)
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(javeriana, 15f))

        val parqueaderoJaveriana = LatLng(4.6275, -74.0650)
        val parqueaderoMarly = LatLng(4.6310, -74.0665)
        mapa.addMarker(MarkerOptions().position(parqueaderoJaveriana).title("Parqueadero Javeriana"))
        mapa.addMarker(MarkerOptions().position(parqueaderoMarly).title("Parqueadero Marly"))

        mapa.setOnMapLongClickListener { punto ->
            mapa.addMarker(MarkerOptions().position(punto).title("Punto seleccionado"))
            trazarRuta(punto)
        }

        pedirPermisoYUbicar()
    }

    private fun buscarYTrazarRuta(direccion: String) {
        if (direccion.isEmpty()) return
        Thread {
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                val resultados = geocoder.getFromLocationName(direccion, 1)
                if (!resultados.isNullOrEmpty()) {
                    val lugar = resultados[0]
                    val destino = LatLng(lugar.latitude, lugar.longitude)
                    runOnUiThread {
                        mapa.addMarker(MarkerOptions().position(destino).title(direccion))
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        trazarRuta(destino)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al buscar dirección", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun trazarRuta(destino: LatLng) {
        val origen = ubicacionActual ?: return
        mostrandoRuta = true
        Thread {
            try {
                val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=${origen.latitude},${origen.longitude}" +
                        "&destination=${destino.latitude},${destino.longitude}" +
                        "&mode=driving" +
                        "&key=$API_KEY"

                val respuesta = URL(url).readText()
                val json = JSONObject(respuesta)
                val rutas = json.getJSONArray("routes")

                if (rutas.length() > 0) {
                    val puntos = rutas.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")

                    val listaPuntosRuta = decodificarPolyline(puntos)

                    runOnUiThread {
                        mapa.addPolyline(
                            PolylineOptions()
                                .addAll(listaPuntosRuta)
                                .color(android.graphics.Color.BLUE)
                                .width(10f)
                        )
                        val limites = com.google.android.gms.maps.model.LatLngBounds.Builder()
                        listaPuntosRuta.forEach { limites.include(it) }
                        mapa.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(limites.build(), 100)
                        )
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al trazar ruta", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun decodificarPolyline(encoded: String): List<LatLng> {
        val lista = mutableListOf<LatLng>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < encoded.length) {
            var b: Int
            var shift = 0
            var resultado = 0
            do {
                b = encoded[index++].code - 63
                resultado = resultado or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            lat += if (resultado and 1 != 0) (resultado shr 1).inv() else resultado shr 1

            shift = 0
            resultado = 0
            do {
                b = encoded[index++].code - 63
                resultado = resultado or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            lng += if (resultado and 1 != 0) (resultado shr 1).inv() else resultado shr 1

            lista.add(LatLng(lat / 1E5, lng / 1E5))
        }
        return lista
    }

    private fun pedirPermisoYUbicar() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            activarUbicacion()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                CODIGO_PERMISO
            )
        }
    }

    private fun activarUbicacion() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        mapa.isMyLocationEnabled = true

        val solicitud = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(resultado: LocationResult) {
                resultado.lastLocation?.let { ubicacion ->
                    val punto = LatLng(ubicacion.latitude, ubicacion.longitude)
                    ubicacionActual = punto
                    listaPuntos.add(punto)

                    if (!mostrandoRuta) {
                        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(punto, 15f))
                    }

                    if (listaPuntos.size > 1 && !mostrandoRuta) {
                        mapa.addPolyline(
                            PolylineOptions()
                                .addAll(listaPuntos)
                                .color(android.graphics.Color.GREEN)
                                .width(8f)
                        )
                    }
                }
            }
        }
        clienteUbicacion.requestLocationUpdates(solicitud, callback, mainLooper)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODIGO_PERMISO && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            activarUbicacion()
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }
}