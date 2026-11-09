package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/** Primera pantalla: iniciar sesión, registrarse o entrar como invitado. */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        aplicarMargenesSistema()

        findViewById<TextView>(R.id.tvRegistrarse).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        // De momento se entra directamente. La comprobación del usuario se hará con Firebase.
        findViewById<MaterialButton>(R.id.btnIniciarSesion).setOnClickListener { entrar() }
        findViewById<MaterialButton>(R.id.btnInvitado).setOnClickListener { entrar() }
    }

    /** Va a Inicio y cierra el Login para que "atrás" no vuelva aquí. */
    private fun entrar() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
