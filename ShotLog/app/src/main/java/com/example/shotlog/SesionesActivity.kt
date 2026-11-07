package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

/** Lista de sesiones guardadas (apartado Dianas). */
class SesionesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sesiones)
        aplicarMargenesSistema()

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        // De momento se muestran datos de ejemplo. Al tocar una sesión se abre la pantalla de edición.
        findViewById<RecyclerView>(R.id.rvSesiones).adapter =
            SesionesAdapter(DatosEjemplo.sesiones) { posicion ->
                val intent = Intent(this, EditarSesionActivity::class.java)
                intent.putExtra(EditarSesionActivity.EXTRA_POSICION, posicion)
                startActivity(intent)
            }
    }
}

class SesionesAdapter(
    private val sesiones: List<Sesion>,
    private val alPulsar: (Int) -> Unit
) : RecyclerView.Adapter<SesionesAdapter.SesionViewHolder>() {

    class SesionViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val nombre: TextView = vista.findViewById(R.id.tvNombre)
        val detalle: TextView = vista.findViewById(R.id.tvDetalle)
        val tipo: TextView = vista.findViewById(R.id.tvTipo)
        val puntuacion: TextView = vista.findViewById(R.id.tvPuntuacion)
        val maximo: TextView = vista.findViewById(R.id.tvMaximo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SesionViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_sesion, parent, false)
        return SesionViewHolder(vista)
    }

    override fun onBindViewHolder(holder: SesionViewHolder, position: Int) {
        val sesion = sesiones[position]
        val contexto = holder.itemView.context
        holder.nombre.text = sesion.nombre
        holder.detalle.text = contexto.getString(R.string.sesion_detalle, sesion.fecha, sesion.modalidad)
        holder.tipo.setText(if (sesion.esCompeticion) R.string.tipo_competicion else R.string.tipo_entrenamiento)
        holder.puntuacion.text = sesion.puntuacion.toString()
        holder.maximo.text = contexto.getString(R.string.de_maximo, sesion.maximo)
        holder.itemView.setOnClickListener { alPulsar(holder.bindingAdapterPosition) }
    }

    override fun getItemCount(): Int = sesiones.size
}
