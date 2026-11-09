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

/** Cierra la sesión: vuelve al Login y borra el historial de pantallas. */
fun AppCompatActivity.irAlLogin() {
    val intent = android.content.Intent(this, LoginActivity::class.java)
    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
}
