package com.example.andrey.lastfmapp.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
        @JvmField val name : String,
        @JvmField val artist : String,
        @JvmField val mbid: String?,
        @JvmField val images : Map<String, String>) : Parcelable