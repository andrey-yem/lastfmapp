package com.example.andrey.lastfmapp.api.albums

import com.example.andrey.lastfmapp.domain.AlbumDetails
import io.reactivex.functions.Function
import javax.inject.Inject

class AlbumDetailsToDomainMapper @Inject constructor() :
        Function<AlbumDetailsResponseDTO, AlbumDetails> {

    override fun apply(dto: AlbumDetailsResponseDTO): AlbumDetails {
        val wikiDto = dto.album.wiki
        val wiki = if (wikiDto != null) AlbumDetails.WikiEntry(wikiDto.content, wikiDto.published, wikiDto.summary) else null

        val tracksDto = dto.album.tracks
        val tracks = tracksDto.track.map { trackDto ->
            AlbumDetails.Track(trackDto.attr.rank, trackDto.url, trackDto.duration, trackDto.name)
        }

        val images = dto.album.image.map { it.size to it.url }.toMap()

        return AlbumDetails(
                dto.album.name,
                dto.album.artist,
                dto.album.mbid,
                images,
                dto.album.url,
                tracks,
                wiki)
    }
}