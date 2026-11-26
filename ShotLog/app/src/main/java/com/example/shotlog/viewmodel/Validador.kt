package com.example.shotlog.viewmodel

import androidx.annotation.StringRes
import com.example.shotlog.R

/**
 * Reglas de validación de los formularios de Login y Registro.
 *
 * Cada función devuelve el texto de error (como id de `strings.xml`) o `null` si el valor es correcto.
 * No usa nada de la pantalla, así que la comparten los dos ViewModels y se podrá probar con tests.
 */
object Validador {

    const val LONGITUD_MINIMA_NOMBRE = 3
    const val LONGITUD_MINIMA_CONTRASENA = 6

    private val FORMATO_CORREO = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    @StringRes
    fun errorNombre(nombre: String): Int? = when {
        nombre.isBlank() -> R.string.error_campo_vacio
        nombre.trim().length < LONGITUD_MINIMA_NOMBRE -> R.string.error_nombre_corto
        else -> null
    }

    @StringRes
    fun errorCorreo(correo: String): Int? = when {
        correo.isBlank() -> R.string.error_campo_vacio
        !FORMATO_CORREO.matches(correo.trim()) -> R.string.error_correo_invalido
        else -> null
    }

    @StringRes
    fun errorContrasena(contrasena: String): Int? = when {
        contrasena.isEmpty() -> R.string.error_campo_vacio
        contrasena.length < LONGITUD_MINIMA_CONTRASENA -> R.string.error_contrasena_corta
        else -> null
    }

    @StringRes
    fun errorRepetirContrasena(contrasena: String, repetida: String): Int? = when {
        repetida.isEmpty() -> R.string.error_campo_vacio
        repetida != contrasena -> R.string.error_contrasenas_distintas
        else -> null
    }
}
