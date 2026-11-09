package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

/**
 * INICIO: pantalla principal después de iniciar sesión o entrar como invitado.
 * ☰ arriba a la izquierda abre el menú lateral; ⚙ arriba a la derecha despliega los ajustes.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        aplicarMargenesSistema()

        drawer = findViewById(R.id.drawerLayout)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val menuLateral = findViewById<NavigationView>(R.id.navigationView)

        // ☰ abre el menú lateral
        toolbar.setNavigationOnClickListener { drawer.openDrawer(GravityCompat.START) }

        // ⚙ opciones de ajustes
        toolbar.setOnMenuItemClickListener { opcion ->
            when (opcion.itemId) {
                R.id.menuCuenta -> abrir(CuentaActivity::class.java)
                R.id.menuPreferencias -> abrir(PreferenciasActivity::class.java)
                R.id.menuIdioma -> elegirIdioma()
                R.id.menuCerrarSesion -> confirmarCerrarSesion()
            }
            true
        }

        // Opciones del menú lateral
        menuLateral.setCheckedItem(R.id.navInicio)
        menuLateral.setNavigationItemSelectedListener { opcion ->
            when (opcion.itemId) {
                R.id.navNuevoEntrenamiento -> nuevaSesion(competicion = false)
                R.id.navNuevaCompeticion -> nuevaSesion(competicion = true)
                R.id.navMisEntrenos -> abrir(SesionesActivity::class.java)
                R.id.navAmigos -> Toast.makeText(this, R.string.proximamente, Toast.LENGTH_SHORT).show()
                R.id.navGuia -> abrir(GuiaActivity::class.java)
            }
            drawer.closeDrawer(GravityCompat.START)
            opcion.itemId == R.id.navInicio
        }

        // Botones de la pantalla
        findViewById<View>(R.id.btnNuevoEntrenamiento).setOnClickListener { nuevaSesion(competicion = false) }
        findViewById<View>(R.id.btnNuevaCompeticion).setOnClickListener { nuevaSesion(competicion = true) }
        findViewById<View>(R.id.btnMisEntrenos).setOnClickListener { abrir(SesionesActivity::class.java) }
        findViewById<View>(R.id.btnGuia).setOnClickListener { abrir(GuiaActivity::class.java) }

        // Tarjeta con la última sesión
        val ultima = DatosEjemplo.sesiones.first()
        val tarjeta = findViewById<View>(R.id.ultimaSesion)
        tarjeta.findViewById<TextView>(R.id.tvNombre).text = ultima.nombre
        tarjeta.findViewById<TextView>(R.id.tvDetalle).text =
            getString(R.string.sesion_detalle, ultima.fecha, ultima.modalidad)
        tarjeta.findViewById<TextView>(R.id.tvTipo).setText(
            if (ultima.esCompeticion) R.string.tipo_competicion else R.string.tipo_entrenamiento
        )
        tarjeta.findViewById<TextView>(R.id.tvPuntuacion).text = ultima.puntuacion.toString()
        tarjeta.findViewById<TextView>(R.id.tvMaximo).text = getString(R.string.de_maximo, ultima.maximo)
        tarjeta.setOnClickListener {
            startActivity(Intent(this, DetalleSesionActivity::class.java).putExtra(DetalleSesionActivity.EXTRA_POSICION, 0))
        }

        // Si el menú lateral está abierto, "atrás" lo cierra
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun abrir(pantalla: Class<*>) {
        startActivity(Intent(this, pantalla))
    }

    private fun nuevaSesion(competicion: Boolean) {
        startActivity(
            Intent(this, NuevaSesionActivity::class.java)
                .putExtra(NuevaSesionActivity.EXTRA_COMPETICION, competicion)
        )
    }

    private fun elegirIdioma() {
        val idiomas = arrayOf(
            getString(R.string.idioma_castellano),
            getString(R.string.idioma_catalan),
            getString(R.string.idioma_ingles)
        )
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.idioma)
            .setIcon(R.drawable.ic_idioma)
            .setSingleChoiceItems(idiomas, 0, null)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.aceptar, null) // Cambiar el idioma se hará en la parte de lógica
            .show()
    }

    private fun confirmarCerrarSesion() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.cerrar_sesion_pregunta)
            .setNegativeButton(R.string.cancelar, null)
            .setPositiveButton(R.string.cerrar_sesion) { _, _ -> irAlLogin() }
            .show()
    }
}
