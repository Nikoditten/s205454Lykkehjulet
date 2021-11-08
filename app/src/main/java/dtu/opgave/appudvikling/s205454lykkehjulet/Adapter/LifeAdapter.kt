package dtu.opgave.appudvikling.s205454lykkehjulet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import dtu.opgave.appudvikling.s205454lykkehjulet.Model.LifeModel
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class LifeAdapter(private val lifeList: List<LifeModel>) :
    RecyclerView.Adapter<LifeAdapter.LifeViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LifeAdapter.LifeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.life_item, parent, false)

        return LifeAdapter.LifeViewHolder(view)
    }

    override fun onBindViewHolder(holder: LifeViewHolder, position: Int) { val lifeModel = lifeList[position] }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return lifeList.size
    }

    // Holds the views for adding it to image and text
    class LifeViewHolder(view: View) : RecyclerView.ViewHolder(view) {}

}