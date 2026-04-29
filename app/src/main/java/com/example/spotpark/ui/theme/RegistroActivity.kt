package com.example.spotpark.ui.theme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spotpark.MapaActivity
import com.example.spotpark.R
import com.example.spotpark.RegistroAdminActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

/*class RegistroActivity : AppCompatActivity() {

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
}*/




class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = Firebase.auth


        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnConductor = findViewById<Button>(R.id.btnConductor)
        val btnAdministrador = findViewById<Button>(R.id.btnAdministrador)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val txtIrLogin = findViewById<TextView>(R.id.txtIrLogin)

        btnConductor.setBackgroundResource(R.drawable.bg_tipo_seleccionado)
        btnAdministrador.setBackgroundResource(R.drawable.bg_tipo_normal)

        btnAdministrador.setOnClickListener {
            val intent = Intent(this, RegistroAdminActivity::class.java)
            startActivity(intent)
        }


        btnRegistrarse.setOnClickListener {
            val email = etCorreo.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MapaActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Escribe un correo y contraseña", Toast.LENGTH_SHORT).show()
            }
        }

        txtIrLogin.setOnClickListener {
            finish()
        }
    }
}