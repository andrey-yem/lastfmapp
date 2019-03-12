package com.example.andrey.lastfmapp.ui.album_list

import android.os.Bundle
import com.example.andrey.lastfmapp.ui.base.BaseContract
import com.example.andrey.lastfmapp.domain.Album


class AlbumListContract {

    interface View: BaseContract.View {
        fun showAlbums(albums: List<Album>)
        fun goToAlbumDetailsScreen(album: Album, transitionName: String, optionsBundle : Bundle?)
        fun showError(message: String)
        fun showProgressBar(showBar: Boolean)
    }

    interface Presenter: BaseContract.Presenter<View> {
        fun searchQueryUpdated(query : String)
        fun loadMore()
        fun albumCardSelected(album : Album, transitionName: String, optionsBundle : Bundle?)
    }
}