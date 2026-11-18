package com.example.shotlog.modelo

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Funciones de extensión para mostrar puntuaciones y fechas. */

/** Décimas → texto: 104 → "10,4" (con decimales) o "10" (sin decimales). */
fun Int.comoPuntos(conDecimales: Boolean): String =
    if (conDecimales) String.format(Locale.getDefault(), "%.1f", this / 10f)
    else (this / 10).toString()

/** Un disparo siempre se muestra con su décima, también en pistola. */
fun Disparo.textoPuntuacion(): String = decimas.comoPuntos(true)

fun Entreno.textoTotal(): String = totalDecimas.comoPuntos(modalidad.tipoDiana.cuentanDecimales)

fun Entreno.textoMaximo(): String = maximoDecimas.comoPuntos(modalidad.tipoDiana.cuentanDecimales)

/** Milisegundos → "17/11/2026". */
fun Long.comoFecha(): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(this))
