package com.example.shotlog.datos

import android.util.Log
import com.example.shotlog.DatosEjemplo
import com.example.shotlog.modelo.Entreno

/**
 * Guarda los entrenos mientras la app está abierta (en memoria).
 * En la actividad 09 se cambiará por una base de datos sin tocar el resto de la app.
 */
object RepositorioEntrenos {

    private const val TAG = "ShotLog"

    private val entrenos: MutableList<Entreno> = DatosEjemplo.entrenos().toMutableList()
    private var ultimoId: Long = entrenos.maxOfOrNull { it.id } ?: 0L

    /** Del más reciente al más antiguo. */
    fun todos(): List<Entreno> = entrenos.toList()

    fun obtener(id: Long): Entreno? = entrenos.find { it.id == id }

    fun ultimo(): Entreno? = entrenos.firstOrNull()

    /** Recibe una función que crea el entreno con el id nuevo. */
    fun anadir(crear: (Long) -> Entreno): Entreno {
        ultimoId++
        val entreno = crear(ultimoId)
        entrenos.add(0, entreno)
        Log.d(TAG, "Entreno añadido: ${entreno.id} - ${entreno.nombre} (${entreno.disparos.size} disparos)")
        return entreno
    }

    fun modificar(id: Long, nombre: String, notas: String): Boolean {
        val posicion = entrenos.indexOfFirst { it.id == id }
        if (posicion == -1) return false
        entrenos[posicion] = entrenos[posicion].copy(nombre = nombre, notas = notas)
        Log.d(TAG, "Entreno modificado: $id - $nombre")
        return true
    }

    fun eliminar(id: Long): Boolean {
        val eliminado = entrenos.removeAll { it.id == id }
        if (eliminado) Log.d(TAG, "Entreno eliminado: $id")
        return eliminado
    }
}
