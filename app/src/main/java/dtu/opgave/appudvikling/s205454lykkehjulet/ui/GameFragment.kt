package dtu.opgave.appudvikling.s205454lykkehjulet.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import dtu.opgave.appudvikling.s205454lykkehjulet.Model.LifeModel
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class GameFragment : Fragment() {

    //SOURCES:
    // https://kotlinlang.org/docs/control-flow.html#when-expression
    // https://camposha.info/android-examples/android-sharedpreferences/
    // https://www.tutorialspoint.com/how-to-create-horizontal-listview-in-android-using-kotlin
    // https://stackoverflow.com/questions/49846295/kotlin-count-occurrences-of-chararray-in-string
    // https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/


    //TODO:
    // 9. new game
    // 10. lobby - vis high score og mulighed for nyt spil


    // Global lists
    private var lifesList = ArrayList<LifeModel>()
    private var charList = ArrayList<CharModel>()
    private var guessedChars = ArrayList<String>()

    private val wordGenerator: WordGenerator = WordGenerator()

    private var charArray: List<String> = emptyList()

    private var phase: Phase = Phase.WHEEL

    private var tempPointReward: Int = 0

    private var countCorrectGuess: Int = 0

    // SharedPreferences found on:
    // https://camposha.info/android-examples/android-sharedpreferences/
    lateinit var shared: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize view
        val view = inflater.inflate(R.layout.fragment_game, container, false)

        """Initialize views"""
        // Textview
        val pointsTxt: TextView = view.findViewById(R.id.pointsTxt)
        val guessedTxt: TextView = view.findViewById(R.id.guessedTxt)
        val categoryTxt: TextView = view.findViewById(R.id.categoryTxt)

        // Button
        val actionBtn: Button = view.findViewById(R.id.actionBtn)

        // Edittext
        val guessEt: EditText = view.findViewById(R.id.guessEt)

        //Recyclerview
        val wordRv = view.findViewById<RecyclerView>(R.id.wordRV)
        val lifeRv = view.findViewById<RecyclerView>(R.id.lifeRV)

        // Shared Pref
        shared = view.context.getSharedPreferences("GAME", Context.MODE_PRIVATE)

        // Initialize player object
        val player: Player = Player(shared.getInt("point", 5000), shared.getInt("life", 5))

        // Rewards enum class
        val rewards: Rewards = Rewards()

        charArray = wordGenerator.generateWord()

        // The charArray contains 2 empty spaces ([, s, k, o, l, e, ]), therefore we minus 2 from the count
        countCorrectGuess = charArray.size - 2

        val category: String = wordGenerator.getCategory()

        categoryTxt.text = category

        for (i in 1 until charArray.size - 1) {
            charList.add(CharModel(charArray[i].uppercase(), false))
        }

        displayWord(view, wordRv)
        displayLifeBar(view, lifeRv, player)

        pointsTxt.text = player.point.toString()
        guessEt.visibility = View.GONE
        actionBtn.setText(R.string.spin_the_wheel)

        actionBtn.setOnClickListener {
            if (phase == Phase.WHEEL) {
                val reward: Enum<Rewards.Reward> = rewards.getReward()
                Log.d("REWARD", "onCreateView: REWARD: $reward")
                if (Rewards.Reward.values()[reward.ordinal].points == -1) {
                    // https://kotlinlang.org/docs/control-flow.html#when-expression
                    when (reward.name) {
                        Rewards.Reward.BANKRUPT.name -> {
                            showToast(view, "Du er gået bankerot, du mister derfor alle dine point")
                            player.point = 0
                            phase = Phase.WHEEL
                            guessEt.visibility = View.GONE
                            actionBtn.setText(R.string.spin_the_wheel)
                        }
                        Rewards.Reward.EXTRA_TURN.name -> {
                            player.life += 1
                            showToast(view, "Du får et ekstra liv")
                            displayLifeBar(view, lifeRv, player)
                            phase = Phase.WHEEL
                            guessEt.visibility = View.GONE
                            actionBtn.setText(R.string.spin_the_wheel)
                        }
                        Rewards.Reward.SKIP_TURN.name -> {
                            player.life -= 1
                            showToast(view, "Du springer en tur over og mister et liv")
                            if (player.life == 0) {
                                gameOver(view.context, won = false, player = player)
                            } else {
                                displayLifeBar(view, lifeRv, player)
                                phase = Phase.WHEEL
                                guessEt.visibility = View.GONE
                                actionBtn.setText(R.string.spin_the_wheel)
                            }
                        }
                    }
                } else {
                    tempPointReward = Rewards.Reward.values()[reward.ordinal].points
                    showToast(view, "Du landede på $tempPointReward point")
                    phase = Phase.GUESS
                    guessEt.visibility = View.VISIBLE
                    actionBtn.setText(R.string.guess)
                }
            } else {
                val guess: String = guessEt.text.toString().lowercase()
                if (charArray.contains(guess) && !guessedChars.contains(guess)) {

                    guessedChars.add(guess)
                    guessedTxt.text = guessedChars.toString()
                    guessEt.setText("")

                    // https://stackoverflow.com/questions/49846295/kotlin-count-occurrences-of-chararray-in-string
                    // The charArray contains 2 empty spaces ([, s, k, o, l, e, ]), therefore we minus 2 from the count
                    val count: Int = charArray.count { guess.contains(it) } - 2
                    player.point += tempPointReward * count
                    countCorrectGuess -= count

                    val index: List<Int> = indexOfAll(guess)
                    Log.d("REWARD", "onCreateView: INDEX: $index, $charArray")

                    for (i in index) {
                        charList[i - 1] = CharModel(charArray[i].uppercase(), true)
                    }

                    displayWord(view, wordRv)

                    if (countCorrectGuess == 0) {
                        gameOver(view.context, won = true, player = player)
                    }
                    Log.d("REWARD", "onCreateView: COUNT: $count, $charArray")
                } else {
                    if (!guessedChars.contains(guess)) {
                        if (player.life < 2) {
                            player.life -= 1
                            gameOver(view.context, won = false, player = player)
                        }
                        guessedChars.add(guess)
                        guessedTxt.text = guessedChars.toString()
                        guessEt.setText("")
                        player.life -= 1
                        displayLifeBar(view, lifeRv, player)
                    } else {
                        guessEt.setText("")
                        player.life -= 1
                        displayLifeBar(view, lifeRv, player)
                    }
                }
                tempPointReward = 0
                pointsTxt.text = player.point.toString()
                phase = Phase.WHEEL
                guessEt.visibility = View.GONE
                actionBtn.setText(R.string.spin_the_wheel)
            }

        }

        return view
    }

    private fun showToast(view: View, message: String) {
        Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
    }

    private fun displayWord(view: View, wordRv: RecyclerView) {

        // https://www.tutorialspoint.com/how-to-create-horizontal-listview-in-android-using-kotlin
        val mLayoutManager = LinearLayoutManager(view.context)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        wordRv.layoutManager = mLayoutManager

        val adapter = CharAdapter(charList)

        wordRv.adapter = adapter
    }

    private fun displayLifeBar(view: View, lifeRv: RecyclerView, player: Player) {
        // https://www.tutorialspoint.com/how-to-create-horizontal-listview-in-android-using-kotlin
        val mLayoutManager = LinearLayoutManager(view.context)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        lifeRv.layoutManager = mLayoutManager

        lifesList = ArrayList<LifeModel>()

        for (i in 1..player.life) {
            lifesList.add(LifeModel(i))
        }

        val adapter = LifeAdapter(lifesList)

        lifeRv.adapter = adapter
    }

    private fun gameOver(context: Context, won: Boolean, player: Player) {
        phase = Phase.ENDED
        val highscoreLife: Int = shared.getInt("lifeHighscore", 0)
        val highscorePoint: Int = shared.getInt("pointHighscore", 0)
        if (player.life > highscoreLife) {
            shared.edit().putInt("lifeHighscore", player.life).apply()
        }
        if (player.point > highscorePoint) {
            shared.edit().putInt("pointHighscore", player.point).apply()
        }
        val intent: Intent = Intent(context, GameOverActivity::class.java)
        intent.putExtra("WON", won)
        intent.putExtra("POINT", player.point)
        intent.putExtra("LIFE", player.life)
        startActivity(intent)
    }

    private fun indexOfAll(item: String): List<Int> {
        var count: List<Int> = emptyList()
        for (i in 1 until charArray.size - 1) {
            if (charArray[i] == item) {
                count = count + listOf<Int>(i)
            }
        }
        return count
    }

}