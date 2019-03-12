package com.example.andrey.lastfmapp.ui.album_details

import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.domain.AlbumDetails.Companion.IMAGE_RESOLUTION_EXTRALARGE
import com.example.andrey.lastfmapp.domain.AlbumDetails.Companion.IMAGE_RESOLUTION_LARGE
import com.example.andrey.lastfmapp.domain.AlbumDetails.Companion.IMAGE_RESOLUTION_MEDIUM
import com.example.andrey.lastfmapp.domain.AlbumDetails.Companion.IMAGE_RESOLUTION_SMALL
import com.example.andrey.lastfmapp.domain.IAlbumOrchestrator
import com.example.andrey.lastfmapp.utils.IScheduler
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AlbumDetailsPresenter @Inject constructor(
        private val albumOrchestrator: IAlbumOrchestrator,
        private val scheduler: IScheduler
) : AlbumDetailsContract.Presenter {

    private lateinit var view: AlbumDetailsContract.View
    internal val subscriptions = CompositeDisposable()
    private var isLoading = false
        set(value) {
            field = value
            view.showProgressBar(value)
        }

    override fun bindView(view: AlbumDetailsContract.View) {
        this.view = view
    }

    override fun unbindView() {
        subscriptions.clear()
    }

    override fun bindData(data: Album, animationData: String) {
        view.setAlbumName(data.name)
        view.setArtistName(data.artist)
        view.performEnterScreenAnimation(getImageUrl(data), animationData)
        fetchAlbumDetails(data)
    }

    /*
     * Checks if imageMaps contains image for resolutions in the preferable order -> L, M, S, XL
     * If not then return any entry in the map or NULL if imageMap is empty
     */
    private fun getImageUrl(album: Album): String? {
        val imagesMap = album.images
        val preferableImage = PREFERABLE_IMAGE_RESOLUTIONS.firstOrNull { imagesMap.containsKey(it) }?.let { imagesMap[it] }
        return when {
            preferableImage != null -> preferableImage
            imagesMap.isEmpty() -> null
            else -> imagesMap[imagesMap.keys.toList()[0]]
        }
    }

    // TODO: add generic error handler for all views?
    override fun fetchAlbumDetails(album: Album) {
        isLoading = true
        subscriptions.add(albumOrchestrator.getAlbumDetails(album.artist, album.name)
                .subscribeOn(scheduler.background())
                .observeOn(scheduler.main())
                .subscribe({ albumDetails ->
                    val trackTitles = albumDetails.tracks.sortedBy { it.rank }.map { it.name }
                    view.setTrackTitles(trackTitles)
                    isLoading = false
                }, { error ->
                    view.showError(error.message ?: "")
                    isLoading = false
                }))
    }

    companion object {
        val PREFERABLE_IMAGE_RESOLUTIONS = listOf(
                IMAGE_RESOLUTION_LARGE,
                IMAGE_RESOLUTION_MEDIUM,
                IMAGE_RESOLUTION_SMALL,
                IMAGE_RESOLUTION_EXTRALARGE)
    }
}