package com.example.shotlog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel de la pantalla de Registro (patrón MVVM).
 *
 * Valida los cuatro campos del formulario y publica el resultado en LiveData.
 * La [com.example.shotlog.RegistroActivity] observa esos LiveData: marca en rojo los
 * campos con error y, cuando el registro es correcto, avisa y vuelve al Login.
 */
class RegistroViewModel : ViewModel() {

    /** Campos del formulario. Sirven para saber cuál es el primero con error. */
    enum class Campo { NOMBRE, CORREO, CONTRASENA, REPETIR }

    private val _errorNombre = MutableLiveData<Int?>(null)
    val errorNombre: LiveData<Int?> = _errorNombre

    private val _errorCorreo = MutableLiveData<Int?>(null)
    val errorCorreo: LiveData<Int?> = _errorCorreo

    private val _errorContrasena = MutableLiveData<Int?>(null)
    val errorContrasena: LiveData<Int?> = _errorContrasena

    private val _errorRepetir = MutableLiveData<Int?>(null)
    val errorRepetir: LiveData<Int?> = _errorRepetir

    private val _primerCampoConError = MutableLiveData<Campo?>(null)
    /** Campo al que hay que llevar el cursor después de validar (o `null` si no hay errores). */
    val primerCampoConError: LiveData<Campo?> = _primerCampoConError

    private val _registroCorrecto = MutableLiveData(false)
    /** Pasa a `true` cuando todos los campos son correctos. */
    val registroCorrecto: LiveData<Boolean> = _registroCorrecto

    // Últimos valores validados de cada campo (ver campoCambiado)
    private val valoresValidados = mutableMapOf<Campo, String>()

    /** Botón "Registrarse": valida todo el formulario. */
    fun registrarse(nombre: String, correo: String, contrasena: String, repetida: String) {
        valoresValidados[Campo.NOMBRE] = nombre
        valoresValidados[Campo.CORREO] = correo
        valoresValidados[Campo.CONTRASENA] = contrasena
        valoresValidados[Campo.REPETIR] = repetida

        val errores = linkedMapOf(
            Campo.NOMBRE to Validador.errorNombre(nombre),
            Campo.CORREO to Validador.errorCorreo(correo),
            Campo.CONTRASENA to Validador.errorContrasena(contrasena),
            Campo.REPETIR to Validador.errorRepetirContrasena(contrasena, repetida)
        )
        _errorNombre.value = errores[Campo.NOMBRE]
        _errorCorreo.value = errores[Campo.CORREO]
        _errorContrasena.value = errores[Campo.CONTRASENA]
        _errorRepetir.value = errores[Campo.REPETIR]

        val primero = errores.entries.firstOrNull { it.value != null }?.key
        _primerCampoConError.value = primero
        if (primero == null) {
            // Más adelante aquí se creará la cuenta con Firebase
            _registroCorrecto.value = true
        }
    }

    /**
     * Al volver a escribir en un campo, su error desaparece.
     * Si el texto es el mismo que se validó (Android lo vuelve a escribir al girar el móvil), se mantiene.
     */
    fun campoCambiado(campo: Campo, texto: String) {
        if (texto == valoresValidados[campo]) return
        when (campo) {
            Campo.NOMBRE -> _errorNombre.value = null
            Campo.CORREO -> _errorCorreo.value = null
            Campo.CONTRASENA -> _errorContrasena.value = null
            Campo.REPETIR -> _errorRepetir.value = null
        }
    }

    /** La pantalla ya ha movido el cursor al campo con error. */
    fun focoAtendido() {
        _primerCampoConError.value = null
    }

    /** La pantalla ya ha mostrado el aviso y ha vuelto al Login. */
    fun registroAtendido() {
        _registroCorrecto.value = false
    }
}
