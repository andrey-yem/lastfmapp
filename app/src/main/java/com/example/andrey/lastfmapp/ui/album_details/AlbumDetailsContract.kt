package com.example.andrey.lastfmapp.ui.album_details

import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.ui.base.BaseContract


class AlbumDetailsContract {

    interface View: BaseContract.View {
        fun setTrackTitles(tracks: List<String>)
        fun setAlbumName(albumName: String)
        fun setArtistName(artistName: String)
        fun showError(message: String)
        fun showProgressBar(showBar: Boolean)
        fun performEnterScreenAnimation(imageUrl: String?, imageTransitionName: String)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun fetchAlbumDetails(album: Album)
        fun bindData(data: Album, animationData : String)
    }
}