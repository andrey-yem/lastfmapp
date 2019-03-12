package com.example.andrey.lastfmapp

import android.app.Application
import com.example.andrey.lastfmapp.di.ApplicationComponent
import com.example.andrey.lastfmapp.di.ApplicationModule
import com.example.andrey.lastfmapp.di.DaggerApplicationComponent

class LastFmApp : Application(), ApplicationComponent.ApplicationComponentProvider {
    override lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        setup()
    }

    private fun setup() {
        appComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule()).build()
        appComponent.inject(this)
    }

    companion object {
        lateinit var instance: LastFmApp private set
    }
}