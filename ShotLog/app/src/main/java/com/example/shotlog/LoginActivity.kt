package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        aplicarMargenesSistema()

        // Ir a la pantalla de registro
        findViewById<TextView>(R.id.tvRegistrarse).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        // De momento (actividad 05) estos botones solo vuelven a la pantalla anterior.
        // La comprobación del usuario se hará con Firebase más adelante.
        findViewById<MaterialButton>(R.id.btnIniciarSesion).setOnClickListener { finish() }
        findViewById<MaterialButton>(R.id.btnInvitado).setOnClickListener { finish() }
    }
}
