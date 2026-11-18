package com.example.shotlog

import com.example.shotlog.modelo.Entreno
import com.example.shotlog.modelo.Modalidad
import com.example.shotlog.modelo.ModoEntreno
import com.example.shotlog.tiro.RegistroEntreno
import java.util.Calendar
import java.util.Random

/**
 * Entrenos de ejemplo para que la lista no salga vacía.
 * Los disparos se generan alrededor del centro con una dispersión realista.
 */
object DatosEjemplo {

    fun entrenos(): List<Entreno> = listOf(
        crear(6, "Preparación Copa Catalunya", fecha(2026, 11, 4), Modalidad.PISTOLA, ModoEntreno.COMPETICION,
            60, 9.5f, 11, "Buen ritmo. En la serie 4 me precipité en dos disparos."),
        crear(5, "Técnica de gatillo", fecha(2026, 11, 2), Modalidad.PISTOLA, ModoEntreno.ENTRENAMIENTO,
            40, 10.5f, 22, "Trabajo de presión continua. Mejor en las dos últimas series."),
        crear(4, "Liga de club · Jornada 3", fecha(2026, 10, 26), Modalidad.PISTOLA, ModoEntreno.COMPETICION,
            60, 10f, 33, ""),
        crear(3, "Prueba de carabina", fecha(2026, 10, 22), Modalidad.CARABINA, ModoEntreno.ENTRENAMIENTO,
            30, 1.6f, 44, "Primera vez con la carabina del club."),
        crear(2, "Series de 10 disparos", fecha(2026, 10, 19), Modalidad.PISTOLA, ModoEntreno.ENTRENAMIENTO,
            50, 11f, 55, "Respiración más controlada."),
        crear(1, "Tirada social", fecha(2026, 10, 12), Modalidad.CARABINA, ModoEntreno.COMPETICION,
            40, 1.4f, 66, "")
    )

    private fun crear(
        id: Long, nombre: String, fecha: Long, modalidad: Modalidad, modo: ModoEntreno,
        disparos: Int, dispersionMm: Float, semilla: Long, notas: String
    ): Entreno {
        val registro = RegistroEntreno(modalidad, modo)
        val azar = Random(semilla)
        repeat(disparos) {
            registro.registrar(
                (azar.nextGaussian() * dispersionMm).toFloat(),
                (azar.nextGaussian() * dispersionMm).toFloat()
            )
        }
        return registro.aEntreno(id, nombre, notas, fecha)
    }

    private fun fecha(anio: Int, mes: Int, dia: Int): Long =
        Calendar.getInstance().apply { set(anio, mes - 1, dia, 18, 0, 0) }.timeInMillis
}
