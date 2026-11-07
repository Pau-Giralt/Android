package com.example.shotlog

import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegistroActivity : AppCompatActivity() {

    // Cuadros (para mostrar el error en rojo) y campos de texto (para leer lo escrito)
    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilContrasena: TextInputLayout
    private lateinit var tilRepetir: TextInputLayout
    private lateinit var etNombre: TextInputEditText
    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var etRepetir: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        aplicarMargenesSistema()

        tilNombre = findViewById(R.id.tilNombre)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilContrasena = findViewById(R.id.tilContrasena)
        tilRepetir = findViewById(R.id.tilRepetirContrasena)
        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etContrasena = findViewById(R.id.etContrasena)
        etRepetir = findViewById(R.id.etRepetirContrasena)

        // En cuanto el usuario vuelve a escribir en un campo, se quita su error
        etNombre.doAfterTextChanged { tilNombre.error = null }
        etCorreo.doAfterTextChanged { tilCorreo.error = null }
        etContrasena.doAfterTextChanged { tilContrasena.error = null }
        etRepetir.doAfterTextChanged { tilRepetir.error = null }

        findViewById<MaterialButton>(R.id.btnRegistrarse).setOnClickListener { registrarse() }

        // Navegación: volver al login
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }
        findViewById<TextView>(R.id.tvIrLogin).setOnClickListener { finish() }
    }

    private fun registrarse() {
        if (!validarCampos()) return

        // TODO: aquí se creará la cuenta en Firebase Authentication.
        // De momento solo avisamos y volvemos al login.
        Toast.makeText(this, R.string.cuenta_creada, Toast.LENGTH_LONG).show()
        finish()
    }

    /** Comprueba todos los campos, marca en rojo los incorrectos y devuelve true si todo está bien. */
    private fun validarCampos(): Boolean {
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString()
        val repetir = etRepetir.text.toString()

        var todoCorrecto = true

        // Nombre de usuario
        tilNombre.error = when {
            nombre.isEmpty() -> getString(R.string.error_campo_vacio)
            nombre.length < 3 -> getString(R.string.error_nombre_corto)
            else -> null
        }
        if (tilNombre.error != null) todoCorrecto = false

        // Correo
        tilCorreo.error = when {
            correo.isEmpty() -> getString(R.string.error_campo_vacio)
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> getString(R.string.error_correo_invalido)
            else -> null
        }
        if (tilCorreo.error != null) todoCorrecto = false

        // Contraseña (Firebase pide mínimo 6 caracteres)
        tilContrasena.error = when {
            contrasena.isEmpty() -> getString(R.string.error_campo_vacio)
            contrasena.length < 6 -> getString(R.string.error_contrasena_corta)
            else -> null
        }
        if (tilContrasena.error != null) todoCorrecto = false

        // Repetir contraseña
        tilRepetir.error = when {
            repetir.isEmpty() -> getString(R.string.error_campo_vacio)
            repetir != contrasena -> getString(R.string.error_contrasenas_distintas)
            else -> null
        }
        if (tilRepetir.error != null) todoCorrecto = false

        // Lleva el cursor al primer campo con error
        listOf(tilNombre, tilCorreo, tilContrasena, tilRepetir)
            .firstOrNull { it.error != null }
            ?.editText?.requestFocus()

        return todoCorrecto
    }
}
