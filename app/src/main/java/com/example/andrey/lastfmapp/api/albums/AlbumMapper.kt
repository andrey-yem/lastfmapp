package com.example.andrey.lastfmapp.api.albums

import com.example.andrey.lastfmapp.domain.Album
import io.reactivex.functions.Function
import javax.inject.Inject

class AlbumMapper @Inject constructor(
) : Function<AlbumSearchResponseDTO.AlbumSummaryDTO, Album> {

    override fun apply(dto: AlbumSearchResponseDTO.AlbumSummaryDTO): Album {
        return Album(dto.name, dto.artist, dto.mbid, dto.image.map { it.size to it.url }.toMap())
    }
}