package com.example.shotlog.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * ViewModel de la pantalla de Login (patrón MVVM).
 *
 * Aquí está la lógica: comprobar el correo y la contraseña y decidir si se puede entrar.
 * La [com.example.shotlog.LoginActivity] solo le pasa lo que escribe el usuario y
 * **observa** los LiveData para pintar los errores o pasar a Inicio.
 *
 * Como el ViewModel no se destruye al girar el móvil, los errores siguen en pantalla.
 */
class LoginViewModel : ViewModel() {

    // Cada LiveData tiene una versión "Mutable" privada (solo la cambia el ViewModel)
    // y una pública de solo lectura (la pantalla solo puede observarla).

    private val _errorCorreo = MutableLiveData<Int?>(null)
    /** Error del campo correo (id de `strings.xml`), o `null` si es correcto. */
    val errorCorreo: LiveData<Int?> = _errorCorreo

    private val _errorContrasena = MutableLiveData<Int?>(null)
    /** Error del campo contraseña (id de `strings.xml`), o `null` si es correcto. */
    val errorContrasena: LiveData<Int?> = _errorContrasena

    private val _accesoCorrecto = MutableLiveData(false)
    /** Pasa a `true` cuando los datos son correctos y se puede entrar en la app. */
    val accesoCorrecto: LiveData<Boolean> = _accesoCorrecto

    // Últimos valores validados: al girar el móvil Android vuelve a escribir el texto en los campos,
    // y así se distingue ese caso de un cambio real del usuario.
    private var correoValidado = ""
    private var contrasenaValidada = ""

    /** Botón "Iniciar sesión": valida los dos campos y, si están bien, permite entrar. */
    fun iniciarSesion(correo: String, contrasena: String) {
        correoValidado = correo
        contrasenaValidada = contrasena
        _errorCorreo.value = Validador.errorCorreo(correo)
        _errorContrasena.value = Validador.errorContrasena(contrasena)

        if (_errorCorreo.value == null && _errorContrasena.value == null) {
            // Más adelante aquí se comprobará el usuario con Firebase
            _accesoCorrecto.value = true
        }
    }

    /** Al volver a escribir en un campo, su error desaparece. */
    fun correoCambiado(correo: String) {
        if (correo != correoValidado) _errorCorreo.value = null
    }

    fun contrasenaCambiada(contrasena: String) {
        if (contrasena != contrasenaValidada) _errorContrasena.value = null
    }

    /** La pantalla avisa de que ya ha pasado a Inicio, para no hacerlo dos veces. */
    fun accesoAtendido() {
        _accesoCorrecto.value = false
    }
}
