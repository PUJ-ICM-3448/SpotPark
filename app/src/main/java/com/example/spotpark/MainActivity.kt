package com.example.spotpark

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen(
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
    onLogin: () -> Unit,
    onVoiceLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

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
            label = { Text("Usuario o Correo Electrónico", color = grayHint) },
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
            onClick = onLogin,
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