package com.example.shotlog

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.shotlog.modelo.Modalidad
import com.example.shotlog.modelo.ModoEntreno
import com.example.shotlog.modelo.comoPuntos
import com.example.shotlog.modelo.textoPuntuacion
import com.example.shotlog.tiro.NivelZoom
import com.example.shotlog.tiro.RegistroEntreno
import com.example.shotlog.ui.DianaView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Pantalla de registro de disparos (estilo SIUS). Clara por defecto y oscura con el modo oscuro.
 * La lógica (puntuación, series, totales) está en [RegistroEntreno]; aquí solo se muestra.
 */
class RegistroTirosActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODALIDAD = "modalidad"
        const val EXTRA_MODO = "modo"
        const val EXTRA_MAXIMO = "maximo"
        const val EXTRA_X = "x"
        const val EXTRA_Y = "y"

        private const val TAG = "ShotLog"
        private const val ESTADO_ZOOM = "zoom"
        private const val ESTADO_REGISTRO = "modoRegistro"
        private const val DURACION_AVISO_MS = 3000L
    }

    private lateinit var registro: RegistroEntreno

    private lateinit var diana: DianaView
    private lateinit var btnRegistrar: MaterialButton
    private lateinit var tvZoom: TextView
    private lateinit var tvAvisoRegistro: TextView
    private lateinit var valorDisparo: TextView
    private lateinit var valorUltimo: TextView
    private lateinit var etiquetaSerie: TextView
    private lateinit var valorSerie: TextView
    private lateinit var tvEtiquetaTotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var scrollSeries: View
    private lateinit var contenedorSeries: LinearLayout

    private var modoRegistro = false
    private val ocultarAviso = Runnable {
        tvAvisoRegistro.animate().alpha(0f).setDuration(400)
            .withEndAction { tvAvisoRegistro.visibility = View.GONE }
            .start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        setContentView(R.layout.activity_registro_tiros)
        aplicarMargenesSistema()

        val modalidad = Modalidad.valueOf(intent.getStringExtra(EXTRA_MODALIDAD) ?: Modalidad.PISTOLA.name)
        val modo = ModoEntreno.valueOf(intent.getStringExtra(EXTRA_MODO) ?: ModoEntreno.ENTRENAMIENTO.name)
        registro = RegistroEntreno(modalidad, modo, intent.getIntExtra(EXTRA_MAXIMO, 0))

        enlazarVistas()

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.registro_tiros_titulo, getString(modalidad.nombre), getString(modo.nombre))
        toolbar.setNavigationOnClickListener { salir() }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.accionDeshacer) deshacer()
            true
        }

        diana.tipoDiana = modalidad.tipoDiana
        diana.alCambiarZoom = { mostrarZoom(it) }
        diana.alTocarEnRegistro = { x, y -> registrarDisparo(x, y) }

        btnRegistrar.setOnClickListener { cambiarModoRegistro(!modoRegistro) }
        findViewById<MaterialButton>(R.id.btnFinalizar).setOnClickListener { finalizar() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = salir()
        })

        // Al girar la pantalla se recuperan los disparos, el zoom y el modo registro
        if (savedInstanceState != null) restaurar(savedInstanceState)
        mostrarZoom(diana.nivelZoom)
        cambiarModoRegistro(modoRegistro)
        actualizarPantalla()
    }

    private fun enlazarVistas() {
        diana = findViewById(R.id.diana)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        tvZoom = findViewById(R.id.tvZoom)
        tvAvisoRegistro = findViewById(R.id.tvAvisoRegistro)
        tvEtiquetaTotal = findViewById(R.id.tvEtiquetaTotal)
        tvTotal = findViewById(R.id.tvTotal)
        scrollSeries = findViewById(R.id.scrollSeries)
        contenedorSeries = findViewById(R.id.contenedorSeries)

        val datoDisparo = findViewById<View>(R.id.datoDisparo)
        val datoUltimo = findViewById<View>(R.id.datoUltimo)
        val datoSerie = findViewById<View>(R.id.datoSerie)
        datoDisparo.findViewById<TextView>(R.id.tvEtiqueta).setText(R.string.dato_disparo)
        datoUltimo.findViewById<TextView>(R.id.tvEtiqueta).setText(R.string.dato_ultimo)
        valorDisparo = datoDisparo.findViewById(R.id.tvValor)
        valorUltimo = datoUltimo.findViewById(R.id.tvValor)
        etiquetaSerie = datoSerie.findViewById(R.id.tvEtiqueta)
        valorSerie = datoSerie.findViewById(R.id.tvValor)
        valorUltimo.accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
    }

    // ---------- Acciones ----------

    private fun registrarDisparo(x: Float, y: Float) {
        val disparo = registro.registrar(x, y)
        Log.d(TAG, "Disparo ${disparo.numero}: ${disparo.textoPuntuacion()} (x=$x mm, y=$y mm)")
        actualizarPantalla()

        if (registro.terminado) {
            Toast.makeText(this, getString(R.string.competicion_terminada, registro.disparos.size), Toast.LENGTH_LONG).show()
            cambiarModoRegistro(false)
            abrirGuardar()
        }
    }

    private fun deshacer() {
        if (registro.deshacer() == null) {
            Toast.makeText(this, R.string.nada_que_deshacer, Toast.LENGTH_SHORT).show()
        } else {
            actualizarPantalla()
        }
    }

    private fun cambiarModoRegistro(activo: Boolean) {
        modoRegistro = activo
        diana.modoRegistro = activo
        mostrarAvisoRegistro(activo)
        btnRegistrar.setText(if (activo) R.string.registrando else R.string.registrar_tiro)
        val fondo = if (activo) R.color.sius_registro else R.color.verde_medio
        val texto = if (activo) R.color.negro_fijo else R.color.beige
        btnRegistrar.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, fondo))
        btnRegistrar.setTextColor(ContextCompat.getColor(this, texto))
    }

    /** El aviso aparece al activar el modo registro y se desvanece solo a los pocos segundos. */
    private fun mostrarAvisoRegistro(activo: Boolean) {
        tvAvisoRegistro.removeCallbacks(ocultarAviso)
        tvAvisoRegistro.animate().cancel()
        if (activo) {
            tvAvisoRegistro.alpha = 1f
            tvAvisoRegistro.visibility = View.VISIBLE
            tvAvisoRegistro.postDelayed(ocultarAviso, DURACION_AVISO_MS)
        } else {
            tvAvisoRegistro.visibility = View.GONE
        }
    }

    private fun finalizar() {
        if (registro.disparos.isEmpty()) {
            Toast.makeText(this, R.string.sin_disparos, Toast.LENGTH_SHORT).show()
            return
        }
        abrirGuardar()
    }

    /** La pantalla de guardar recibe los disparos (en mm) y vuelve a calcularlo todo. */
    private fun abrirGuardar() {
        val intent = Intent(this, GuardarEntrenoActivity::class.java)
            .putExtra(EXTRA_MODALIDAD, registro.modalidad.name)
            .putExtra(EXTRA_MODO, registro.modo.name)
            .putExtra(EXTRA_X, registro.disparos.map { it.x }.toFloatArray())
            .putExtra(EXTRA_Y, registro.disparos.map { it.y }.toFloatArray())
        startActivity(intent)
    }

    private fun salir() {
        if (registro.disparos.isEmpty()) {
            finish()
            return
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.salir_titulo)
            .setMessage(R.string.salir_mensaje)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.salir) { _, _ -> finish() }
            .show()
    }

    // ---------- Pantalla ----------

    private fun actualizarPantalla() {
        val decimales = registro.tipoDiana.cuentanDecimales

        // En la diana solo se ven los disparos de la serie actual (el 11 empieza una nueva)
        diana.disparos = registro.disparosSerieActual

        valorDisparo.text = getString(R.string.disparo_de_serie, registro.disparosSerieActual.size)
        valorUltimo.text = registro.ultimo?.textoPuntuacion() ?: getString(R.string.sin_valor)
        etiquetaSerie.text = getString(R.string.dato_serie, registro.numeroSerieActual)
        valorSerie.text = registro.totalSerieActualDecimas.comoPuntos(decimales)

        tvEtiquetaTotal.text = getString(R.string.total_disparos, registro.disparos.size)
        tvTotal.text = registro.totalDecimas.comoPuntos(decimales)

        // En competición, además, los puntos de cada serie terminada
        val series = registro.seriesCompletas
        scrollSeries.visibility =
            if (registro.modo == ModoEntreno.COMPETICION && series.isNotEmpty()) View.VISIBLE else View.GONE
        contenedorSeries.removeAllViews()
        series.forEach { serie ->
            val chip = layoutInflater.inflate(R.layout.vista_serie_sius, contenedorSeries, false) as TextView
            chip.text = getString(
                R.string.serie_corta, serie.numero, serie.totalDecimas(registro.tipoDiana).comoPuntos(decimales)
            )
            contenedorSeries.addView(chip)
        }
    }

    private fun mostrarZoom(nivel: NivelZoom) {
        val texto = getString(R.string.zoom_nivel, nivel.numero)
        tvZoom.text = if (nivel == NivelZoom.COMPLETO) "$texto · ${getString(R.string.zoom_ayuda)}" else texto
    }

    override fun onDestroy() {
        tvAvisoRegistro.removeCallbacks(ocultarAviso)
        super.onDestroy()
    }

    // ---------- Girar la pantalla ----------

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloatArray(EXTRA_X, registro.disparos.map { it.x }.toFloatArray())
        outState.putFloatArray(EXTRA_Y, registro.disparos.map { it.y }.toFloatArray())
        outState.putString(ESTADO_ZOOM, diana.nivelZoom.name)
        outState.putBoolean(ESTADO_REGISTRO, modoRegistro)
    }

    private fun restaurar(estado: Bundle) {
        val xs = estado.getFloatArray(EXTRA_X) ?: floatArrayOf()
        val ys = estado.getFloatArray(EXTRA_Y) ?: floatArrayOf()
        xs.indices.forEach { registro.registrar(xs[it], ys[it]) }
        estado.getString(ESTADO_ZOOM)?.let { diana.cambiarZoom(NivelZoom.valueOf(it), animar = false) }
        modoRegistro = estado.getBoolean(ESTADO_REGISTRO)
    }
}
