package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.shotlog.datos.RepositorioEntrenos
import com.example.shotlog.ui.SesionesAdapter
import com.google.android.material.appbar.MaterialToolbar

/** Mis entrenos: lista de entrenos guardados. */
class   SesionesActivity : AppCompatActivity() {

    private val adapter = SesionesAdapter { entreno ->
        startActivity(
            Intent(this, DetalleSesionActivity::class.java)
                .putExtra(DetalleSesionActivity.EXTRA_ID, entreno.id)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        setContentView(R.layout.activity_sesiones)
        aplicarMargenesSistema()

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        findViewById<RecyclerView>(R.id.rvSesiones).adapter = adapter

        // Añadir: abre la pantalla de nuevo entreno
        findViewById<View>(R.id.fabNuevoEntreno).setOnClickListener {
            startActivity(Intent(this, NuevaSesionActivity::class.java))
        }
    }

    /** Al volver de añadir, editar o eliminar, la lista se actualiza con animación. */
    override fun onResume() {
        super.onResume()
        adapter.submitList(RepositorioEntrenos.todos())
    }
}
