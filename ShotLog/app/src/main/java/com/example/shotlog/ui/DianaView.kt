package com.example.shotlog.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.PathInterpolator
import androidx.core.content.ContextCompat
import com.example.shotlog.R
import com.example.shotlog.modelo.Disparo
import com.example.shotlog.tiro.NivelZoom
import com.example.shotlog.tiro.TipoDiana
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * Dibuja una diana ISSF a escala real con sus impactos.
 *
 * Todo se calcula en milímetros y se pasa a píxeles con [escala] (píxeles por mm),
 * que depende del zoom. Así los disparos, guardados en mm, quedan siempre en su sitio.
 *
 * - Fuera del modo registro, tocar la diana cambia el zoom (1 → 2 → 3 → 1).
 * - En modo registro, tocar la diana avisa con [alTocarEnRegistro] y la posición en mm.
 */
class DianaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var tipoDiana: TipoDiana = TipoDiana.PISTOLA
        set(value) {
            field = value
            diametroVisible = value.diametroVisible(nivelZoom)
            actualizarDescripcion()
            invalidate()
        }

    var disparos: List<Disparo> = emptyList()
        set(value) {
            field = value
            actualizarDescripcion()
            invalidate()
        }

    var modoRegistro: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    /** false en las miniaturas: no responden al toque y no resaltan el último disparo. */
    var interactiva: Boolean = true

    var nivelZoom: NivelZoom = NivelZoom.COMPLETO
        private set

    var alTocarEnRegistro: ((xMm: Float, yMm: Float) -> Unit)? = null
    var alCambiarZoom: ((NivelZoom) -> Unit)? = null

    private var diametroVisible = tipoDiana.diametroVisible(nivelZoom)
    private var animadorZoom: ValueAnimator? = null
    private var toqueX = 0f
    private var toqueY = 0f

    private val colorPapel = ContextCompat.getColor(context, R.color.diana_papel)
    private val colorNegro = ContextCompat.getColor(context, R.color.diana_negro)
    private val colorLineaClara = ContextCompat.getColor(context, R.color.diana_linea_clara)

    private val pintura = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pinturaTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val pinturaImpacto = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pinturaBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.sius_registro)
    }

    private val densidad = resources.displayMetrics.density

    /** Píxeles por milímetro con el zoom actual. */
    private val escala: Float get() = min(width, height) / diametroVisible

    init {
        isClickable = true
        isFocusable = true
        actualizarDescripcion()
    }

    // ---------- Zoom ----------

    fun cambiarZoom(nivel: NivelZoom, animar: Boolean = true) {
        nivelZoom = nivel
        val destino = tipoDiana.diametroVisible(nivel)
        animadorZoom?.cancel()
        if (animar && isLaidOut) {
            animadorZoom = ValueAnimator.ofFloat(diametroVisible, destino).apply {
                duration = 380
                interpolator = PathInterpolator(0.4f, 0f, 0.2f, 1f)
                addUpdateListener {
                    diametroVisible = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            diametroVisible = destino
            invalidate()
        }
        actualizarDescripcion()
        alCambiarZoom?.invoke(nivel)
    }

    /** Para las miniaturas: el zoom más grande en el que se ven todos los disparos. */
    fun ajustarZoomADisparos() {
        val alejado = disparos.maxOfOrNull { hypot(it.x, it.y) + tipoDiana.calibre } ?: 0f
        val nivel = NivelZoom.entries.lastOrNull { tipoDiana.diametroVisible(it) / 2 >= alejado }
            ?: NivelZoom.COMPLETO
        cambiarZoom(nivel, animar = false)
    }

    // ---------- Medida ----------

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val ancho = MeasureSpec.getSize(widthMeasureSpec)
        val alto = MeasureSpec.getSize(heightMeasureSpec)
        val lado = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED || alto == 0) ancho
        else min(ancho, alto)
        setMeasuredDimension(lado, lado)
    }

    // ---------- Dibujo ----------

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        val e = escala

        canvas.drawColor(colorPapel)
        dibujarAnillos(canvas, cx, cy, e)
        dibujarNumeros(canvas, cx, cy, e)
        dibujarImpactos(canvas, cx, cy, e)

        if (modoRegistro) {
            val grosor = 4 * densidad
            pinturaBorde.strokeWidth = grosor
            canvas.drawRect(grosor / 2, grosor / 2, width - grosor / 2, height - grosor / 2, pinturaBorde)
        }
    }

    private fun dibujarAnillos(canvas: Canvas, cx: Float, cy: Float, e: Float) {
        val grosorLinea = max(1f, 0.25f * e)
        for (n in 1..10) {
            val radio = tipoDiana.radioAnillo(n) * e
            val enNegro = n >= tipoDiana.primerAnilloNegro

            // Relleno: papel fuera de la zona negra, negro dentro (en carabina, el 10 es un punto blanco)
            pintura.style = Paint.Style.FILL
            pintura.color = when {
                n == 10 && tipoDiana.diezBlanco -> colorLineaClara
                enNegro -> colorNegro
                else -> colorPapel
            }
            canvas.drawCircle(cx, cy, radio, pintura)

            // Línea del anillo: blanca dentro de la zona negra, negra fuera
            pintura.style = Paint.Style.STROKE
            pintura.strokeWidth = grosorLinea
            pintura.color = if (n > tipoDiana.primerAnilloNegro) colorLineaClara else colorNegro
            canvas.drawCircle(cx, cy, radio, pintura)
        }
        if (tipoDiana.diametroDiezInterior > 0f) {
            pintura.color = colorLineaClara
            canvas.drawCircle(cx, cy, tipoDiana.diametroDiezInterior / 2 * e, pintura)
        }
    }

    /** Números del 1 al 8 en las cuatro direcciones, como en la diana de papel. */
    private fun dibujarNumeros(canvas: Canvas, cx: Float, cy: Float, e: Float) {
        val tamano = tipoDiana.anchoAnillo * e * 0.5f
        if (tamano < 9 * densidad / 1.5f) return
        pinturaTexto.textSize = tamano
        val ajusteVertical = (pinturaTexto.descent() + pinturaTexto.ascent()) / 2
        for (n in 1..8) {
            val radioMedio = (tipoDiana.radioAnillo(n) + tipoDiana.radioAnillo(n + 1)) / 2 * e
            pinturaTexto.color = if (n >= tipoDiana.primerAnilloNegro) colorLineaClara else colorNegro
            val texto = n.toString()
            canvas.drawText(texto, cx, cy - radioMedio - ajusteVertical, pinturaTexto)
            canvas.drawText(texto, cx, cy + radioMedio - ajusteVertical, pinturaTexto)
            canvas.drawText(texto, cx - radioMedio, cy - ajusteVertical, pinturaTexto)
            canvas.drawText(texto, cx + radioMedio, cy - ajusteVertical, pinturaTexto)
        }
    }

    private fun dibujarImpactos(canvas: Canvas, cx: Float, cy: Float, e: Float) {
        val radio = max(tipoDiana.calibre / 2 * e, 3 * densidad)
        val colorAnterior = ContextCompat.getColor(context, R.color.impacto_anterior)
        val colorUltimo = ContextCompat.getColor(context, R.color.impacto_ultimo)
        val colorContorno = ContextCompat.getColor(context, R.color.impacto_contorno)

        disparos.forEachIndexed { i, disparo ->
            val px = cx + disparo.x * e
            val py = cy + disparo.y * e
            val esUltimo = interactiva && i == disparos.lastIndex

            pinturaImpacto.style = Paint.Style.FILL
            pinturaImpacto.color = if (esUltimo) colorUltimo else colorAnterior
            canvas.drawCircle(px, py, radio, pinturaImpacto)

            pinturaImpacto.style = Paint.Style.STROKE
            pinturaImpacto.strokeWidth = max(1f, radio * 0.12f)
            pinturaImpacto.color = colorContorno
            canvas.drawCircle(px, py, radio, pinturaImpacto)

            // Número del disparo dentro del impacto, si cabe
            if (radio >= 9 * densidad) {
                pinturaTexto.textSize = radio * 0.95f
                pinturaTexto.color = colorContorno
                val ajuste = (pinturaTexto.descent() + pinturaTexto.ascent()) / 2
                canvas.drawText(disparo.numero.toString(), px, py - ajuste, pinturaTexto)
            }
        }
    }

    // ---------- Toque ----------

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!interactiva) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                toqueX = event.x
                toqueY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                // Solo cuenta como toque si el dedo apenas se ha movido
                if (abs(event.x - toqueX) < 24 * densidad && abs(event.y - toqueY) < 24 * densidad) {
                    if (modoRegistro) {
                        val e = escala
                        alTocarEnRegistro?.invoke((event.x - width / 2f) / e, (event.y - height / 2f) / e)
                    } else {
                        performClick()
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /** Un "clic" (también desde TalkBack) cambia el zoom. */
    override fun performClick(): Boolean {
        super.performClick()
        if (interactiva && !modoRegistro) cambiarZoom(nivelZoom.siguiente())
        return true
    }

    private fun actualizarDescripcion() {
        contentDescription = context.getString(
            R.string.descripcion_diana,
            context.getString(if (tipoDiana == TipoDiana.PISTOLA) R.string.modalidad_pistola else R.string.modalidad_carabina),
            nivelZoom.numero,
            disparos.size
        )
    }

    override fun onDetachedFromWindow() {
        animadorZoom?.cancel()
        super.onDetachedFromWindow()
    }
}
