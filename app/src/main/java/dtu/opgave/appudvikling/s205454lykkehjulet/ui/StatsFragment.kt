package dtu.opgave.appudvikling.s205454lykkehjulet.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import dtu.opgave.appudvikling.s205454lykkehjulet.R


class StatsFragment : Fragment() {

    lateinit var shared : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_stats, container, false)

        val pointHighscoreTxt: TextView = view.findViewById(R.id.highscorePointTxt)
        val lifeHighscoreTxt: TextView = view.findViewById(R.id.highscoreLifeTxt)

        shared = view.context.getSharedPreferences("GAME" , Context.MODE_PRIVATE)

        pointHighscoreTxt.text = shared.getInt("pointHighscore", 0).toString()
        lifeHighscoreTxt.text = shared.getInt("lifeHighscore", 0).toString()

        return view;
    }

}