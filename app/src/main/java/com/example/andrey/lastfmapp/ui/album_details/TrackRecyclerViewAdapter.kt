package com.example.andrey.lastfmapp.ui.album_details

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.andrey.lastfmapp.R
import kotlinx.android.synthetic.main.recyclerview_row.view.*

class TrackRecyclerViewAdapter(context: Context)
: RecyclerView.Adapter<TrackRecyclerViewAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var items: List<String> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trackTitle = items[position]
        holder.itemView.tvTrackOrder.text = Integer.valueOf(position + 1).toString()
        holder.itemView.tvTrackName.text = trackTitle
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal fun getItems() : List<String> {
        return items
    }

    internal fun setItems(items : List<String>) {
        this.items = items
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)
}
