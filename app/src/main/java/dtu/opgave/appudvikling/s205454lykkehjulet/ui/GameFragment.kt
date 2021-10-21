package dtu.opgave.appudvikling.s205454lykkehjulet.ui

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
import dtu.opgave.appudvikling.s205454lykkehjulet.Logic.GamePhase
import dtu.opgave.appudvikling.s205454lykkehjulet.Logic.Player
import dtu.opgave.appudvikling.s205454lykkehjulet.Logic.WordGenerator
import dtu.opgave.appudvikling.s205454lykkehjulet.Model.CharModel
import dtu.opgave.appudvikling.s205454lykkehjulet.Model.LifeModel
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class GameFragment : Fragment() {


    //TODO:
    // 1. change phase
    // 2. make cards invisible
    // 3. show category
    // 4. spin wheel
    // 5. rewards
    // 6. show winning page
    // 7. show losing page
    // 8. check for life = 0
    // 9. new game


    // Global lists
    private var lifesList = ArrayList<LifeModel>()
    private var charList = ArrayList<CharModel>()
    private var guessedChars = ArrayList<String>()

    private val wordGenerator: WordGenerator = WordGenerator()

    private var charArray: List<String> = emptyList()

    private var phase: GamePhase = GamePhase.GUESS

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

        // Button
        val actionBtn: Button = view.findViewById(R.id.actionBtn)

        // Edittext
        val guessEt: EditText = view.findViewById(R.id.guessEt)

        //Recyclerview
        val wordRv = view.findViewById<RecyclerView>(R.id.wordRV)
        val lifeRv = view.findViewById<RecyclerView>(R.id.lifeRV)

        // Initialize player object
        val player: Player = Player(5000, 5)

        displayWord(view, wordRv)
        displayLifeBar(view, lifeRv, player)

        pointsTxt.text = player.point.toString()

        actionBtn.setOnClickListener{
            if (phase == GamePhase.GUESS) {
                val guess: String = guessEt.text.toString()
                if (charArray.contains(guess) && !guessedChars.contains(guess)){
                    //TODO: Change visibility of card
                    guessedChars.add(guess)
                    guessedTxt.text = guessedChars.toString()
                    guessEt.setText("")
                    Log.d("GUESS", "Guessed")
                } else {
                    if (!guessedChars.contains(guess)){
                        Log.d("GUESS", "Not guessed")
                        guessedChars.add(guess)
                        guessedTxt.text = guessedChars.toString()
                        guessEt.setText("")
                        player.life = player.life - 1
                        displayLifeBar(view, lifeRv, player)
                    }
                }
            }
        }

        return view
    }

    private fun displayWord(view: View, wordRv: RecyclerView){

        charArray = wordGenerator.generateWord()

        val category: String = wordGenerator.getCategory()

        guessedChars = ArrayList<String>()
        charList = ArrayList<CharModel>()

        for (i in 1 until charArray.size-1) {
                charList.add(CharModel(charArray[i]))
                Log.d("CHARARRAY", "Number: " + i + " Char: " + charArray[i])
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

    private fun togglePhase(actionBtn: Button){
        if (phase == GamePhase.GUESS) {
            phase = GamePhase.WHEEL
            actionBtn.text = ""
        }else if (phase == GamePhase.WHEEL){
            phase = GamePhase.GUESS
        }
    }
}