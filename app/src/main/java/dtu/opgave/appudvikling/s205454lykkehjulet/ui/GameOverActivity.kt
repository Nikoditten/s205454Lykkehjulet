package dtu.opgave.appudvikling.s205454lykkehjulet.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class GameOverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val won: Boolean = intent.extras!!.getBoolean("WON")
        val point: Int = intent.extras!!.getInt("POINT")
        val life: Int = intent.extras!!.getInt("LIFE")

        val gameStatusTxt: TextView = findViewById(R.id.gameStatusTxt)
        val pointsLeftTxt: TextView = findViewById(R.id.pointsLeftTxt)
        val lifeLeftTxt: TextView = findViewById(R.id.lifeLeftTxt)

        if (won) gameStatusTxt.text = "Tillykke, du vandt" else gameStatusTxt.text = "Øv, du tabte"
        pointsLeftTxt.text = "Du fik $point point"
        lifeLeftTxt.text = "Du havde $life liv tilbage"

    }
}