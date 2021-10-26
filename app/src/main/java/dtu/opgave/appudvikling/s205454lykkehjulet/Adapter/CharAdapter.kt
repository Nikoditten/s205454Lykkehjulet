package dtu.opgave.appudvikling.s205454lykkehjulet.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dtu.opgave.appudvikling.s205454lykkehjulet.Model.CharModel
import dtu.opgave.appudvikling.s205454lykkehjulet.R

class CharAdapter(private val mList: List<CharModel>) : RecyclerView.Adapter<CharAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.char_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event_model = mList[position]

        holder.charView.text = event_model.charItem

        if (!event_model.visible) {
            holder.charView.visibility = View.INVISIBLE
        } else {
            holder.charView.visibility = View.VISIBLE
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        Log.d("CHARARRAY", "getItemCount: " + mList.size)
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val charView: TextView = itemView.findViewById(R.id.charTxt)
    }

}