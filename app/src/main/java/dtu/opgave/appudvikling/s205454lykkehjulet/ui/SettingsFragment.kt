package dtu.opgave.appudvikling.s205454lykkehjulet.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class SettingsFragment : Fragment() {

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
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)

        val numberOfLife: TextView = view.findViewById(R.id.selectedLifeTxt)
        val numberOfPoint: TextView = view.findViewById(R.id.selectedPointTxt)

        val lifeSeekBar: SeekBar = view.findViewById(R.id.lifeSb)
        val pointSeekBar: SeekBar = view.findViewById(R.id.pointSb)

        shared = view.context.getSharedPreferences("GAME" , Context.MODE_PRIVATE)
        val edit = shared.edit()

        lifeSeekBar.progress = shared.getInt("life", 5)

        numberOfLife.text = "${getString(R.string.number_of_lifes)} - ${lifeSeekBar.progress}"

        // Seekbar onChange found on
        // https://www.geeksforgeeks.org/seekbar-in-kotlin/
        lifeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                numberOfLife.text = "${getString(R.string.number_of_lifes)} - $progress"
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                edit.putInt("life" , seek.progress).apply()
            }
        })

        pointSeekBar.progress = shared.getInt("point", 5000)/1000
        numberOfPoint.text = "${getString(R.string.number_of_points)} - ${pointSeekBar.progress}000"

        pointSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                numberOfPoint.text = "${getString(R.string.number_of_points)} - ${progress}000"
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                edit.putInt("point" , seek.progress*1000).apply()
            }
        })

        return view
    }

}