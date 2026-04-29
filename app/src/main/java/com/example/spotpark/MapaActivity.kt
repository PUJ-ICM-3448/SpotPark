package com.example.spotpark

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
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
    private val API_KEY = "TU_API_KEY"

    private lateinit var sensorManager: SensorManager
    private var acelerometro: Sensor? = null
    private var enMovimiento = false
    private var proximitySensor: Sensor? = null

    private var destinoGlobal: LatLng? = null

    // Manejo de rutas y marcadores activos
    private val rutasActivas = mutableListOf<Polyline>()
    private val marcadoresDestino = mutableListOf<Marker>()

    // Cola de rutas
    private data class DestinoPendiente(val titulo: String, val destino: LatLng)
    private val colaDestinos = mutableListOf<DestinoPendiente>()
    private var yaLlegoAlActual = false

    // 🔥 HARDWARE
    private val PICK_IMAGE = 200
    private val TAKE_PHOTO = 201

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val bottomSheet = findViewById<android.widget.LinearLayout>(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        val etBuscarDireccion = findViewById<EditText>(R.id.etBuscarDireccion)
        etBuscarDireccion.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                manejarNuevaRuta(etBuscarDireccion.text.toString(), null)
                true
            } else false
        }

        val btnUbicacion = findViewById<Button>(R.id.btnUbicacion)
        val btnParqueadero1 = findViewById<Button>(R.id.btnParqueadero1)
        val btnParqueadero2 = findViewById<Button>(R.id.btnParqueadero2)
        val btnBusquedaVoz = findViewById<Button>(R.id.btnBusquedaVoz)
        val btnNotificaciones = findViewById<Button>(R.id.btnNotificaciones)
        val btnHardware = findViewById<Button>(R.id.btnHardware)

        btnHardware.setOnClickListener {
            mostrarOpcionesHardware()
        }

        btnUbicacion.setOnClickListener {
            mostrandoRuta = false
            pedirPermisoYUbicar()
        }

        btnNotificaciones.setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
        }

        btnParqueadero1.setOnClickListener {
            manejarNuevaRuta("Parqueadero Javeriana", LatLng(4.6275, -74.0650))
        }

        btnParqueadero2.setOnClickListener {
            manejarNuevaRuta("Parqueadero Marly", LatLng(4.6310, -74.0665))
        }

        btnBusquedaVoz.setOnClickListener {
            startActivity(Intent(this, DetalleParqueaderoActivity::class.java))
        }
    }

    // ================= HARDWARE =================

    private fun mostrarOpcionesHardware() {
        val opciones = arrayOf("Tomar foto", "Elegir de galería")

        AlertDialog.Builder(this)
            .setTitle("Selecciona opción")
            .setItems(opciones) { _, which ->
                if (which == 0) abrirCamara()
                else abrirGaleria()
            }
            .show()
    }

    private fun abrirCamara() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, TAKE_PHOTO)
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Toast.makeText(this, "📁 Imagen seleccionada", Toast.LENGTH_SHORT).show()
            }
            if (requestCode == TAKE_PHOTO) {
                val bitmap = data?.extras?.get("data") as Bitmap
                guardarBitmap(bitmap)
                Toast.makeText(this, "📷 Foto tomada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarBitmap(bitmap: Bitmap) {
        MediaStore.Images.Media.insertImage(
            contentResolver,
            bitmap,
            "SpotPark",
            null
        )
    }

    // ================= SENSORES =================

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val magnitud = Math.sqrt(
                (event.values[0]*event.values[0] +
                        event.values[1]*event.values[1] +
                        event.values[2]*event.values[2]).toDouble()
            )
            enMovimiento = magnitud > 13
            if (!enMovimiento) verificarLlegada()
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val proximityListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (proximitySensor != null && event.values[0] < proximitySensor!!.maximumRange) {
                Toast.makeText(this@MapaActivity, "Sensor proximidad activado", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    override fun onResume() {
        super.onResume()
        acelerometro?.also { sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
        proximitySensor?.also { sensorManager.registerListener(proximityListener, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
        sensorManager.unregisterListener(proximityListener)
    }

    // ================= MAPA =================

    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap
        mapa.uiSettings.isZoomControlsEnabled = true
        mapa.uiSettings.isMyLocationButtonEnabled = true

        val javeriana = LatLng(4.6280, -74.0641)
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(javeriana, 15f))

        mapa.setOnMapLongClickListener {
            manejarNuevaRuta("Punto seleccionado", it)
        }

        pedirPermisoYUbicar()
    }

    private fun manejarNuevaRuta(texto: String, destino: LatLng?) {
        if (destino != null) {
            destinoGlobal = destino
            trazarRuta(destino)
        } else {
            buscarDireccionYTrazar(texto)
        }
    }

    private fun buscarDireccionYTrazar(direccion: String) {
        Thread {
            val geocoder = Geocoder(this, Locale.getDefault())
            val resultados = geocoder.getFromLocationName(direccion, 1)
            if (!resultados.isNullOrEmpty()) {
                val destino = LatLng(resultados[0].latitude, resultados[0].longitude)
                runOnUiThread { trazarRuta(destino) }
            }
        }.start()
    }

    private fun trazarRuta(destino: LatLng) {
        val origen = ubicacionActual ?: return
        Thread {
            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=${origen.latitude},${origen.longitude}" +
                    "&destination=${destino.latitude},${destino.longitude}" +
                    "&mode=driving&key=$API_KEY"

            val json = JSONObject(URL(url).readText())
            val puntos = json.getJSONArray("routes")
                .getJSONObject(0)
                .getJSONObject("overview_polyline")
                .getString("points")

            val ruta = decodificarPolyline(puntos)

            runOnUiThread {
                mapa.addPolyline(PolylineOptions().addAll(ruta).width(10f))
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            activarUbicacion()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                CODIGO_PERMISO)
        }
    }

    private fun activarUbicacion() {
        val solicitud = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000).build()
        clienteUbicacion.requestLocationUpdates(solicitud,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let {
                        ubicacionActual = LatLng(it.latitude, it.longitude)
                    }
                }
            }, mainLooper)
    }

    private fun verificarLlegada() {
        if (ubicacionActual == null || destinoGlobal == null) return
        val resultados = FloatArray(1)
        Location.distanceBetween(
            ubicacionActual!!.latitude,
            ubicacionActual!!.longitude,
            destinoGlobal!!.latitude,
            destinoGlobal!!.longitude,
            resultados
        )
        if (resultados[0] < 30) {
            Toast.makeText(this, "🚗 Has llegado al destino", Toast.LENGTH_LONG).show()
        }
    }
}