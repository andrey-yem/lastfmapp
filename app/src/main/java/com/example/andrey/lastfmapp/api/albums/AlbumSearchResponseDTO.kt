package com.example.andrey.lastfmapp.api.albums

import com.google.gson.annotations.SerializedName

class AlbumSearchResponseDTO {

    @SerializedName("results")
    var results: ResultsDTO? = null

    class ResultsDTO {
        @SerializedName("albummatches")
        var matches: AlbumMatchDTO? = null
    }

    class AlbumMatchDTO {
        @SerializedName("album")
        lateinit var albums: List<AlbumSummaryDTO>
    }


    class AlbumSummaryDTO {
        @SerializedName("artist")
        lateinit var artist: String

        @SerializedName("image")
        lateinit var image: List<ImageDTO>

        @SerializedName("mbid")
        lateinit var mbid: String

        @SerializedName("name")
        lateinit var name: String

        @SerializedName("streamable")
        lateinit var streamable: String

        @SerializedName("url")
        lateinit var url: String
    }
}