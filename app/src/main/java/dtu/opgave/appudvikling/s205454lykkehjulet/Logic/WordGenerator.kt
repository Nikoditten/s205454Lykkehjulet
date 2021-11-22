package dtu.opgave.appudvikling.s205454lykkehjulet.Logic

import kotlin.random.Random

class WordGenerator {

    // Liste med ord og deres tilhørende kategorier
    val Words: List<String> = listOf("politi", "skole", "computer")
    val Categories: List<String> = listOf("Lov og orden", "Læring", "Elektronik")

    var index: Int = 0

    // Generer tilfældigt ord
    fun generateWord() : List<String> {
        index = Random.nextInt(0, Words.size-1)
        return Words[index].split("")
    }

    // Returnerer det udvalgte ords tilhørende kategori
    fun getCategory() : String {
        return Categories[index]
    }

}