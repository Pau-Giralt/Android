package com.example.shotlog.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shotlog.R
import com.example.shotlog.modelo.Entreno
import com.example.shotlog.modelo.comoFecha
import com.example.shotlog.modelo.textoMaximo
import com.example.shotlog.modelo.textoTotal

/**
 * Adapter de la lista de entrenos (Mis entrenos).
 *
 * Es un ListAdapter: al darle una lista nueva con submitList(), DiffUtil compara
 * con la anterior y anima solo lo que ha cambiado (añadido, modificado o eliminado).
 */
class SesionesAdapter(
    private val alPulsar: (Entreno) -> Unit
) : ListAdapter<Entreno, SesionesAdapter.SesionViewHolder>(ComparadorEntrenos) {

    /** Holder: guarda las vistas de una fila para no buscarlas cada vez. */
    class SesionViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        fun mostrar(entreno: Entreno, alPulsar: (Entreno) -> Unit) {
            SesionesAdapter.rellenarTarjeta(itemView, entreno)
            itemView.setOnClickListener { alPulsar(entreno) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SesionViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_sesion, parent, false)
        return SesionViewHolder(vista)
    }

    override fun onBindViewHolder(holder: SesionViewHolder, position: Int) {
        holder.mostrar(getItem(position), alPulsar)
    }

    companion object {
        /** Rellena una tarjeta item_sesion (también la usa Inicio para "Tu última sesión"). */
        fun rellenarTarjeta(vista: View, entreno: Entreno) {
            val contexto = vista.context
            vista.findViewById<TextView>(R.id.tvNombre).text = entreno.nombre
            vista.findViewById<TextView>(R.id.tvDetalle).text = contexto.getString(
                R.string.sesion_detalle, entreno.fecha.comoFecha(), contexto.getString(entreno.modalidad.nombre)
            )
            vista.findViewById<TextView>(R.id.tvTipo).setText(entreno.modo.nombre)
            vista.findViewById<TextView>(R.id.tvPuntuacion).text = entreno.textoTotal()
            vista.findViewById<TextView>(R.id.tvMaximo).text =
                contexto.getString(R.string.de_maximo, entreno.textoMaximo())
        }
    }
}

/** Le dice a DiffUtil cuándo dos entrenos son el mismo (mismo id) y cuándo han cambiado. */
private object ComparadorEntrenos : DiffUtil.ItemCallback<Entreno>() {
    override fun areItemsTheSame(viejo: Entreno, nuevo: Entreno) = viejo.id == nuevo.id
    override fun areContentsTheSame(viejo: Entreno, nuevo: Entreno) = viejo == nuevo
}
