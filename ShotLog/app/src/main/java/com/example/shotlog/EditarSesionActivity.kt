package com.example.shotlog

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

/** Editar una sesión: se pueden cambiar el nombre y las notas. */
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

        // Sesión de ejemplo (la primera si se abre desde la pantalla inicial)
        val posicion = intent.getIntExtra(EXTRA_POSICION, 0)
        val sesion = DatosEjemplo.sesiones[posicion]

        findViewById<TextView>(R.id.tvTipo).setText(
            if (sesion.esCompeticion) R.string.tipo_competicion else R.string.tipo_entrenamiento
        )
        findViewById<TextView>(R.id.tvDetalle).text =
            getString(R.string.sesion_detalle, sesion.fecha, sesion.modalidad)
        findViewById<TextView>(R.id.tvPuntuacion).text = sesion.puntuacion.toString()
        findViewById<TextView>(R.id.tvMaximo).text = getString(R.string.de_maximo, sesion.maximo)
        findViewById<TextInputEditText>(R.id.etNombreSesion).setText(sesion.nombre)
        findViewById<TextInputEditText>(R.id.etNotas).setText(sesion.notas)
        findViewById<TextView>(R.id.tvAvisoCompeticion).visibility =
            if (sesion.esCompeticion) View.VISIBLE else View.GONE

        // Una miniatura por diana
        val contenedor = findViewById<LinearLayout>(R.id.contenedorDianas)
        sesion.dianas.forEachIndexed { indice, puntos ->
            val miniatura = layoutInflater.inflate(R.layout.item_diana_mini, contenedor, false)
            miniatura.findViewById<TextView>(R.id.tvNumDiana).text = getString(R.string.diana_numero, indice + 1)
            miniatura.findViewById<TextView>(R.id.tvPuntosDiana).text = puntos.toString()
            miniatura.contentDescription = getString(R.string.diana_descripcion, indice + 1, puntos)
            contenedor.addView(miniatura)
        }

        findViewById<MaterialButton>(R.id.btnGuardar).setOnClickListener {
            Toast.makeText(this, R.string.cambios_guardados, Toast.LENGTH_SHORT).show()
            finish()
        }

        // Borrar pide confirmación
        findViewById<MaterialButton>(R.id.btnEliminar).setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.eliminar_titulo)
                .setMessage(R.string.eliminar_mensaje)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.eliminar) { _, _ ->
                    Toast.makeText(this, R.string.sesion_eliminada, Toast.LENGTH_SHORT).show()
                    finish()
                }
                .show()
        }
    }
}
