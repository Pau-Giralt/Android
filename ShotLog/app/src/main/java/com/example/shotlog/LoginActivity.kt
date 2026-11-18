package com.example.shotlog

import android.content.Intent
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.Fade
import android.transition.TransitionSet
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

/** Primera pantalla: iniciar sesión, registrarse o entrar como invitado. */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        prepararTransicionDesdeSplash()
        setContentView(R.layout.activity_login)
        aplicarMargenesSistema()

        findViewById<TextView>(R.id.tvRegistrarse).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        // De momento se entra directamente. La comprobación del usuario se hará con Firebase.
        findViewById<MaterialButton>(R.id.btnIniciarSesion).setOnClickListener { entrar() }
        findViewById<MaterialButton>(R.id.btnInvitado).setOnClickListener { entrar() }
    }

    /**
     * Cuando se llega desde el Splash: el logo viaja desde el centro hasta su sitio
     * (moviéndose y encogiéndose) y el resto de la pantalla aparece con un fundido.
     */
    private fun prepararTransicionDesdeSplash() {
        window.sharedElementEnterTransition = TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            addTransition(ChangeImageTransform())
            setDuration(650)
            setInterpolator(DecelerateInterpolator(1.5f))
        }
        window.enterTransition = Fade().apply {
            setDuration(450)
            setStartDelay(250)
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
        }
    }

    /** Va a Inicio y cierra el Login para que "atrás" no vuelva aquí. */
    private fun entrar() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
