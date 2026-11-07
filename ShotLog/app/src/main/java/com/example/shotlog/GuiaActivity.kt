package com.example.shotlog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/** Guía de inicio: 4 páginas que se pasan deslizando. */
class GuiaActivity : AppCompatActivity() {

    private val paginas = listOf(
        PaginaGuia(R.drawable.ic_diana, R.string.guia_titulo_1, R.string.guia_texto_1),
        PaginaGuia(R.drawable.ic_camara, R.string.guia_titulo_2, R.string.guia_texto_2),
        PaginaGuia(R.drawable.ic_notas, R.string.guia_titulo_3, R.string.guia_texto_3),
        PaginaGuia(R.drawable.ic_progreso, R.string.guia_titulo_4, R.string.guia_texto_4)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guia)
        aplicarMargenesSistema()

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerGuia)
        val puntos = findViewById<TabLayout>(R.id.tabPuntos)
        val btnSiguiente = findViewById<MaterialButton>(R.id.btnSiguiente)
        val btnSaltar = findViewById<MaterialButton>(R.id.btnSaltar)

        viewPager.adapter = GuiaAdapter(paginas)

        // Un punto por página (los puntos solo indican, no se pulsan)
        TabLayoutMediator(puntos, viewPager) { tab, posicion ->
            tab.contentDescription = getString(R.string.guia_pagina, posicion + 1, paginas.size)
            tab.view.isClickable = false
        }.attach()

        // En la última página el botón pasa a "Empezar" y se oculta "Saltar"
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val esUltima = position == paginas.lastIndex
                btnSiguiente.setText(if (esUltima) R.string.guia_empezar else R.string.guia_siguiente)
                btnSaltar.visibility = if (esUltima) View.INVISIBLE else View.VISIBLE
            }
        })

        btnSiguiente.setOnClickListener {
            if (viewPager.currentItem < paginas.lastIndex) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                finish()
            }
        }
        btnSaltar.setOnClickListener { finish() }
    }
}

data class PaginaGuia(
    @param:DrawableRes val icono: Int,
    @param:StringRes val titulo: Int,
    @param:StringRes val texto: Int
)

class GuiaAdapter(private val paginas: List<PaginaGuia>) :
    RecyclerView.Adapter<GuiaAdapter.PaginaViewHolder>() {

    class PaginaViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val icono: ImageView = vista.findViewById(R.id.ivIconoGuia)
        val titulo: TextView = vista.findViewById(R.id.tvTituloGuia)
        val texto: TextView = vista.findViewById(R.id.tvTextoGuia)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaginaViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_guia, parent, false)
        return PaginaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PaginaViewHolder, position: Int) {
        val pagina = paginas[position]
        holder.icono.setImageResource(pagina.icono)
        holder.titulo.setText(pagina.titulo)
        holder.texto.setText(pagina.texto)
    }

    override fun getItemCount(): Int = paginas.size
}
