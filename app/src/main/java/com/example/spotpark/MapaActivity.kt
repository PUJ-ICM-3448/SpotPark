package com.example.spotpark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MapaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        val btnParqueadero1 = findViewById<Button>(R.id.btnParqueadero1)
        val btnParqueadero2 = findViewById<Button>(R.id.btnParqueadero2)
        val btnBusquedaVoz = findViewById<Button>(R.id.btnBusquedaVoz)
        val btnNotificaciones = findViewById<Button>(R.id.btnNotificaciones)

        btnNotificaciones.setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
        }

        btnParqueadero1.setOnClickListener {
            startActivity(Intent(this, DetalleParqueaderoActivity::class.java))
        }

        btnParqueadero2.setOnClickListener {
            startActivity(Intent(this, DetalleParqueaderoActivity::class.java))
        }

        btnBusquedaVoz.setOnClickListener {
            startActivity(Intent(this, DetalleParqueaderoActivity::class.java))
        }
    }
}