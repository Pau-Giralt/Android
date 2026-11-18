package com.example.shotlog

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.shotlog.datos.RepositorioEntrenos
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

/** Editar un entreno: nombre y notas. */
class EditarSesionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        setContentView(R.layout.activity_editar_sesion)
        aplicarMargenesSistema()

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        val entreno = RepositorioEntrenos.obtener(intent.getLongExtra(EXTRA_ID, 0L))
        if (entreno == null) {
            finish()
            return
        }

        val etNombre = findViewById<TextInputEditText>(R.id.etNombreSesion)
        val etNotas = findViewById<TextInputEditText>(R.id.etNotas)
        if (savedInstanceState == null) {
            etNombre.setText(entreno.nombre)
            etNotas.setText(entreno.notas)
        }
        findViewById<TextView>(R.id.tvAvisoCompeticion).visibility =
            if (entreno.esCompeticion) View.VISIBLE else View.GONE

        findViewById<MaterialButton>(R.id.btnGuardar).setOnClickListener {
            val nombre = etNombre.text.toString().trim().ifEmpty { entreno.nombre }
            RepositorioEntrenos.modificar(entreno.id, nombre, etNotas.text.toString().trim())
            Toast.makeText(this, R.string.entreno_modificado, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
