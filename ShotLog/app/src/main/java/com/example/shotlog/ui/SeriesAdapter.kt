package com.example.shotlog.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shotlog.R
import com.example.shotlog.modelo.Serie
import com.example.shotlog.modelo.comoPuntos
import com.example.shotlog.tiro.TipoDiana

/** Adapter de la lista horizontal de series: una mini diana con sus impactos por serie. */
class SeriesAdapter(
    private val series: List<Serie>,
    private val tipoDiana: TipoDiana
) : RecyclerView.Adapter<SeriesAdapter.SerieViewHolder>() {

    class SerieViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val diana: DianaView = vista.findViewById(R.id.dianaSerie)
        val numero: TextView = vista.findViewById(R.id.tvNumSerie)
        val puntos: TextView = vista.findViewById(R.id.tvPuntosSerie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SerieViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_serie, parent, false)
        return SerieViewHolder(vista).apply { diana.interactiva = false }
    }

    override fun onBindViewHolder(holder: SerieViewHolder, position: Int) {
        val serie = series[position]
        val contexto = holder.itemView.context
        val textoPuntos = serie.totalDecimas(tipoDiana).comoPuntos(tipoDiana.cuentanDecimales)

        holder.diana.tipoDiana = tipoDiana
        holder.diana.disparos = serie.disparos
        holder.diana.ajustarZoomADisparos()
        holder.numero.text = contexto.getString(R.string.serie_numero, serie.numero)
        holder.puntos.text = textoPuntos
        holder.itemView.contentDescription = contexto.getString(R.string.serie_descripcion, serie.numero, textoPuntos)
    }

    override fun getItemCount(): Int = series.size
}
