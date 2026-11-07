package com.example.shotlog

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Deja espacio para la barra de estado, la barra de navegación y el teclado,
 * para que el contenido no quede tapado (la app usa edge-to-edge).
 * Todas las pantallas tienen la vista raíz con id "main".
 */
fun AppCompatActivity.aplicarMargenesSistema() {
    ViewCompat.setOnApplyWindowInsetsListener(findViewById<View>(R.id.main)) { vista, insets ->
        val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        val teclado = insets.getInsets(WindowInsetsCompat.Type.ime())
        vista.setPadding(barras.left, barras.top, barras.right, maxOf(barras.bottom, teclado.bottom))
        insets
    }
}
