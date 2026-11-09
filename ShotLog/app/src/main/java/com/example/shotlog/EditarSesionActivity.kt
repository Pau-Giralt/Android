package com.example.shotlog

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

/** Editar un entreno: nombre y notas. */
class EditarSesionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_POSICION = "posicion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_sesion)
        aplicarMargenesSistema()

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        val sesion = DatosEjemplo.sesiones[intent.getIntExtra(EXTRA_POSICION, 0)]
        findViewById<TextInputEditText>(R.id.etNombreSesion).setText(sesion.nombre)
        findViewById<TextInputEditText>(R.id.etNotas).setText(sesion.notas)
        findViewById<TextView>(R.id.tvAvisoCompeticion).visibility =
            if (sesion.esCompeticion) View.VISIBLE else View.GONE

        findViewById<MaterialButton>(R.id.btnGuardar).setOnClickListener {
            Toast.makeText(this, R.string.cambios_guardados, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
