package dtu.opgave.appudvikling.s205454lykkehjulet.Logic

import kotlin.random.Random

class WordGenerator {

    // Liste med ord
    val Words: List<List<String>> = listOf(
        listOf("Politi", "Ambulance", "Ambulanceredder", "Brandmand", "Politibil", "Retssal", "Dommer"),
        listOf("Underviser", "Bog", "Lektier", "Stil", "Skolegård", "Idræt", "Dansk", "Matematik"),
        listOf("Computer", "Mobil", "Apple", "Google", "Android", "Windows", "iPad", "MacBook")
    )

    // Liste med spillets kategorier
    val Categories: List<String> = listOf("Lov og orden", "Uddannelse", "Elektronik")

    // Get og Set - kan kaldes således: WordGenerator.word
    var wordList: List<String> = listOf()
    var word: String = ""
    var category: String = ""


    // Generer tilfældig kategori og udvælg derefter et tilfældigt ord
    fun generateNewWord() {
        val categoryIndex: Int = Random.nextInt(0, Categories.size)
        val wordIndex: Int = Random.nextInt(0, Words[categoryIndex].size)
        category = Categories[categoryIndex]
        word = Words[categoryIndex][wordIndex].lowercase()
        wordList = word.split("")
    }

}