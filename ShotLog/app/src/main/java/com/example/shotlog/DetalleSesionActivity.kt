package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.shotlog.datos.RepositorioEntrenos
import com.example.shotlog.modelo.Entreno
import com.example.shotlog.modelo.comoFecha
import com.example.shotlog.modelo.textoMaximo
import com.example.shotlog.modelo.textoTotal
import com.example.shotlog.ui.SeriesAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/** Detalle de un entreno: resumen, series y notas. Desde aquí se edita o se elimina. */
class DetalleSesionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "id"
    }

    private var idEntreno = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        setContentView(R.layout.activity_detalle_sesion)
        aplicarMargenesSistema()

        idEntreno = intent.getLongExtra(EXTRA_ID, RepositorioEntrenos.ultimo()?.id ?: 0L)
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnEditar).setOnClickListener {
            startActivity(
                Intent(this, EditarSesionActivity::class.java)
                    .putExtra(EditarSesionActivity.EXTRA_ID, idEntreno)
            )
        }
        findViewById<MaterialButton>(R.id.btnEliminar).setOnClickListener { confirmarEliminar() }
    }

    /** Se rellena en onResume para ver los cambios al volver de Editar. */
    override fun onResume() {
        super.onResume()
        val entreno = RepositorioEntrenos.obtener(idEntreno)
        if (entreno == null) finish() else mostrar(entreno)
    }

    private fun mostrar(entreno: Entreno) {
        findViewById<MaterialToolbar>(R.id.toolbar).title = entreno.nombre
        findViewById<TextView>(R.id.tvTipo).setText(entreno.modo.nombre)
        findViewById<TextView>(R.id.tvDetalle).text =
            getString(R.string.sesion_detalle, entreno.fecha.comoFecha(), getString(entreno.modalidad.nombre))
        findViewById<TextView>(R.id.tvPuntuacion).text = entreno.textoTotal()
        findViewById<TextView>(R.id.tvMaximo).text = getString(R.string.de_maximo, entreno.textoMaximo())
        findViewById<TextView>(R.id.tvNotas).text = entreno.notas.ifEmpty { getString(R.string.sin_notas) }
        findViewById<RecyclerView>(R.id.rvSeries).adapter =
            SeriesAdapter(entreno.series, entreno.modalidad.tipoDiana)
    }

    private fun confirmarEliminar() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.eliminar_titulo)
            .setMessage(R.string.eliminar_mensaje)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.eliminar) { _, _ ->
                RepositorioEntrenos.eliminar(idEntreno)
                Toast.makeText(this, R.string.entreno_eliminado, Toast.LENGTH_SHORT).show()
                finish()
            }
            .show()
    }
}
