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
        val guess: String = guessEt.text.toString().lowercase()
        if (charArray.contains(guess) && !guessedChars.contains(guess)) {
            showToast(view, getString(R.string.correct))
            guessedChars.add(guess)
            guessedTxt.text = guessedChars.toString()
            guessEt.setText("")

            // charArray indeholder 2 tomme mellemrum ([, s, k, o, l, e, ]), derfor fratrækkes to fra charArray.size
            // https://stackoverflow.com/questions/49846295/kotlin-count-occurrences-of-chararray-in-string
            val count: Int = charArray.count { guess.contains(it) } - 2
            player.point += tempPointReward * count
            countCorrectGuess -= count

            val index: List<Int> = wordGenerator.indexOfAll(guess, charArray)

            for (i in index) {
                charList[i - 1] = CharModel(charArray[i], true)
                charAdapter.notifyItemChanged(i - 1)
            }

            if (countCorrectGuess == 0) {
                gameOver(view.context, won = true)
            }

        } else {
            if (!guessedChars.contains(guess)) {
                showToast(view, getString(R.string.wrong))
                if (player.life < 2) {
                    player.life -= 1
                    gameOver(view.context, won = false)
                }
                guessedChars.add(guess)
                guessedTxt.text = guessedChars.toString()
                guessEt.setText("")
                player.life -= 1
            } else {
                guessEt.setText("")
                player.life -= 1
                showToast(view, getString(R.string.already_guessed))
            }
        }
        updateLifeRV()
        tempPointReward = 0
        pointsTxt.text = player.point.toString()
        togglePhase()
    }

    private fun executeWheelPhase(view: View) {
        val reward: Enum<Rewards.Reward> = rewards.getReward()
        if (Rewards.Reward.values()[reward.ordinal].points == -1) {
            // When kommando fundet på
            // https://kotlinlang.org/docs/control-flow.html#when-expression
            when (reward.name) {
                Rewards.Reward.BANKRUPT.name -> {
                    rewardTxt.text = getString(R.string.bankrupt)
                    player.point = 0
                    gameOver(view.context, false)
                }
                Rewards.Reward.EXTRA_TURN.name -> {
                    player.life += 1
                    rewardTxt.text = getString(R.string.extra_life)
                }
                Rewards.Reward.SKIP_TURN.name -> {
                    player.life -= 1
                    rewardTxt.text = getString(R.string.skip_turn)
                    if (player.life == 0) {
                        gameOver(view.context, won = false)
                    }
                }
            }
            updateLifeRV()
        } else {
            tempPointReward = Rewards.Reward.values()[reward.ordinal].points
            rewardTxt.text = getString(R.string.point_reward, tempPointReward)
            togglePhase()
        }
    }

    private fun initRecyclerViews(view: View) {
        // Horizontalt recyclerview fundet på
        // https://www.tutorialspoint.com/how-to-create-horizontal-listview-in-android-using-kotlin
        charLayoutManager = LinearLayoutManager(view.context)
        // Recyclerviewet skal have et horizontalt scroll direction
        charLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        // Definere et adapter objekt til wordRv
        charAdapter = CharAdapter(charList)

        // Tildeler LinearLayoutManager og charAdapter til mit wordRv
        wordRv.layoutManager = charLayoutManager
        wordRv.adapter = charAdapter

        // Definere LinearLayoutManager til lifeRv
        lifeLayoutManager = LinearLayoutManager(view.context)
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
        // Hvis en Toast med kort varighed
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