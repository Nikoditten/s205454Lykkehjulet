package dtu.opgave.appudvikling.s205454lykkehjulet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import dtu.opgave.appudvikling.s205454lykkehjulet.Model.LifeModel
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class LifeAdapter(private val mList: List<LifeModel>) :
    RecyclerView.Adapter<LifeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LifeAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.life_item, parent, false)

        return LifeAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) { val life_model = mList[position] }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {}

}