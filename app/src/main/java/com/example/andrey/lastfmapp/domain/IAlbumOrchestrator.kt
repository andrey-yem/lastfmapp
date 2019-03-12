package com.example.andrey.lastfmapp.domain

import io.reactivex.Single

interface IAlbumOrchestrator {
    fun getAlbumDetails(artist: String, album: String): Single<AlbumDetails>
    fun findAlbums(query: String, page: Int): Single<List<Album>>
}
