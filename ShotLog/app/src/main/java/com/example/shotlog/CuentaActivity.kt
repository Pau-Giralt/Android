package com.example.shotlog

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

/** Cuenta: privacidad, cambiar contraseña y cerrar sesión. */
class CuentaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cuenta)
        aplicarMargenesSistema()

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnCambiarContrasena).setOnClickListener {
            Toast.makeText(this, R.string.proximamente, Toast.LENGTH_SHORT).show()
        }
        findViewById<MaterialButton>(R.id.btnCerrarSesion).setOnClickListener { irAlLogin() }
    }
}
