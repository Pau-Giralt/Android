package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

/**
 * Pantalla inicial TEMPORAL con botones para llegar a todas las pantallas.
 * En la actividad 06 se sustituirá por la pantalla de Inicio con el menú lateral.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        aplicarMargenesSistema()

        abrirAlPulsar(R.id.btnIrLogin, LoginActivity::class.java)
        abrirAlPulsar(R.id.btnIrRegistro, RegistroActivity::class.java)
        abrirAlPulsar(R.id.btnIrGuia, GuiaActivity::class.java)
        abrirAlPulsar(R.id.btnIrPreferencias, PreferenciasActivity::class.java)
        abrirAlPulsar(R.id.btnIrSesiones, SesionesActivity::class.java)
        abrirAlPulsar(R.id.btnIrEditar, EditarSesionActivity::class.java)
        abrirAlPulsar(R.id.btnAyuda, GuiaActivity::class.java)
    }

    private fun abrirAlPulsar(idBoton: Int, pantalla: Class<*>) {
        findViewById<View>(idBoton).setOnClickListener {
            startActivity(Intent(this, pantalla))
        }
    }
}
