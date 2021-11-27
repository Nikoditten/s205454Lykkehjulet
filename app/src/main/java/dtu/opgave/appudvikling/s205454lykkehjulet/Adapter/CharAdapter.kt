package dtu.opgave.appudvikling.s205454lykkehjulet.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dtu.opgave.appudvikling.s205454lykkehjulet.Model.CharModel
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class CharAdapter(private val charList: List<CharModel>) : RecyclerView.Adapter<CharAdapter.CharViewHolder>() {

    // Denne fil er baseret på slides fra uge 05

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharViewHolder {
        // Definer view
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.char_item, parent, false)

        return CharViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharViewHolder, position: Int) {
        val charModel = charList[position]
        // Indsætter bogstav i charView
        holder.charView.text = charModel.charItem

        // Hvis charModel ikke er visible - sæt visibilty til INVISIBLE -- ellers VISIBLE
        if (!charModel.visible) {
            holder.charView.visibility = View.INVISIBLE
        } else {
            holder.charView.visibility = View.VISIBLE
        }

    }

    // Returnér antallet af items i listen
    override fun getItemCount(): Int {
        return charList.size
    }

    class CharViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Angiv det textview der bruges i char_item.xml
        val charView: TextView = view.findViewById(R.id.charTxt)
    }

}