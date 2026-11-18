package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/**
 * Pantalla de inicio animada: aparece la diana, un disparo impacta en el 10
 * (con una onda y una pequeña sacudida) y sale el nombre.
 * Al terminar, el logo sube hasta su sitio en el Login mientras el nombre se desvanece.
 */
class SplashActivity : AppCompatActivity() {

    private val manejador = Handler(Looper.getMainLooper())
    private var haIdoAlLogin = false

    private lateinit var diana: View
    private lateinit var bala: View
    private lateinit var onda: View
    private lateinit var nombre: View

    private val abrirLogin = Runnable { irAlLoginConTransicion() }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Quita la pantalla de arranque del sistema (Android 12+) en cuanto se dibuja la nuestra
        installSplashScreen()
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        setContentView(R.layout.activity_splash)

        val contenedor = findViewById<View>(R.id.contenedorDiana)
        diana = findViewById(R.id.ivDiana)
        bala = findViewById(R.id.vBala)
        onda = findViewById(R.id.vOnda)
        nombre = findViewById(R.id.tvNombreSplash)

        // 1. La diana aparece
        diana.animate().alpha(1f).setDuration(500).start()

        // 2. El disparo llega desde "delante" (grande) hasta el centro
        bala.scaleX = 4f
        bala.scaleY = 4f
        bala.animate()
            .alpha(1f).scaleX(1f).scaleY(1f)
            .setStartDelay(700).setDuration(350)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction { impacto(contenedor) }
            .start()

        // 3. Sale el nombre de la app
        nombre.translationY = 24f
        nombre.animate()
            .alpha(1f).translationY(0f)
            .setStartDelay(1400).setDuration(600)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Si el usuario ha desactivado las animaciones del móvil, no se le hace esperar
        val escala = Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        manejador.postDelayed(abrirLogin, (2400 * escala).toLong().coerceAtLeast(300))
    }

    /** Onda que se abre desde el centro y pequeña sacudida de la diana. */
    private fun impacto(contenedor: View) {
        onda.alpha = 0.9f
        onda.scaleX = 0.3f
        onda.scaleY = 0.3f
        onda.animate().alpha(0f).scaleX(2.4f).scaleY(2.4f)
            .setDuration(600).setInterpolator(DecelerateInterpolator()).start()

        contenedor.animate().scaleX(0.94f).scaleY(0.94f).setDuration(110)
            .withEndAction {
                contenedor.animate().scaleX(1f).scaleY(1f).setDuration(180).start()
            }
            .start()
    }

    /**
     * El logo es un "elemento compartido" con el Login (mismo transitionName):
     * Android lo mueve y lo encoge hasta la posición del logo del Login.
     * A la vez, el nombre y el punto del impacto se desvanecen.
     */
    private fun irAlLoginConTransicion() {
        nombre.animate().alpha(0f).translationY(-16f).setStartDelay(0).setDuration(300).start()
        bala.animate().alpha(0f).setStartDelay(0).setDuration(200).start()

        val opciones = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, diana, getString(R.string.transicion_logo)
        )
        startActivity(Intent(this, LoginActivity::class.java), opciones.toBundle())
        haIdoAlLogin = true
    }

    override fun onStop() {
        super.onStop()
        // Se cierra cuando el Login ya está delante (cerrarla antes cortaría la transición)
        if (haIdoAlLogin) finish()
    }

    override fun onDestroy() {
        // Si se cierra la app durante la animación, no abrimos el Login
        manejador.removeCallbacks(abrirLogin)
        super.onDestroy()
    }
}
