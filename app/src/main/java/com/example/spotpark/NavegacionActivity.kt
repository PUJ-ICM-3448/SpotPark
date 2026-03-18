package com.example.spotpark

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class NavegacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navegacion)

        val btnFinalizarRuta = findViewById<Button>(R.id.btnFinalizarRuta)

        btnFinalizarRuta.setOnClickListener {
            startActivity(Intent(this, MapaActivity::class.java))
            finish()
        }
    }
}