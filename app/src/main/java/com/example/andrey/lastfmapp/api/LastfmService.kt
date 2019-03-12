package com.example.andrey.lastfmapp.api

import com.example.andrey.lastfmapp.api.albums.AlbumDetailsResponseDTO
import com.example.andrey.lastfmapp.api.albums.AlbumSearchResponseDTO
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface LastfmService {

    @GET("2.0/?method=album.search")
    fun findAlbums(
            @Query("album") album: String,
            @Query("page") page: Int
    ): Single<AlbumSearchResponseDTO>

    @GET("2.0/?method=album.getinfo")
    fun getAlbumDetails(
            @Query("artist") artist: String,
            @Query("album") album: String
    ): Single<AlbumDetailsResponseDTO>

}