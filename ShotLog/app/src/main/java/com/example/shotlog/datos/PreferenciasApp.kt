package com.example.shotlog.datos

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.shotlog.modelo.Modalidad

/** Preferencias de la app guardadas en el móvil (SharedPreferences). */
object PreferenciasApp {

    private const val ARCHIVO = "shotlog_preferencias"
    private const val TEMA_OSCURO = "tema_oscuro"
    private const val MODALIDAD = "modalidad_por_defecto"

    private fun preferencias(contexto: Context) =
        contexto.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    // ---------- Tema ----------

    fun temaOscuro(contexto: Context): Boolean = preferencias(contexto).getBoolean(TEMA_OSCURO, false)

    fun guardarTemaOscuro(contexto: Context, oscuro: Boolean) {
        preferencias(contexto).edit { putBoolean(TEMA_OSCURO, oscuro) }
        aplicarTema(oscuro)
    }

    /** Cambia el tema de toda la app (Android vuelve a dibujar las pantallas abiertas). */
    fun aplicarTema(oscuro: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (oscuro) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    // ---------- Modalidad por defecto ----------

    fun modalidadPorDefecto(contexto: Context): Modalidad {
        val guardada = preferencias(contexto).getString(MODALIDAD, null)
        return Modalidad.entries.find { it.name == guardada } ?: Modalidad.PISTOLA
    }

    fun guardarModalidad(contexto: Context, modalidad: Modalidad) {
        preferencias(contexto).edit { putString(MODALIDAD, modalidad.name) }
    }
}
