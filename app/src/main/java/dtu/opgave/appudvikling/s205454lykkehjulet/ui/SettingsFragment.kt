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

    // SharedPreferences fundet på:
    // https://camposha.info/android-examples/android-sharedpreferences/
    lateinit var shared : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Definering af view
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)

        // Definerings af textviews
        val numberOfLife: TextView = view.findViewById(R.id.selectedLifeTxt)
        val numberOfPoint: TextView = view.findViewById(R.id.selectedPointTxt)

        // Definering af SeekBars
        val lifeSeekBar: SeekBar = view.findViewById(R.id.lifeSb)
        val pointSeekBar: SeekBar = view.findViewById(R.id.pointSb)

        // Definer kilde til sharedpref
        shared = view.context.getSharedPreferences("GAME" , Context.MODE_PRIVATE)
        // Sætter i edit mode
        val edit = shared.edit()

        // Hent den gemte værdi for life, hvis værdien er null. Brug 5 som værdi
        // Sæt den indhentede værdi lig med SeekBarens progress
        lifeSeekBar.progress = shared.getInt("life", 5)

        // Sæt den indhentede værdi ind i textviewet
        numberOfLife.text = "${getString(R.string.number_of_lifes)} - ${lifeSeekBar.progress}"

        // Seekbar onChange funder på
        // https://www.geeksforgeeks.org/seekbar-in-kotlin/
        lifeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                // Ændre text for numberOfLife, hver gang brugeren ændre på seekbaren
                numberOfLife.text = "${getString(R.string.number_of_lifes)} - $progress"
            }

            override fun onStartTrackingTouch(seek: SeekBar) {}

            override fun onStopTrackingTouch(seek: SeekBar) {
                // Når brugeren er færdig med at bruge seekbaren.
                // Gem værdien i sharedpref
                edit.putInt("life" , seek.progress).apply()
            }
        })

        // Hent den gemte værdi for life, hvis værdien er null. Brug 5 som værdi
        // Sæt den indhentede værdi lig med SeekBarens progress
        pointSeekBar.progress = shared.getInt("point", 5000)/1000

        // Sæt den indhentede værdi ind i textviewet
        numberOfPoint.text = "${getString(R.string.number_of_points)} - ${pointSeekBar.progress}000"

        // Seekbar onChange funder på
        // https://www.geeksforgeeks.org/seekbar-in-kotlin/
        pointSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                // Ændre text for numberOfPoint, hver gang brugeren ændre på seekbaren
                numberOfPoint.text = "${getString(R.string.number_of_points)} - ${progress}000"
            }

            override fun onStartTrackingTouch(seek: SeekBar) {}

            override fun onStopTrackingTouch(seek: SeekBar) {
                // Når brugeren er færdig med at bruge seekbaren.
                // Gem værdien i sharedpref
                edit.putInt("point" , seek.progress*1000).apply()
            }
        })

        return view
    }

}