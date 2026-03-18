package com.example.spotpark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegistroAdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_admin)

        val btnConductor = findViewById<Button>(R.id.btnConductor)
        val btnAdministrador = findViewById<Button>(R.id.btnAdministrador)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val txtIrLogin = findViewById<TextView>(R.id.txtIrLogin)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        btnAdministrador.setOnClickListener {
            btnAdministrador.setBackgroundResource(R.drawable.bg_tipo_seleccionado)
            btnConductor.setBackgroundResource(R.drawable.bg_tipo_normal)
        }

        btnConductor.setOnClickListener {
            btnConductor.setBackgroundResource(R.drawable.bg_tipo_seleccionado)
            btnAdministrador.setBackgroundResource(R.drawable.bg_tipo_normal)
            startActivity(Intent(this, RegistroActivity::class.java))
            finish()
        }

        btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, MapaActivity::class.java))
        }

        txtIrLogin.setOnClickListener {
            finish()
        }

        btnVolver.setOnClickListener {
            finish()
        }
    }
}