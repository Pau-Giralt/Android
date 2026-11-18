package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.shotlog.datos.PreferenciasApp
import com.example.shotlog.modelo.Modalidad
import com.example.shotlog.modelo.ModoEntreno
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup

/** Empezar un entreno: modalidad, modo y, en competición, formato (60 o 40 disparos). */
class NuevaSesionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_COMPETICION = "competicion"
    }

    private lateinit var grupoModalidad: MaterialButtonToggleGroup
    private lateinit var grupoModo: MaterialButtonToggleGroup
    private lateinit var grupoFormato: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        setContentView(R.layout.activity_nueva_sesion)
        aplicarMargenesSistema()

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.nuevo_entreno)
        toolbar.setNavigationOnClickListener { finish() }

        grupoModalidad = findViewById(R.id.grupoModalidad)
        grupoModo = findViewById(R.id.grupoModo)
        grupoFormato = findViewById(R.id.grupoFormato)

        if (savedInstanceState == null) {
            // Modalidad por defecto de Preferencias
            grupoModalidad.check(
                if (PreferenciasApp.modalidadPorDefecto(this) == Modalidad.CARABINA) R.id.btnCarabina else R.id.btnPistola
            )
            // Desde Inicio se puede llegar con la competición ya marcada
            if (intent.getBooleanExtra(EXTRA_COMPETICION, false)) grupoModo.check(R.id.btnModoCompeticion)
        }
        grupoModo.addOnButtonCheckedListener { _, _, _ -> mostrarSegunModo() }
        mostrarSegunModo()

        findViewById<MaterialButton>(R.id.btnEmpezar).setOnClickListener { empezar() }
    }

    private fun esCompeticion() = grupoModo.checkedButtonId == R.id.btnModoCompeticion

    /** El formato solo se elige en competición. */
    private fun mostrarSegunModo() {
        val competicion = esCompeticion()
        findViewById<View>(R.id.seccionFormato).visibility = if (competicion) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.tvInfo).setText(
            if (competicion) R.string.info_competicion else R.string.info_entrenamiento
        )
    }

    private fun empezar() {
        val modalidad = if (grupoModalidad.checkedButtonId == R.id.btnCarabina) Modalidad.CARABINA else Modalidad.PISTOLA
        val modo = if (esCompeticion()) ModoEntreno.COMPETICION else ModoEntreno.ENTRENAMIENTO
        val maximo = when {
            !esCompeticion() -> 0
            grupoFormato.checkedRadioButtonId == R.id.rbFormato40 -> 40
            else -> 60
        }
        startActivity(
            Intent(this, RegistroTirosActivity::class.java)
                .putExtra(RegistroTirosActivity.EXTRA_MODALIDAD, modalidad.name)
                .putExtra(RegistroTirosActivity.EXTRA_MODO, modo.name)
                .putExtra(RegistroTirosActivity.EXTRA_MAXIMO, maximo)
        )
    }
}
