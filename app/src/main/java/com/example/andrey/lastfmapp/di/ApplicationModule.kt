package com.example.andrey.lastfmapp.di

import com.example.andrey.lastfmapp.BuildConfig
import com.example.andrey.lastfmapp.api.LastFmInterceptor
import com.example.andrey.lastfmapp.api.LastfmService
import com.example.andrey.lastfmapp.api.albums.*
import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.domain.AlbumDetails
import com.example.andrey.lastfmapp.domain.AlbumOrchestrator
import com.example.andrey.lastfmapp.domain.IAlbumOrchestrator
import com.example.andrey.lastfmapp.utils.IScheduler
import com.example.andrey.lastfmapp.utils.SchedulerImpl
import dagger.Module
import dagger.Provides
import io.reactivex.functions.Function
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val LASTFM_API = "https://ws.audioscrobbler.com/"
const val apiKey = "ce340f5eebb5f77cab7fc0ab76bc5f28"

@Module
open class ApplicationModule {

    @Singleton
    @Provides
    open fun provideScheduler(scheduler: SchedulerImpl): IScheduler = scheduler

    @Singleton
    @Provides
    fun provideLastfmService(retrofit: Retrofit): LastfmService {
        return retrofit.create(LastfmService::class.java)
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient = OkHttpClient().newBuilder()
            .addInterceptor(LastFmInterceptor(apiKey))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .build()

    @Singleton
    @Provides
    fun provideRetrofitClient(client: OkHttpClient): Retrofit = retrofit2.Retrofit.Builder()
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(LASTFM_API)
            .build()

    @Singleton
    @Provides
    open fun provideAlbumOrchestrator(
            interactor: AlbumApiInteractor
    ): IAlbumOrchestrator {
        return AlbumOrchestrator(interactor)
    }

    @Singleton
    @Provides
    fun provideAlbumApiInteractor(
            api: LastfmService,
            detailsResponseMapper : Function<AlbumDetailsResponseDTO, AlbumDetails>,
            searchResponseMapper : Function<AlbumSearchResponseDTO, List<Album>>
    ): AlbumApiInteractor {
        return AlbumApiRetrofitInteractor(api, detailsResponseMapper, searchResponseMapper)
    }


    @Singleton
    @Provides
    fun provideAlbumMapper(
            impl: AlbumMapper
    ): Function<AlbumSearchResponseDTO.AlbumSummaryDTO, Album>  = impl

    @Singleton
    @Provides
    fun provideAlbumSearchResponseMapper(
            impl: AlbumSearchToDomainMapper
    ): Function<AlbumSearchResponseDTO, List<Album>>  = impl

    @Singleton
    @Provides
    fun provideAlbumDetailsResponseMapper(
            impl: AlbumDetailsToDomainMapper
    ): Function<AlbumDetailsResponseDTO, AlbumDetails>  = impl
}