package com.example.andrey.lastfmapp.ui.album_list

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.andrey.lastfmapp.GlideApp
import com.example.andrey.lastfmapp.R
import com.example.andrey.lastfmapp.domain.Album
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class AlbumRecyclerViewAdapter(private val context: Context)
    : RecyclerView.Adapter<AlbumRecyclerViewAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null
    private var items: List<Album> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = items[position]
        holder.itemView.tvAlbumName.text = album.name
        holder.itemView.tvArtistName.text = album.artist

        val imageUrl = album.images[album.images.keys.toList()[2]]
        ViewCompat.setTransitionName(holder.itemView.ivAlbumCover, album.mbid)

        GlideApp
                .with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_photo)
                .fitCenter()
                .into(holder.itemView.ivAlbumCover)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal fun getItems() : List<Album> {
        return items
    }

    internal fun setItems(items: List<Album>) {
        this.items = items
    }

    internal fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int, item: Album, sharedImageView: ImageView)
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override
        fun onClick(view: View) {
            mClickListener?.onItemClick(view, adapterPosition, items[adapterPosition], itemView.ivAlbumCover)
        }
    }
}
