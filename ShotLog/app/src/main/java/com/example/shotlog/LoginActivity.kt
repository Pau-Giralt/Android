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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.shotlog.viewmodel.LoginViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Primera pantalla: iniciar sesión, registrarse o entrar como invitado.
 *
 * Sigue el patrón MVVM: la validación está en [LoginViewModel]. Esta Activity solo
 * le pasa lo que escribe el usuario y observa sus LiveData para actualizar la pantalla.
 */
class LoginActivity : AppCompatActivity() {

    // "by viewModels()" crea el ViewModel la primera vez y devuelve el mismo al girar el móvil
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        prepararTransicionDesdeSplash()
        setContentView(R.layout.activity_login)
        aplicarMargenesSistema()

        findViewById<TextView>(R.id.tvRegistrarse).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        val tilCorreo = findViewById<TextInputLayout>(R.id.tilCorreo)
        val tilContrasena = findViewById<TextInputLayout>(R.id.tilContrasena)
        val etCorreo = findViewById<TextInputEditText>(R.id.etCorreo)
        val etContrasena = findViewById<TextInputEditText>(R.id.etContrasena)

        // Vista → ViewModel: lo que hace el usuario
        etCorreo.doAfterTextChanged { viewModel.correoCambiado(it.toString()) }
        etContrasena.doAfterTextChanged { viewModel.contrasenaCambiada(it.toString()) }
        findViewById<MaterialButton>(R.id.btnIniciarSesion).setOnClickListener {
            viewModel.iniciarSesion(etCorreo.text.toString(), etContrasena.text.toString())
        }
        // Entrar como invitado no necesita validar nada
        findViewById<MaterialButton>(R.id.btnInvitado).setOnClickListener { entrar() }

        // ViewModel → Vista: la pantalla observa los LiveData y se actualiza sola
        viewModel.errorCorreo.observe(this) { error ->
            tilCorreo.error = error?.let { getString(it) }
        }
        viewModel.errorContrasena.observe(this) { error ->
            tilContrasena.error = error?.let { getString(it) }
        }
        viewModel.accesoCorrecto.observe(this) { correcto ->
            if (correcto) {
                viewModel.accesoAtendido()
                entrar()
            }
        }
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
