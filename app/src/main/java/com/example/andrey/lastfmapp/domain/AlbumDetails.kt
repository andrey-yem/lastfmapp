package com.example.andrey.lastfmapp.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlbumDetails(
        @JvmField val name : String,
        @JvmField val artist : String,
        @JvmField val mbid: String?,
        @JvmField val images : Map<String, String>,
        @JvmField val url: String,
        @JvmField val tracks: List<Track>,
        @JvmField val wiki: WikiEntry?) : Parcelable {

    @Parcelize
    data class Track(
            @JvmField val rank: String,
            @JvmField val url: String,
            @JvmField val duration: String,
            @JvmField val name: String) : Parcelable

    @Parcelize
    data class WikiEntry(
            @JvmField val content: String,
            @JvmField val published: String,
            @JvmField val summary: String) : Parcelable

    companion object {
        const val IMAGE_RESOLUTION_SMALL = "small"
        const val IMAGE_RESOLUTION_MEDIUM = "medium"
        const val IMAGE_RESOLUTION_LARGE = "large"
        const val IMAGE_RESOLUTION_EXTRALARGE = "extralarge"
    }
}