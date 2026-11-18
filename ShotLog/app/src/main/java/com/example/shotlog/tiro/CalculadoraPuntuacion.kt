package com.example.shotlog.tiro

import com.example.shotlog.modelo.Disparo
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.min

/**
 * Calcula la puntuación de un disparo a partir de su posición (en mm respecto al centro).
 *
 * Como en las dianas reales, el disparo vale el anillo más alto que toca el borde del balín,
 * así que se compara la distancia del centro del balín con "radio del anillo + medio calibre".
 * La puntuación se guarda en décimas (104 = 10,4), igual que la muestran los blancos electrónicos.
 */
object CalculadoraPuntuacion {

    private const val MARGEN = 0.0001f

    fun decimas(tipo: TipoDiana, x: Float, y: Float): Int {
        val distancia = hypot(x, y)
        val limiteDiez = tipo.radioDiez + tipo.calibre / 2
        val limiteUno = tipo.radioAnillo(1) + tipo.calibre / 2

        return when {
            distancia > limiteUno + MARGEN -> 0
            // Dentro del 10: de 10,0 (en el borde) a 10,9 (en el centro)
            distancia <= limiteDiez -> 100 + min(9, floor((1 - distancia / limiteDiez) * 10 + MARGEN).toInt())
            // Del 9,9 al 1,0: una décima por cada décima parte del anillo
            else -> floor((10 - (distancia - limiteDiez) / tipo.anchoAnillo) * 10 + MARGEN).toInt()
        }
    }

    /** En carabina cuentan las décimas; en pistola solo la parte entera de cada disparo. */
    fun decimasParaTotal(tipo: TipoDiana, decimas: Int): Int =
        if (tipo.cuentanDecimales) decimas else decimas / 10 * 10

    fun total(tipo: TipoDiana, disparos: List<Disparo>): Int =
        disparos.sumOf { decimasParaTotal(tipo, it.decimas) }

    /** Máximo posible por disparo: 10,9 en carabina y 10 en pistola. */
    fun maximoPorDisparo(tipo: TipoDiana): Int = if (tipo.cuentanDecimales) 109 else 100
}
