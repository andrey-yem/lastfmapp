package com.example.andrey.lastfmapp.api.albums

import com.example.andrey.lastfmapp.api.LastfmService
import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.domain.AlbumDetails
import io.reactivex.Single
import io.reactivex.functions.Function
import javax.inject.Inject

class AlbumApiRetrofitInteractor @Inject constructor(
        private val api: LastfmService,
        private val detailsResponseMapper: Function<AlbumDetailsResponseDTO, AlbumDetails>,
        private val searchResponseMapper: Function<AlbumSearchResponseDTO, List<Album>>
) : AlbumApiInteractor {

    override fun getAlbumDetails(artist: String, albumName: String): Single<AlbumDetails> {
        return api.getAlbumDetails(artist, albumName)
                .map(detailsResponseMapper)
    }

    override fun findAlbums(query: String, pageOffset: Int): Single<List<Album>> {
        return api.findAlbums(query, FIRST_PAGE_INDEX + pageOffset)
                .map(searchResponseMapper)
    }

    companion object {
        const val FIRST_PAGE_INDEX = 1
    }

}
