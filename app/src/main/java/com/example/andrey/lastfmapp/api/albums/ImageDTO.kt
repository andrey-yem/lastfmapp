package com.example.andrey.lastfmapp.api.albums

import com.google.gson.annotations.SerializedName

class ImageDTO {
    // "small", "medium", "large", "extralarge"
    @SerializedName("size")
    lateinit var size: String

    @SerializedName("#text")
    lateinit var url: String
}