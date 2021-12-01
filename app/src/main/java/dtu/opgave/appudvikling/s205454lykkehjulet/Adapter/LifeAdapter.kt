package dtu.opgave.appudvikling.s205454lykkehjulet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class LifeAdapter(private val lifeCount: Int) :
    RecyclerView.Adapter<LifeAdapter.LifeViewHolder>() {

    // Denne fil er baseret på slides fra uge 05

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LifeViewHolder {
        // Definer view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.life_item, parent, false)
        return LifeViewHolder(view)
    }

    // Returnér antallet af items i listen
    override fun getItemCount(): Int { return lifeCount }

    // Klassen og funktionen nedenfor bruges ikke,
    // da life_item.xml kun består af et cardview med et hjerte emoji
    override fun onBindViewHolder(holder: LifeViewHolder, position: Int) {}
    class LifeViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

}