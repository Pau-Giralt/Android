package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.shotlog.datos.RepositorioEntrenos
import com.example.shotlog.modelo.Modalidad
import com.example.shotlog.modelo.ModoEntreno
import com.example.shotlog.modelo.comoFecha
import com.example.shotlog.modelo.comoPuntos
import com.example.shotlog.tiro.CalculadoraPuntuacion
import com.example.shotlog.tiro.RegistroEntreno
import com.example.shotlog.ui.SeriesAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

/** Último paso de un entreno: ponerle nombre y notas y guardarlo. */
class GuardarEntrenoActivity : AppCompatActivity() {

    private lateinit var registro: RegistroEntreno

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        setContentView(R.layout.activity_guardar_entreno)
        aplicarMargenesSistema()

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        // Se reconstruye el entreno a partir de las posiciones de los disparos
        val modalidad = Modalidad.valueOf(intent.getStringExtra(RegistroTirosActivity.EXTRA_MODALIDAD) ?: Modalidad.PISTOLA.name)
        val modo = ModoEntreno.valueOf(intent.getStringExtra(RegistroTirosActivity.EXTRA_MODO) ?: ModoEntreno.ENTRENAMIENTO.name)
        val xs = intent.getFloatArrayExtra(RegistroTirosActivity.EXTRA_X) ?: floatArrayOf()
        val ys = intent.getFloatArrayExtra(RegistroTirosActivity.EXTRA_Y) ?: floatArrayOf()
        registro = RegistroEntreno(modalidad, modo)
        xs.indices.forEach { registro.registrar(xs[it], ys[it]) }

        val tipo = registro.tipoDiana
        val ahora = System.currentTimeMillis()
        findViewById<TextView>(R.id.tvTipo).setText(modo.nombre)
        findViewById<TextView>(R.id.tvDetalle).text =
            getString(R.string.sesion_detalle, ahora.comoFecha(), getString(modalidad.nombre))
        findViewById<TextView>(R.id.tvPuntuacion).text = registro.totalDecimas.comoPuntos(tipo.cuentanDecimales)
        findViewById<TextView>(R.id.tvMaximo).text = getString(
            R.string.de_maximo,
            (registro.disparos.size * CalculadoraPuntuacion.maximoPorDisparo(tipo)).comoPuntos(tipo.cuentanDecimales)
        )
        findViewById<RecyclerView>(R.id.rvSeries).adapter = SeriesAdapter(registro.series, tipo)

        val etNombre = findViewById<TextInputEditText>(R.id.etNombreEntreno)
        val etNotas = findViewById<TextInputEditText>(R.id.etNotasEntreno)
        val nombrePorDefecto = getString(R.string.nombre_por_defecto, getString(modo.nombre), ahora.comoFecha())
        if (savedInstanceState == null) etNombre.setText(nombrePorDefecto)

        findViewById<MaterialButton>(R.id.btnGuardar).setOnClickListener {
            val nombre = etNombre.text.toString().trim().ifEmpty { nombrePorDefecto }
            val notas = etNotas.text.toString().trim()
            RepositorioEntrenos.anadir { id -> registro.aEntreno(id, nombre, notas, ahora) }
            Toast.makeText(this, R.string.entreno_anadido, Toast.LENGTH_SHORT).show()
            irAMisEntrenos()
        }
    }

    /** Vuelve a Inicio (cerrando registro y guardar) y abre Mis entrenos encima. */
    private fun irAMisEntrenos() {
        val inicio = Intent(this, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivities(arrayOf(inicio, Intent(this, SesionesActivity::class.java)))
        finish()
    }
}
