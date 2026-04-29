package com.example.spotpark

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spotpark.ui.theme.RegistroActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(
                auth = auth,
                onLogin = {
                    startActivity(Intent(this, MapaActivity::class.java))
                },
                onVoiceLogin = {
                    startActivity(Intent(this, MapaActivity::class.java))
                },
                onGoRegister = {
                    startActivity(Intent(this, RegistroActivity::class.java))
                }
            )
        }
    }
}

@Composable
fun LoginScreen(
    auth: FirebaseAuth,
    onLogin: () -> Unit,
    onVoiceLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val context = LocalContext.current

    val darkGreen = Color(0xFF003B1F)
    val midGreen = Color(0xFF1E523C)
    val softGreen = Color(0xFF2C6448)
    val white = Color.White
    val grayHint = Color(0xFFD0D0D0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkGreen)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_spotpark),
            contentDescription = "Logo SpotPark",
            modifier = Modifier.size(240.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Encuentra-Reserva-Parquea",
            color = white,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Correo Electrónico", color = grayHint) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = midGreen,
                unfocusedContainerColor = midGreen,
                focusedTextColor = white,
                unfocusedTextColor = white,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = grayHint,
                unfocusedLabelColor = grayHint
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña", color = grayHint) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = midGreen,
                unfocusedContainerColor = midGreen,
                focusedTextColor = white,
                unfocusedTextColor = white,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = grayHint,
                unfocusedLabelColor = grayHint
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
                auth.signInWithEmailAndPassword(usuario, contrasena)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // ¡Éxito! Navegamos al Home
                            onLogin()
                        } else {
                            // Error (ej: contraseña mal o usuario no existe)
                            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }},
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = softGreen)
        ) {
            Text("Iniciar Sesión", color = white)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onVoiceLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = softGreen)
        ) {
            Text("Iniciar con voz", color = white)
        }

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "¿No tienes cuenta?",
            color = white,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onGoRegister,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text("Regístrate", color = white)
        }
    }
}