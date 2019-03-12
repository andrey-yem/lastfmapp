package com.example.andrey.lastfmapp.di

import com.example.andrey.lastfmapp.ui.album_details.AlbumDetailsActivity
import com.example.andrey.lastfmapp.ui.album_details.AlbumDetailsContract
import com.example.andrey.lastfmapp.ui.album_details.AlbumDetailsPresenter
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module(includes = [AlbumDetailsModule.Bindings::class])
class AlbumDetailsModule(private var activity: AlbumDetailsActivity) {

    @Provides
    fun provideActivity(): AlbumDetailsActivity {
        return activity
    }

    @Module
    internal interface Bindings {

        @Binds
        @ActivityScope
        fun bindPresenter(impl: AlbumDetailsPresenter): AlbumDetailsContract.Presenter

    }
}