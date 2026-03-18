package com.example.spotpark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val btnConductor = findViewById<Button>(R.id.btnConductor)
        val btnAdministrador = findViewById<Button>(R.id.btnAdministrador)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val txtIrLogin = findViewById<TextView>(R.id.txtIrLogin)

        btnConductor.setOnClickListener {
            btnConductor.setBackgroundResource(R.drawable.bg_tipo_seleccionado)
            btnAdministrador.setBackgroundResource(R.drawable.bg_tipo_normal)
        }

        btnAdministrador.setOnClickListener {
            startActivity(Intent(this, RegistroAdminActivity::class.java))
        }

        btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, MapaActivity::class.java))
        }

        txtIrLogin.setOnClickListener {
            finish()
        }
    }
}