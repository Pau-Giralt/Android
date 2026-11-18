package com.example.shotlog.modelo

import androidx.annotation.StringRes
import com.example.shotlog.R
import com.example.shotlog.tiro.CalculadoraPuntuacion
import com.example.shotlog.tiro.TipoDiana

enum class Modalidad(@param:StringRes val nombre: Int, val tipoDiana: TipoDiana) {
    PISTOLA(R.string.modalidad_pistola, TipoDiana.PISTOLA),
    CARABINA(R.string.modalidad_carabina, TipoDiana.CARABINA)
}

enum class ModoEntreno(@param:StringRes val nombre: Int) {
    ENTRENAMIENTO(R.string.tipo_entrenamiento),
    COMPETICION(R.string.tipo_competicion)
}

/**
 * Un disparo. La posición se guarda en milímetros respecto al centro de la diana
 * (no en píxeles), así se dibuja bien con cualquier zoom y en cualquier pantalla.
 * La puntuación va en décimas: 104 = 10,4.
 */
data class Disparo(
    val numero: Int,
    val x: Float,
    val y: Float,
    val decimas: Int
)

data class Serie(
    val numero: Int,
    val disparos: List<Disparo>
) {
    val completa: Boolean get() = disparos.size == 10

    fun totalDecimas(tipo: TipoDiana): Int = CalculadoraPuntuacion.total(tipo, disparos)
}

data class Entreno(
    val id: Long,
    val nombre: String,
    val fecha: Long,
    val modalidad: Modalidad,
    val modo: ModoEntreno,
    val series: List<Serie>,
    val notas: String
) {
    val disparos: List<Disparo> get() = series.flatMap { it.disparos }
    val esCompeticion: Boolean get() = modo == ModoEntreno.COMPETICION
    val totalDecimas: Int get() = CalculadoraPuntuacion.total(modalidad.tipoDiana, disparos)
    val maximoDecimas: Int
        get() = disparos.size * CalculadoraPuntuacion.maximoPorDisparo(modalidad.tipoDiana)
}
