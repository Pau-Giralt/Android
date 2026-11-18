package com.example.shotlog.tiro

import com.example.shotlog.modelo.Disparo
import com.example.shotlog.modelo.Entreno
import com.example.shotlog.modelo.Modalidad
import com.example.shotlog.modelo.ModoEntreno
import com.example.shotlog.modelo.Serie

/**
 * Lleva la cuenta de un entreno mientras se dispara: disparos, series de 10 y totales.
 * No sabe nada de la pantalla: la Activity solo le pasa coordenadas y lee el resultado.
 *
 * @param disparosMaximos en competición, 60 o 40; en entrenamiento, 0 (sin límite).
 */
class RegistroEntreno(
    val modalidad: Modalidad,
    val modo: ModoEntreno,
    val disparosMaximos: Int = 0
) {
    private val lista = mutableListOf<Disparo>()

    val tipoDiana: TipoDiana get() = modalidad.tipoDiana
    val disparos: List<Disparo> get() = lista
    val ultimo: Disparo? get() = lista.lastOrNull()

    /** Serie que se ve en la diana. El disparo 11 empieza la serie 2, y así sucesivamente. */
    val indiceSerieActual: Int get() = if (lista.isEmpty()) 0 else (lista.size - 1) / DISPAROS_POR_SERIE
    val numeroSerieActual: Int get() = indiceSerieActual + 1
    val disparosSerieActual: List<Disparo> get() = lista.drop(indiceSerieActual * DISPAROS_POR_SERIE)

    val series: List<Serie>
        get() = lista.chunked(DISPAROS_POR_SERIE).mapIndexed { i, disparos -> Serie(i + 1, disparos) }
    val seriesCompletas: List<Serie> get() = series.filter { it.completa }

    val totalDecimas: Int get() = CalculadoraPuntuacion.total(tipoDiana, lista)
    val totalSerieActualDecimas: Int get() = CalculadoraPuntuacion.total(tipoDiana, disparosSerieActual)

    /** En competición se termina al llegar al número de disparos del formato. */
    val terminado: Boolean get() = disparosMaximos > 0 && lista.size >= disparosMaximos

    fun registrar(x: Float, y: Float): Disparo {
        val disparo = Disparo(
            numero = lista.size + 1,
            x = x,
            y = y,
            decimas = CalculadoraPuntuacion.decimas(tipoDiana, x, y)
        )
        lista.add(disparo)
        return disparo
    }

    fun deshacer(): Disparo? = lista.removeLastOrNull()

    fun aEntreno(id: Long, nombre: String, notas: String, fecha: Long): Entreno =
        Entreno(id, nombre, fecha, modalidad, modo, series, notas)

    companion object {
        const val DISPAROS_POR_SERIE = 10
    }
}
