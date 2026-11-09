package com.example.shotlog

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

/** Empezar un entrenamiento o una competición: modalidad y, en competición, formato. */
class NuevaSesionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COMPETICION = "competicion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nueva_sesion)
        aplicarMargenesSistema()

        val esCompeticion = intent.getBooleanExtra(EXTRA_COMPETICION, false)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(if (esCompeticion) R.string.nueva_competicion else R.string.nuevo_entrenamiento)
        toolbar.setNavigationOnClickListener { finish() }

        // El formato (60 o 40 disparos) solo se elige en competición
        findViewById<View>(R.id.seccionFormato).visibility = if (esCompeticion) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.tvInfo).setText(
            if (esCompeticion) R.string.info_competicion else R.string.info_entrenamiento
        )

        findViewById<MaterialButton>(R.id.btnEmpezar).setOnClickListener {
            Toast.makeText(this, R.string.empezar_proximamente, Toast.LENGTH_LONG).show()
        }
    }
}
