package com.example.andrey.lastfmapp.ui.album_list

import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.andrey.lastfmapp.R
import com.example.andrey.lastfmapp.di.AlbumListModule
import com.example.andrey.lastfmapp.di.ApplicationComponent
import com.example.andrey.lastfmapp.di.DaggerAlbumListComponent
import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.ui.album_details.AlbumDetailsActivity
import kotlinx.android.synthetic.main.activity_album_list.*
import javax.inject.Inject

class AlbumListActivity : AppCompatActivity(), AlbumListContract.View, AlbumRecyclerViewAdapter.ItemClickListener {

    @Inject
    lateinit var presenter: AlbumListContract.Presenter

    private lateinit var listAdapter: AlbumRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_list)
        initSearchView()
        initListAdapter()
        injectDependency()
        presenter.bindView(this)
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.searchQueryUpdated(newText ?: "")
                return true
            }
        })
    }

    private fun initListAdapter() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvAlbums.layoutManager = layoutManager
        listAdapter = AlbumRecyclerViewAdapter(this)
        listAdapter.setClickListener(this)
        rvAlbums.adapter = listAdapter
        /* Trigger autoload when approaching end of list */
        rvAlbums.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemIndex = (rvAlbums.layoutManager as StaggeredGridLayoutManager)
                        .findLastVisibleItemPositions(null).max()!!
                if (lastVisibleItemIndex + RECYCLERVIEW_AUTOLOAD_THRESHOLD > listAdapter.itemCount) {
                    presenter.loadMore()
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }

    private fun injectDependency() {
        val activityComponent = DaggerAlbumListComponent.builder()
                .albumListModule(AlbumListModule(this))
                .applicationComponent((application as ApplicationComponent.ApplicationComponentProvider).appComponent)
                .build()
        activityComponent.inject(this)
    }

    override fun showAlbums(albums: List<Album>) {
        listAdapter.setItems(albums)
        listAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(view: View, position: Int, item: Album, sharedImageView: ImageView) {
        val transitionName = ViewCompat.getTransitionName(sharedImageView)
        val optionsBundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                sharedImageView,
                transitionName).toBundle()
        presenter.albumCardSelected(item, transitionName, optionsBundle)
    }

    override fun goToAlbumDetailsScreen(
            album: Album, transitionName: String, optionsBundle : Bundle?) {
        val intent = AlbumDetailsActivity.getAlbumDetailsIntent(this, album, transitionName)
        startActivity(intent, optionsBundle)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar(showBar: Boolean) {
        progressBar.visibility = if (showBar) View.VISIBLE else View.GONE
    }

    companion object {
        @VisibleForTesting
        val RECYCLERVIEW_AUTOLOAD_THRESHOLD = 10
    }
}
