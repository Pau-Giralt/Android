package com.example.shotlog

import android.app.Application
import com.example.shotlog.datos.PreferenciasApp

/** Se ejecuta al abrir la app, antes que cualquier pantalla: aplica el tema guardado. */
class ShotLogApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenciasApp.aplicarTema(PreferenciasApp.temaOscuro(this))
    }
}
