package dtu.opgave.appudvikling.s205454lykkehjulet.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dtu.opgave.appudvikling.s205454lykkehjulet.Model.CharModel
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class CharAdapter(private val charList: List<CharModel>) : RecyclerView.Adapter<CharAdapter.CharViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.char_item, parent, false)

        return CharViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharViewHolder, position: Int) {
        val charModel = charList[position]

        holder.charView.text = charModel.charItem

        if (!charModel.visible) {
            holder.charView.visibility = View.INVISIBLE
        } else {
            holder.charView.visibility = View.VISIBLE
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        Log.d("CHARARRAY", "getItemCount: " + charList.size)
        return charList.size
    }

    // Holds the views for adding it to image and text
    class CharViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val charView: TextView = view.findViewById(R.id.charTxt)
    }

}