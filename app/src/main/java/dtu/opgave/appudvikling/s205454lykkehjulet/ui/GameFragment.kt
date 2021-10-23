package dtu.opgave.appudvikling.s205454lykkehjulet.ui

import android.content.Context
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


    //TODO:
    // 2. make cards invisible
    // 6. show winning page
    // 7. show losing page
    // 9. new game


    // Global lists
    private var lifesList = ArrayList<LifeModel>()
    private var charList = ArrayList<CharModel>()
    private var guessedChars = ArrayList<String>()

    private val wordGenerator: WordGenerator = WordGenerator()

    private var charArray: List<String> = emptyList()

    private var phase: Phase = Phase.WHEEL

    private var tempPointReward: Int = 0

    // SharedPreferences found on:
    // https://camposha.info/android-examples/android-sharedpreferences/
    lateinit var shared : SharedPreferences

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
        shared = view.context.getSharedPreferences("GAMESETTINGS" , Context.MODE_PRIVATE)

        // Initialize player object
        val player: Player = Player(shared.getInt("point", 5000), shared.getInt("life", 5))

        // Rewards enum class
        val rewards: Rewards = Rewards()

        displayWord(view, wordRv, categoryTxt)
        displayLifeBar(view, lifeRv, player)

        pointsTxt.text = player.point.toString()
        actionBtn.setText(R.string.spin_the_wheel)

        actionBtn.setOnClickListener{
            if (phase == Phase.WHEEL){
                val reward: Enum<Rewards.Reward> = rewards.getReward()
                Log.d("REWARD", "onCreateView: REWARD: $reward")
                if (Rewards.Reward.values()[reward.ordinal].points == -1){
                    when (reward.name){
                        Rewards.Reward.BANKRUPT.name -> gameOver()
                        Rewards.Reward.EXTRA_TURN.name -> {
                            player.life = player.life + 1
                            displayLifeBar(view, lifeRv, player)
                            phase = Phase.WHEEL
                            actionBtn.setText(R.string.spin_the_wheel)
                        }
                        Rewards.Reward.SKIP_TURN.name -> {
                            player.life -= 1
                            if (player.life <= 1){
                                gameOver()
                            } else {
                                displayLifeBar(view, lifeRv, player)
                                phase = Phase.WHEEL
                                actionBtn.setText(R.string.spin_the_wheel)
                            }
                        }
                    }
                } else {
                    tempPointReward = Rewards.Reward.values()[reward.ordinal].points
                    phase = Phase.GUESS
                    actionBtn.setText(R.string.guess)
                }
            }else{
                takeGuessTurn(guessEt, guessedTxt, player, view, lifeRv, actionBtn)
                phase = Phase.WHEEL
                actionBtn.setText(R.string.spin_the_wheel)
            }

        }

        return view
    }

    private fun displayWord(view: View, wordRv: RecyclerView, categoryTxt: TextView){

        charArray = wordGenerator.generateWord()

        val category: String = wordGenerator.getCategory()

        categoryTxt.text = category

        guessedChars = ArrayList<String>()
        charList = ArrayList<CharModel>()

        for (i in 1 until charArray.size-1) {
                charList.add(CharModel(charArray[i].uppercase()))
        }

        // https://www.tutorialspoint.com/how-to-create-horizontal-listview-in-android-using-kotlin
        val mLayoutManager = LinearLayoutManager(view.context)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        wordRv.layoutManager = mLayoutManager

        val adapter = CharAdapter(charList)

        wordRv.adapter = adapter
    }

    private fun displayLifeBar(view: View, lifeRv: RecyclerView, player: Player){
        // https://www.tutorialspoint.com/how-to-create-horizontal-listview-in-android-using-kotlin
        val mLayoutManager = LinearLayoutManager(view.context)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        lifeRv.layoutManager = mLayoutManager

        lifesList = ArrayList<LifeModel>()

        for (i in 1..player.life){
            lifesList.add(LifeModel(i))
        }

        val adapter = LifeAdapter(lifesList)

        lifeRv.adapter = adapter
    }

    private fun takeGuessTurn(guessEt: EditText, guessedTxt: TextView, player: Player, view: View, lifeRv: RecyclerView, actionBtn: Button){
        val guess: String = guessEt.text.toString()
        if (charArray.contains(guess) && !guessedChars.contains(guess)){
            //TODO: Change visibility of card
            guessedChars.add(guess)
            guessedTxt.text = guessedChars.toString()
            guessEt.setText("")
            //TODO: Multiply tempPointReward with number of occurrence in word
            player.point += tempPointReward
            tempPointReward = 0
            phase = Phase.WHEEL
            actionBtn.setText(R.string.spin_the_wheel)
        } else {
            tempPointReward = 0
            if (!guessedChars.contains(guess)){
                if (player.life < 2){
                    gameOver()
                }
                guessedChars.add(guess)
                guessedTxt.text = guessedChars.toString()
                guessEt.setText("")
                player.life -= 1
                displayLifeBar(view, lifeRv, player)
            }
        }
    }

    private fun gameOver(){
        phase = Phase.ENDED
    }

}