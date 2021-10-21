package dtu.opgave.appudvikling.s205454lykkehjulet.Logic

class WordGenerator {

    val Words: List<String> = listOf("politi", "skole", "computer")
    val Categories: List<String> = listOf("Lov og orden", "Læring", "Elektronik")

    var index: Int = 0

    public fun generateWord() : List<String> {
        // https://stackoverflow.com/questions/45685026/how-can-i-get-a-random-number-in-kotlin
        index = (0..2).random()
        return Words[index].split("")
    }

    public fun getCategory() : String {
        return Categories[index]
    }

}