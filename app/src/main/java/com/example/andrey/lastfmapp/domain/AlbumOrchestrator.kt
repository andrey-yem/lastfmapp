package com.example.andrey.lastfmapp.domain

import com.example.andrey.lastfmapp.api.albums.AlbumApiInteractor
import io.reactivex.Single
import javax.inject.Inject

class AlbumOrchestrator @Inject constructor(
        private val apiInteractor: AlbumApiInteractor
        //, val databaseInteractor: AlbumDatabaseInteractor
) : IAlbumOrchestrator {

    // Here we can add logic to query local storage first, and then hit network
    override fun getAlbumDetails(artist: String, album: String): Single<AlbumDetails> {
        return apiInteractor.getAlbumDetails(artist, album)
    }

    override fun findAlbums(query: String, page: Int): Single<List<Album>> {
        return apiInteractor.findAlbums(query, page)
    }
}
