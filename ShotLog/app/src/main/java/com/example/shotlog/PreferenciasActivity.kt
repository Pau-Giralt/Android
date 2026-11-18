package com.example.shotlog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shotlog.datos.PreferenciasApp
import com.example.shotlog.modelo.Modalidad
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup

/** Preferencias: tema (claro u oscuro) y modalidad por defecto. Se guardan en SharedPreferences. */
class PreferenciasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        setContentView(R.layout.activity_preferencias)
        aplicarMargenesSistema()

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        // Tema: se marca la opción guardada y, al cambiarla, se guarda y se aplica
        val grupoTema = findViewById<MaterialButtonToggleGroup>(R.id.grupoTema)
        grupoTema.check(if (PreferenciasApp.temaOscuro(this)) R.id.btnTemaOscuro else R.id.btnTemaClaro)
        grupoTema.addOnButtonCheckedListener { _, id, marcado ->
            if (marcado) PreferenciasApp.guardarTemaOscuro(this, id == R.id.btnTemaOscuro)
        }

        // Modalidad por defecto: saldrá marcada al empezar un entreno
        val grupoModalidad = findViewById<MaterialButtonToggleGroup>(R.id.grupoModalidad)
        grupoModalidad.check(
            if (PreferenciasApp.modalidadPorDefecto(this) == Modalidad.CARABINA) R.id.btnCarabina else R.id.btnPistola
        )
        grupoModalidad.addOnButtonCheckedListener { _, id, marcado ->
            if (marcado) {
                PreferenciasApp.guardarModalidad(
                    this, if (id == R.id.btnCarabina) Modalidad.CARABINA else Modalidad.PISTOLA
                )
            }
        }
    }
}
