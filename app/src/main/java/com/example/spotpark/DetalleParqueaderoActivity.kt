package com.example.spotpark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DetalleParqueaderoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_parqueadero)

        val btnIniciarRuta = findViewById<Button>(R.id.btnIniciarRuta)
        val btnReportar = findViewById<Button>(R.id.btnReportar)
        val btnVolverMapa = findViewById<Button>(R.id.btnVolverMapa)

        btnIniciarRuta.setOnClickListener {
            startActivity(Intent(this, NavegacionActivity::class.java))
        }

        btnReportar.setOnClickListener {
            startActivity(Intent(this, ReporteActivity::class.java))
        }

        btnVolverMapa.setOnClickListener {
            finish()
        }
    }
}
