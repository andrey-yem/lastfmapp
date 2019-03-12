package com.example.andrey.lastfmapp.api.albums

import com.google.gson.annotations.SerializedName

class AlbumDetailsResponseDTO {

    @SerializedName("album")
    lateinit var album: AlbumDTO

    class AlbumDTO {

        @SerializedName("artist")
        lateinit var artist: String

        @SerializedName("image")
        lateinit var image: List<ImageDTO>

        @SerializedName("listeners")
        lateinit var listeners: String

        @SerializedName("mbid")
        var mbid: String? = null

        @SerializedName("name")
        lateinit var name: String

        @SerializedName("playcount")
        lateinit var playcount: String

        @SerializedName("tracks")
        lateinit var tracks: TracksDTO

        @SerializedName("url")
        lateinit var url: String

        @SerializedName("wiki")
        var wiki: WikiDTO? = null

    }

    class TracksDTO {
        @SerializedName("track")
        lateinit var track: List<TrackDTO>
    }

    class TrackDTO {
        @SerializedName("url")
        lateinit var url: String

        @SerializedName("duration")
        lateinit var duration: String

        @SerializedName("name")
        lateinit var name: String

        @SerializedName("@attr")
        lateinit var attr: RankDTO

        @SerializedName("artist")
        lateinit var artist: ArtistDTO
    }

    class RankDTO {
        @SerializedName("rank")
        lateinit var rank: String
    }

    class ArtistDTO {
        @SerializedName("mbid")
        lateinit var mbid: String

        @SerializedName("name")
        lateinit var name: String

        @SerializedName("url")
        lateinit var url: String
    }

    class WikiDTO {
        @SerializedName("content")
        lateinit var content: String

        @SerializedName("published")
        lateinit var published: String

        @SerializedName("summary")
        lateinit var summary: String
    }
}