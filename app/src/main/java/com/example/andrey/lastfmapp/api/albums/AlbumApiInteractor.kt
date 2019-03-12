package com.example.andrey.lastfmapp.api.albums

import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.domain.AlbumDetails
import io.reactivex.Single

interface AlbumApiInteractor {
    fun getAlbumDetails(artist: String, albumName: String): Single<AlbumDetails>
    fun findAlbums(query: String, pageOffset: Int): Single<List<Album>>
}
