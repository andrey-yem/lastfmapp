package com.example.andrey.lastfmapp.api.albums

import com.example.andrey.lastfmapp.domain.Album
import io.reactivex.functions.Function
import javax.inject.Inject

class AlbumSearchToDomainMapper @Inject constructor(
        private val albumMapper: Function<AlbumSearchResponseDTO.AlbumSummaryDTO, Album>
) : Function<AlbumSearchResponseDTO, List<@JvmSuppressWildcards Album>> {
    override fun apply(dto: AlbumSearchResponseDTO): List<Album> {
        return dto.results?.matches?.albums?.map { albumMapper.apply(it) } ?: ArrayList()
    }
}