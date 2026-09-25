package com.example.myapplication

class BotSeguretat {
    data class Persona(
        val name: String,
        val age: Int,
        val entreteniments: List<String>
    )
    val nom_autoritzat = "Pau"

    fun main() {
        botDeSeguretat()
    }
    fun botDeSeguretat(){

        val jo = Persona(
            name = "Pau",
            age = 20,
            entreteniments = listOf("Dominadas", "Flexiones", "Bicicletas", "Sentadillas", "Squads")
        )

        if (jo.name != nom_autoritzat) {
            println("ERROR: Accés denegat. El nom '${jo.name}' no coincideix amb l'usuari autoritzat.")
            return
        } else {
            println("Benvingut/da, ${jo.name}! Nom verificat amb èxit.\n")
        }

        when (jo.age) {
            in 0..13 -> {
                println("Accés denegat: Ets massa petita (tens ${jo.age} anys).")
                return
            }
            in 14..17 -> {
                println("Atenció: Tens ${jo.age} anys. Sota les regles del bot, necessites permís parental.")
            }
            else -> {
                if (jo.age >= 18) {
                    println("Accés permès! Tens ${jo.age} anys. Pots passar.")
                } else {
                    println("Edat no vàlida.")
                    return
                }
            }
        }
        println("\nEls teus entreteniments que comencen entre la lletra A i la L (per ordre alfabètic):")
        val entretenimentsOrdenats = jo.entreteniments.sorted()


        for (entreteniment in entretenimentsOrdenats) {
            val primeraLletra = entreteniment.trim().firstOrNull()?.uppercaseChar()

            if (primeraLletra != null && primeraLletra in 'A'..'L') {
                println("- $entreteniment")
            }
        }
    }
}