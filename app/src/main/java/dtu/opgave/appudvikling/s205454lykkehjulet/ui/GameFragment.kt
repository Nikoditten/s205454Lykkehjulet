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

    private val wordGenerator: WordGenerator = WordGenerator()

    private val rewards: Rewards = Rewards()

    private var phase: Phase = Phase.WHEEL

    private var tempPointReward: Int = 0

    private var countCorrectGuess: Int = 0

    private lateinit var player: Player

    private lateinit var pointsTxt: TextView
    private lateinit var guessedTxt: TextView
    private lateinit var categoryTxt: TextView
    private lateinit var rewardTxt: TextView

    private lateinit var guessEt: EditText

    private lateinit var actionBtn: Button

    private lateinit var wordRv: RecyclerView
    private lateinit var lifeRv: RecyclerView

    private lateinit var lifeLayoutManager: LinearLayoutManager
    private lateinit var charLayoutManager: LinearLayoutManager

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

            val index: List<Int> = indexOfAll(guess)

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
        charLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        charAdapter = CharAdapter(charList)

        wordRv.layoutManager = charLayoutManager

        wordRv.adapter = charAdapter

        lifeLayoutManager = LinearLayoutManager(view.context)
        lifeLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        lifeRv.layoutManager = lifeLayoutManager

        lifeRv.adapter = LifeAdapter(player.life)
    }

    private fun updateLifeRV() {
        lifeRv.adapter = LifeAdapter(player.life)
    }

    private fun initObjects(view: View) {

        // Shared Pref
        shared = view.context.getSharedPreferences("GAME", Context.MODE_PRIVATE)

        // Initialize player object
        player = Player(shared.getInt("point", 5000), shared.getInt("life", 5))

        wordGenerator.generateNewWord()

    }

    private fun initGameLogic() {

        categoryTxt.text = wordGenerator.category

        charArray = wordGenerator.wordList

        // charArray indeholder 2 tomme mellemrum ([, s, k, o, l, e, ]), derfor fratrækkes to fra charArray.size
        countCorrectGuess = charArray.size - 2

        for (i in 1 until charArray.size - 1) {
            charList.add(CharModel(charArray[i], false))
        }

        pointsTxt.text = player.point.toString()
        guessEt.visibility = View.GONE
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
            phase = Phase.GUESS
            guessEt.visibility = View.VISIBLE
            actionBtn.setText(R.string.guess)
        } else {
            phase = Phase.WHEEL
            guessEt.visibility = View.GONE
            actionBtn.setText(R.string.spin_the_wheel)
            rewardTxt.text = ""
        }
    }

    private fun showToast(view: View, message: String) {
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }

    private fun gameOver(context: Context, won: Boolean) {
        phase = Phase.ENDED

        updateHighScore()

        val intent = Intent(context, GameOverActivity::class.java)
        intent.putExtra("WON", won)
        intent.putExtra("WORD", wordGenerator.word)
        intent.putExtra("POINT", player.point)
        intent.putExtra("LIFE", player.life)
        startActivity(intent)
    }

    private fun updateHighScore() {
        val highscoreLife: Int = shared.getInt("lifeHighscore", 0)
        val highscorePoint: Int = shared.getInt("pointHighscore", 0)
        if (player.life > highscoreLife && player.point > highscorePoint) {
            shared.edit().putInt("lifeHighscore", player.life).apply()
            shared.edit().putInt("pointHighscore", player.point).apply()
        }
    }

    private fun indexOfAll(item: String): List<Int> {
        var count: List<Int> = emptyList()
        for (i in 1 until charArray.size - 1) {
            if (charArray[i] == item) {
                count = count + listOf(i)
            }
        }
        return count
    }

}