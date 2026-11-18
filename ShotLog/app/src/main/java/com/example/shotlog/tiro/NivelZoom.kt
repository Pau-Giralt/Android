package com.example.shotlog.tiro

/** Niveles de zoom de la diana. Se pasa de uno a otro en ciclo: 1 → 2 → 3 → 1. */
enum class NivelZoom {
    COMPLETO,
    MEDIO,
    MAXIMO;

    val numero: Int get() = ordinal + 1

    fun siguiente(): NivelZoom = entries[(ordinal + 1) % entries.size]
}
