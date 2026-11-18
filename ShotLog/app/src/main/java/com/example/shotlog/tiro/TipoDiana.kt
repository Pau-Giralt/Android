package com.example.shotlog.tiro

/**
 * Medidas oficiales ISSF de las dianas de 10 m, en milímetros.
 *
 * - Pistola: el 10 mide 11,5 mm de diámetro y cada anillo suma 16 mm (8 mm de radio).
 *   La zona negra va del 7 al 10 (59,5 mm). Diana completa: 155,5 mm.
 * - Carabina: el 10 es un punto de 0,5 mm y cada anillo suma 5 mm (2,5 mm de radio).
 *   La zona negra va del 4 al 9 (30,5 mm). Diana completa: 45,5 mm.
 */
enum class TipoDiana(
    val radioDiez: Float,
    val anchoAnillo: Float,
    val primerAnilloNegro: Int,
    val diametroDiezInterior: Float,
    val diezBlanco: Boolean,
    val cuentanDecimales: Boolean,
    private val diametrosZoom: List<Float>
) {
    PISTOLA(
        radioDiez = 5.75f,
        anchoAnillo = 8f,
        primerAnilloNegro = 7,
        diametroDiezInterior = 5f,
        diezBlanco = false,
        cuentanDecimales = false,
        diametrosZoom = listOf(162f, 74f, 36f)
    ),
    CARABINA(
        radioDiez = 0.25f,
        anchoAnillo = 2.5f,
        primerAnilloNegro = 4,
        diametroDiezInterior = 0f,
        diezBlanco = true,
        cuentanDecimales = true,
        diametrosZoom = listOf(48f, 37f, 9f)
    );

    /** Calibre del balín (4,5 mm en las dos modalidades). */
    val calibre: Float get() = 4.5f

    /** Radio exterior del anillo n (1 a 10). */
    fun radioAnillo(n: Int): Float = radioDiez + (10 - n) * anchoAnillo

    /** Cuántos milímetros de diana caben a lo ancho en cada nivel de zoom. */
    fun diametroVisible(nivel: NivelZoom): Float = diametrosZoom[nivel.ordinal]
}
