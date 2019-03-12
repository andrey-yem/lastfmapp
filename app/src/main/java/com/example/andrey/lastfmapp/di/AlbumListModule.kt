package com.example.andrey.lastfmapp.di

import com.example.andrey.lastfmapp.ui.album_list.AlbumListActivity
import com.example.andrey.lastfmapp.ui.album_list.AlbumListContract
import com.example.andrey.lastfmapp.ui.album_list.AlbumListPresenter
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [AlbumListModule.Bindings::class])
open class AlbumListModule(private var activity: AlbumListActivity) {

    @Provides
    fun provideActivity(): AlbumListActivity {
        return activity
    }

    @Module
    internal interface Bindings {
        @Binds
        @ActivityScope
        fun bindPresenter(impl: AlbumListPresenter): AlbumListContract.Presenter
    }
}