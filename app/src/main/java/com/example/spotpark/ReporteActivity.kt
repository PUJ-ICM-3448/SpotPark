package com.example.spotpark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ReporteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte)

        val btnDisponible = findViewById<Button>(R.id.btnDisponible)
        val btnOcupado = findViewById<Button>(R.id.btnOcupado)
        val btnEnviarReporte = findViewById<Button>(R.id.btnEnviarReporte)

        btnDisponible.setOnClickListener {
            btnDisponible.setBackgroundResource(R.drawable.bg_tipo_seleccionado)
            btnOcupado.setBackgroundResource(R.drawable.bg_tipo_normal)
        }

        btnOcupado.setOnClickListener {
            btnOcupado.setBackgroundResource(R.drawable.bg_tipo_seleccionado)
            btnDisponible.setBackgroundResource(R.drawable.bg_tipo_normal)
        }

        btnEnviarReporte.setOnClickListener {
            startActivity(Intent(this, MapaActivity::class.java))
            finish()
        }
    }
}