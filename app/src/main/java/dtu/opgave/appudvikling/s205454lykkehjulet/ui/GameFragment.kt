package dtu.opgave.appudvikling.s205454lykkehjulet.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dtu.opgave.appudvikling.s205454lykkehjulet.Adapter.CharAdapter
import dtu.opgave.appudvikling.s205454lykkehjulet.Adapter.LifeAdapter
import dtu.opgave.appudvikling.s205454lykkehjulet.Logic.Phase
import dtu.opgave.appudvikling.s205454lykkehjulet.Logic.Player
import dtu.opgave.appudvikling.s205454lykkehjulet.Logic.Rewards
import dtu.opgave.appudvikling.s205454lykkehjulet.Logic.WordGenerator
import dtu.opgave.appudvikling.s205454lykkehjulet.Model.CharModel
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class GameFragment : Fragment() {

    // Global lists
    private var charList = ArrayList<CharModel>()
    private var guessedChars = ArrayList<String>()
    private var charArray: List<String> = emptyList()

    // Word Generator class til at generere ord og kategori
    private val wordGenerator: WordGenerator = WordGenerator()

    // Reward klasse
    private val rewards: Rewards = Rewards()

    // Game phase klasse
    private var phase: Phase = Phase.WHEEL

    // De point som man potientelt kan vinde,
    // hvis man gætter rigtigt
    private var tempPointReward: Int = 0

    // Til at holde styr på, hvornår hele ordet er gættet
    private var countCorrectGuess: Int = 0

    // Spiller objekt
    private lateinit var player: Player

    // TextViews
    private lateinit var pointsTxt: TextView
    private lateinit var guessedTxt: TextView
    private lateinit var categoryTxt: TextView
    private lateinit var rewardTxt: TextView

    // EditText
    private lateinit var guessEt: EditText

    // Button
    private lateinit var actionBtn: Button

    // Recyclerviews
    private lateinit var wordRv: RecyclerView
    private lateinit var lifeRv: RecyclerView

    // LinearLayoutManager for recyclerviews
    private lateinit var lifeLayoutManager: LinearLayoutManager
    private lateinit var charLayoutManager: LinearLayoutManager

    // Adapter for wordRv
    private lateinit var charAdapter: CharAdapter

    // SharedPreferences fundet på:
    // https://camposha.info/android-examples/android-sharedpreferences/
    private lateinit var shared: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize view
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        // Initialize views
        initViews(view)
        // Initialize objects
        initObjects(view)
        // Initialize game logics
        initGameLogic()
        // Initialize recyclerview
        initRecyclerViews(view)

        // Knappen bruges til og eksekvere lykkehjuls- og gætte fasen
        actionBtn.setOnClickListener {
            if (phase == Phase.WHEEL) {
                executeWheelPhase(view)
            } else {
                executeGuessPhase(view)
            }
        }

        return view
    }

    private fun executeGuessPhase(view: View) {
        // Henter spillerens gæt
        val guess: String = guessEt.text.toString().lowercase()
        // Check om guess er tomt
        if (guess.isEmpty()){
            // Fortæller brugeren, at man ikke kan gætte blankt
            showToast(view, getString(R.string.empty_guess))
            // Returnerer, for at lade brugeren prøve igen,
            // uden det får betydning for spillets phase
            return
        }
        // Tjekker om mit array med ordets bogstaver indeholder brugerens gæt
        // og om brugeren allerede har gættet på bogstavet
        if (charArray.contains(guess) && !guessedChars.contains(guess)) {
            // Viser en toast med korrekt gæt
            showToast(view, getString(R.string.correct))
            // Tilføjer gættet til en liste med gættede bogstaver
            guessedChars.add(guess)
            // Opdater TextView
            guessedTxt.text = guessedChars.toString()
            // Fjerner bogstavet fra EditText feltet
            guessEt.setText("")

            // Tæller hvor mange gange bogstaver er i ordet
            // charArray indeholder 2 tomme mellemrum ([, s, k, o, l, e, ]), derfor fratrækkes to fra charArray.size
            // https://stackoverflow.com/questions/49846295/kotlin-count-occurrences-of-chararray-in-string
            val count: Int = charArray.count { guess.contains(it) } - 2
            // Tilføjer point til spilleren
            player.point += tempPointReward * count
            // Opdatere hvor mange bogstaver der er tilbage som man kan gætte på
            countCorrectGuess -= count

            // Finder indexerne for det gættede bogstav
            val index: List<Int> = wordGenerator.indexOfAll(guess, charArray)

            // Opdatere charList og gør det gættede bogstav synligt
            for (i in index) {
                charList[i - 1] = CharModel(charArray[i], true)
                // Opdatere recyclerviewet
                charAdapter.notifyItemChanged(i - 1)
            }

            // Hvis alle ordets bogstaver er gættet, afsluttes spillet som vundet
            if (countCorrectGuess == 0) {
                gameOver(view.context, won = true)
            }

        } else { // Hvis brugeren allerede havde gættet på bogstavet eller brugeren gættede forkert

            // Hvis brugeren ikke allerede har gættet på bogstvaet
            if (!guessedChars.contains(guess)) {
                // Vis toast med forkert gæt
                showToast(view, getString(R.string.wrong))
                // Vis spillerens liv er mindre end 2 (Lig med 1) fratrækkes et point
                // og spillet afsluttes som tabt
                if (player.life < 2) {
                    player.life -= 1
                    gameOver(view.context, won = false)
                }
                // Bogstavet tilføjes til listen med gættede bogstaver
                guessedChars.add(guess)
                // Opdatere TextViewet
                guessedTxt.text = guessedChars.toString()
                // Fjerner bogstavet fra EditText feltet
                guessEt.setText("")
                // Spilleren fratrækkes et point
                player.life -= 1
            } else {
                // Hvis brugeren allerede har gættet på bogstavet
                    // Fjerene bogstavet fra EditText feltet, spilleren bliver fratrukket 1 liv
                        // og en toast med du har allerede gættet på bogstavet vises
                guessEt.setText("")
                player.life -= 1
                // Vis spillerens liv er mindre end 2 (Lig med 1) fratrækkes et point
                // og spillet afsluttes som tabt
                if (player.life == 0) {
                    gameOver(view.context, won = false)
                }
                showToast(view, getString(R.string.already_guessed))
            }
        }
        // Opdate lifeRv
        updateLifeRV()
        // Sætter temp variablen til 0
        tempPointReward = 0
        // Opdatere spilleren point i UI
        pointsTxt.text = player.point.toString()
        // Skifter spillets phase
        togglePhase()
    }

    private fun executeWheelPhase(view: View) {
        // Generere ny reward
        val reward: Enum<Rewards.Reward> = rewards.getReward()
        // Hvis rewardets point er lig med -1
        // Er reward en af følgende:
        // - Bankrupt
        // - Extra turn
        // - Skip turn
        if (Rewards.Reward.values()[reward.ordinal].points == -1) {
            // Tjekker hvilket reward der er genereret
            // When kommando fundet på
            // https://kotlinlang.org/docs/control-flow.html#when-expression
            when (reward.name) {
                // Hvis reward er bankrupt, sættes spillerens point til 0
                Rewards.Reward.BANKRUPT.name -> {
                    rewardTxt.text = getString(R.string.bankrupt)
                    player.point = 0
                    pointsTxt.text = player.point.toString()
                }
                // Hvis reward er Extra turn, får spiller et liv mere
                Rewards.Reward.EXTRA_TURN.name -> {
                    player.life += 1
                    rewardTxt.text = getString(R.string.extra_life)
                }
                // Hvis reward er skip turn, fjernes fratrækkes et liv fra spilleren
                Rewards.Reward.SKIP_TURN.name -> {
                    player.life -= 1
                    rewardTxt.text = getString(R.string.skip_turn)
                    // Hvis spillerens liv bliver 0, afsluttes spillet som tabt
                    if (player.life == 0) {
                        gameOver(view.context, won = false)
                    }
                }
            }
            // Recyclerview med liv opdateres
            updateLifeRV()
        } else {
            // Hvis reward er et point reward til værdien af point til en temp variabel
            tempPointReward = Rewards.Reward.values()[reward.ordinal].points
            rewardTxt.text = getString(R.string.point_reward, tempPointReward)
            // Skifter spillets phase
            togglePhase()
        }
    }

    private fun initRecyclerViews(view: View) {
        // Horizontalt recyclerview fundet på
        // https://www.tutorialspoint.com/how-to-create-horizontal-listview-in-android-using-kotlin
        charLayoutManager = LinearLayoutManager(view.context)
        // Recyclerviewet skal have et horizontalt scroll direction
        charLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        // Definerer et adapter objekt til wordRv
        charAdapter = CharAdapter(charList)

        // Tildeler LinearLayoutManager og charAdapter til mit wordRv
        wordRv.layoutManager = charLayoutManager
        wordRv.adapter = charAdapter

        // Definerer LinearLayoutManager til lifeRv
        lifeLayoutManager = LinearLayoutManager(view.context)
        // Horizontalt scroll view
        lifeLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        // Tildeler LinearLayoutManager og charAdapter til mit lifeRv
        lifeRv.layoutManager = lifeLayoutManager
        lifeRv.adapter = LifeAdapter(player.life)
    }

    private fun updateLifeRV() {
        // Bruges til at opdatere mit lifeRv, når spillerens liv ændre sig
        lifeRv.adapter = LifeAdapter(player.life)
    }

    private fun initObjects(view: View) {

        // Shared Pref
        shared = view.context.getSharedPreferences("GAME", Context.MODE_PRIVATE)

        // Initialize player object
        player = Player(shared.getInt("point", 5000), shared.getInt("life", 5))

        // Generere et nyt ord
        wordGenerator.generateNewWord()

    }

    private fun initGameLogic() {
        // Giver mit kategory TextView ordets kategori
        categoryTxt.text = wordGenerator.category

        // Tildeler en liste med ordets bogstaver til mit charArray
        charArray = wordGenerator.wordList

        // charArray indeholder 2 tomme mellemrum ([, s, k, o, l, e, ]), derfor fratrækkes to fra charArray.size
        countCorrectGuess = charArray.size - 2

        // Generere et CharModel objekt til hvert bogstav
        // Som indsættes i min charList som skal bruges som argument til mit charAdapter
        for (i in 1 until charArray.size - 1) {
            charList.add(CharModel(charArray[i], false))
        }

        // Sætter spillerens point til mit point TextView
        pointsTxt.text = player.point.toString()
        // Fjerner mit EditText fra layoutet
        // Dette gøres fordi programmet er i wheel phase
        // og man skal derfor ikke have mulighed for at gætte
        guessEt.visibility = View.GONE
        // Sætter "Drej Hjulet" tekst til knappen
        actionBtn.setText(R.string.spin_the_wheel)

    }

    private fun initViews(view: View) {
        // Textview
        pointsTxt = view.findViewById(R.id.pointsTxt)
        guessedTxt = view.findViewById(R.id.guessedTxt)
        categoryTxt = view.findViewById(R.id.categoryTxt)
        rewardTxt = view.findViewById(R.id.rewardTxt)

        // Button
        actionBtn = view.findViewById(R.id.actionBtn)

        // Edittext
        guessEt = view.findViewById(R.id.guessEt)

        //Recyclerview
        wordRv = view.findViewById(R.id.wordRV)
        lifeRv = view.findViewById(R.id.lifeRV)
    }

    private fun togglePhase() {
        if (phase == Phase.WHEEL) {
            // Sætter spillets phase til GUESS
            phase = Phase.GUESS
            // Gør mit gætte textfelt synligt
            guessEt.visibility = View.VISIBLE
            // Sætter knappens tekst til "Gæt"
            actionBtn.setText(R.string.guess)
        } else {
            // Sætter spillets phase til WHEEL
            phase = Phase.WHEEL
            // Fjerner mit gætte textfelt
            guessEt.visibility = View.GONE
            // Sætter knappens tekst til "Drej Hjulet"
            actionBtn.setText(R.string.spin_the_wheel)
            // Fjerner teksten fra min reward tekstfelt
            // For at undgå forvirring
            rewardTxt.text = ""
        }
    }

    private fun showToast(view: View, message: String) {
        // Vis en Toast med kort varighed
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }

    private fun gameOver(context: Context, won: Boolean) {
        // Sætter spillets phase til ENDED
        phase = Phase.ENDED

        // Opdatere brugerens highscore
        updateHighScore()

        // Laver et Intent objekt med henblik på at skifte til GameOverActivity
        // Tilføjer ekstra info som skal føres med over til GameOverActivity
        val intent = Intent(context, GameOverActivity::class.java)
        intent.putExtra("WON", won)
        intent.putExtra("WORD", wordGenerator.word)
        intent.putExtra("POINT", player.point)
        intent.putExtra("LIFE", player.life)
        // Eksekverer min Intent
        startActivity(intent)
    }

    private fun updateHighScore() {
        // Henter den nuværende highscore
        // Default værdien sættes til 0, i tilfælde af, at der ikke blev fundet data
        val highscoreLife: Int = shared.getInt("lifeHighscore", 0)
        val highscorePoint: Int = shared.getInt("pointHighscore", 0)
        // Tjekker om spilleren havde en bedre score
        if (player.life > highscoreLife && player.point > highscorePoint) {
            // Opdaterer highscore værdierne
            shared.edit().putInt("lifeHighscore", player.life).apply()
            shared.edit().putInt("pointHighscore", player.point).apply()
        }
    }

}