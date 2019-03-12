package com.example.andrey.lastfmapp.di

import com.example.andrey.lastfmapp.LastFmApp
import com.example.andrey.lastfmapp.api.LastfmService
import com.example.andrey.lastfmapp.domain.IAlbumOrchestrator
import com.example.andrey.lastfmapp.utils.IScheduler
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(application: LastFmApp)

    fun lastFmService(): LastfmService

    fun albumOrchestrator(): IAlbumOrchestrator

    fun scheduler(): IScheduler

    interface ApplicationComponentProvider {
        val appComponent: ApplicationComponent
    }
}