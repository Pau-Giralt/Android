package com.example.shotlog

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.shotlog.viewmodel.RegistroViewModel
import com.example.shotlog.viewmodel.RegistroViewModel.Campo
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Pantalla de registro.
 *
 * Sigue el patrón MVVM: la validación de los campos está en [RegistroViewModel].
 * Esta Activity solo le pasa lo que escribe el usuario y observa sus LiveData.
 */
class RegistroActivity : AppCompatActivity() {

    // "by viewModels()" crea el ViewModel la primera vez y devuelve el mismo al girar el móvil
    private val viewModel: RegistroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pantallaCompleta()
        setContentView(R.layout.activity_registro)
        aplicarMargenesSistema()

        // Cuadros (para mostrar el error en rojo) y campos de texto (para leer lo escrito)
        val cuadros = mapOf(
            Campo.NOMBRE to findViewById<TextInputLayout>(R.id.tilNombre),
            Campo.CORREO to findViewById<TextInputLayout>(R.id.tilCorreo),
            Campo.CONTRASENA to findViewById<TextInputLayout>(R.id.tilContrasena),
            Campo.REPETIR to findViewById<TextInputLayout>(R.id.tilRepetirContrasena)
        )
        val etNombre = findViewById<TextInputEditText>(R.id.etNombre)
        val etCorreo = findViewById<TextInputEditText>(R.id.etCorreo)
        val etContrasena = findViewById<TextInputEditText>(R.id.etContrasena)
        val etRepetir = findViewById<TextInputEditText>(R.id.etRepetirContrasena)

        // Vista → ViewModel: lo que hace el usuario
        etNombre.doAfterTextChanged { viewModel.campoCambiado(Campo.NOMBRE, it.toString()) }
        etCorreo.doAfterTextChanged { viewModel.campoCambiado(Campo.CORREO, it.toString()) }
        etContrasena.doAfterTextChanged { viewModel.campoCambiado(Campo.CONTRASENA, it.toString()) }
        etRepetir.doAfterTextChanged { viewModel.campoCambiado(Campo.REPETIR, it.toString()) }

        findViewById<MaterialButton>(R.id.btnRegistrarse).setOnClickListener {
            viewModel.registrarse(
                etNombre.text.toString(),
                etCorreo.text.toString(),
                etContrasena.text.toString(),
                etRepetir.text.toString()
            )
        }

        // ViewModel → Vista: cada campo observa su error y se marca en rojo
        viewModel.errorNombre.observe(this) { mostrarError(cuadros.getValue(Campo.NOMBRE), it) }
        viewModel.errorCorreo.observe(this) { mostrarError(cuadros.getValue(Campo.CORREO), it) }
        viewModel.errorContrasena.observe(this) { mostrarError(cuadros.getValue(Campo.CONTRASENA), it) }
        viewModel.errorRepetir.observe(this) { mostrarError(cuadros.getValue(Campo.REPETIR), it) }

        // Lleva el cursor al primer campo con error
        viewModel.primerCampoConError.observe(this) { campo ->
            if (campo != null) {
                cuadros.getValue(campo).editText?.requestFocus()
                viewModel.focoAtendido()
            }
        }

        // Registro correcto: aviso y vuelta al Login
        viewModel.registroCorrecto.observe(this) { correcto ->
            if (correcto) {
                viewModel.registroAtendido()
                Toast.makeText(this, R.string.cuenta_creada, Toast.LENGTH_LONG).show()
                finish()
            }
        }

        // Navegación: volver al login
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        findViewById<TextView>(R.id.tvIrLogin).setOnClickListener { finish() }
    }

    /** Muestra el texto de error en el cuadro, o lo quita si es null. */
    private fun mostrarError(cuadro: TextInputLayout, error: Int?) {
        cuadro.error = error?.let { getString(it) }
    }
}
