package dtu.opgave.appudvikling.s205454lykkehjulet.Logic

import kotlin.random.Random

class WordGenerator {

    // Liste med ord
    val Words: List<List<String>> = listOf(
        listOf("Politi", "Brandmand", "Retssal", "Dommer"),
        listOf("Underviser", "Bog", "Lektier", "Stil", "Idræt", "Dansk"),
        listOf("Computer", "Mobil", "Apple", "Google", "Android", "Windows", "iPad", "MacBook")
    )

    // Liste med spillets kategorier
    val Categories: List<String> = listOf("Lov og orden", "Uddannelse", "Elektronik")

    // Get og Set - kan kaldes således: WordGenerator.word
    var wordList: List<String> = listOf()
    var word: String = ""
    var category: String = ""


    // Generer tilfældig kategori og udvælger derefter et tilfældigt ord
    fun generateNewWord() {
        val categoryIndex: Int = Random.nextInt(0, Categories.size)
        val wordIndex: Int = Random.nextInt(0, Words[categoryIndex].size)
        category = Categories[categoryIndex]
        word = Words[categoryIndex][wordIndex].lowercase()
        wordList = word.split("")
    }

    fun indexOfAll(item: String, charArray: List<String>): List<Int> {
        // En liste indeholdende index for det bogstav brugeren gættede
        // Der kan være flere af samme bogstav... Derfor en liste
        var count: List<Int> = emptyList()
        for (i in 1 until charArray.size - 1) {
            // Tjekker om bogstavet er lig med item
            if (charArray[i] == item) {
                // Tilføjer indexet til listen
                count = count + listOf(i)
            }
        }
        return count
    }

}