package com.example.andrey.lastfmapp.ui.album_list

import android.os.Bundle
import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.domain.IAlbumOrchestrator
import com.example.andrey.lastfmapp.utils.IScheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class AlbumListPresenter @Inject constructor(
        private val albumOrchestrator: IAlbumOrchestrator,
        private val scheduler: IScheduler
) : AlbumListContract.Presenter {


    internal val subscriptions = CompositeDisposable()
    private lateinit var view: AlbumListContract.View
    private var isLoading = false
        set(value) {
            field = value
            view.showProgressBar(value)
        }

    private var searchResults = listOf<Album>()

    private var query: String = ""
    private var pageOffset = 0

    override fun unbindView() {
        subscriptions.clear()
    }

    override fun bindView(view: AlbumListContract.View) {
        this.view = view
        resetSearchResults()
    }

    private fun resetSearchResults() {
        searchResults = mutableListOf()
        pageOffset = 0
    }

    private fun fetchSearchResultsSinglePage(query: String, page: Int): Single<List<Album>> {
        return if (this.query.isEmpty()) {
            Single.fromCallable { emptyList<Album>() }
        } else {
            albumOrchestrator.findAlbums(query, page)
                    .observeOn(scheduler.main())
                    .subscribeOn(scheduler.background())
        }
    }

    // TODO: add generic error handler to handle common errros in all views?
    override fun searchQueryUpdated(query: String) {
        resetSearchResults()
        this.query = query
        isLoading = true

        subscriptions.add(
                fetchSearchResultsSinglePage(this.query, pageOffset)
                        .subscribe({ combinedResults ->
                            searchResults = combinedResults
                            view.showAlbums(searchResults)
                            pageOffset++
                            isLoading = false
                        }, { error ->
                            view.showError(error.message ?: "")
                            isLoading = false
                        }))
    }

    // TODO: add generic error handler to handle common errros in all views?
    override fun loadMore() {
        if (!isLoading) {
            isLoading = true
            subscriptions.add(
                    Single.just(searchResults).concatWith(
                            fetchSearchResultsSinglePage(this.query, pageOffset)).toList()
                            .subscribe({ combinedResults ->
                                searchResults = combinedResults.flatten()
                                pageOffset++
                                view.showAlbums(searchResults)
                                isLoading = false
                            }, { error ->
                                view.showError(error.message ?: "")
                                isLoading = false
                            }))
        }
    }

    override fun albumCardSelected(album: Album, transitionName: String, optionsBundle: Bundle?) {
        view.goToAlbumDetailsScreen(album, transitionName, optionsBundle)
    }
}