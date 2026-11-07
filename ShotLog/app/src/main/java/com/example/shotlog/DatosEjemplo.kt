package com.example.shotlog

/** Una sesión de tiro (entrenamiento o competición). */
data class Sesion(
    val nombre: String,
    val fecha: String,
    val modalidad: String,
    val esCompeticion: Boolean,
    val dianas: List<Int>,
    val notas: String
) {
    val puntuacion: Int get() = dianas.sum()
    val maximo: Int get() = dianas.size * 100
}

/**
 * Datos de ejemplo para enseñar la interfaz (actividad 05).
 * Más adelante saldrán de la base de datos.
 */
object DatosEjemplo {
    val sesiones = listOf(
        Sesion("Preparación Copa Catalunya", "04/11/2026", "Pistola", true,
            listOf(94, 92, 95, 93, 96, 92), "Buen ritmo. En la serie 4 me precipité en dos disparos."),
        Sesion("Técnica de gatillo", "02/11/2026", "Pistola", false,
            listOf(91, 93, 92, 95), "Trabajo de presión continua. Mejor en las dos últimas dianas."),
        Sesion("Liga de club · Jornada 3", "26/10/2026", "Pistola", true,
            listOf(92, 93, 91, 94, 92, 93), ""),
        Sesion("Prueba de carabina", "22/10/2026", "Carabina", false,
            listOf(96, 97, 95), "Primera vez con la carabina del club."),
        Sesion("Series de 10 disparos", "19/10/2026", "Pistola", false,
            listOf(90, 92, 93, 91, 94), "Respiración más controlada."),
        Sesion("Tirada social", "12/10/2026", "Carabina", true,
            listOf(95, 96, 94, 97), "")
    )
}
