package com.example.andrey.lastfmapp.ui.album_details

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.andrey.lastfmapp.GlideApp
import com.example.andrey.lastfmapp.di.AlbumDetailsModule
import com.example.andrey.lastfmapp.di.ApplicationComponent
import com.example.andrey.lastfmapp.di.DaggerAlbumDetailsComponent
import com.example.andrey.lastfmapp.domain.Album
import kotlinx.android.synthetic.main.activity_album_details.*
import javax.inject.Inject
import android.view.View
import com.example.andrey.lastfmapp.R


class AlbumDetailsActivity : AppCompatActivity(), AlbumDetailsContract.View {

    @Inject
    lateinit var presenter: AlbumDetailsContract.Presenter

    private lateinit var listAdapter: TrackRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_details)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        initListAdapter()
        injectDependency()
        presenter.bindView(this)
        presenter.bindData(intent.extras!!.getParcelable(EXTRA_ALBUM_ITEM),
                intent.extras.getString(EXTRA_ALBUM_IMAGE_TRANSITION_NAME))
    }

    private fun initListAdapter() {
        rvTracks.layoutManager = LinearLayoutManager(this)
        listAdapter = TrackRecyclerViewAdapter(this)
        rvTracks.adapter = listAdapter
    }

    private fun injectDependency() {
        val activityComponent = DaggerAlbumDetailsComponent.builder()
                .albumDetailsModule(AlbumDetailsModule(this))
                .applicationComponent((application as ApplicationComponent.ApplicationComponentProvider).appComponent)
                .build()

        activityComponent.inject(this)
    }

    override fun performEnterScreenAnimation(imageUrl: String?, imageTransitionName: String) {
        supportPostponeEnterTransition()
        ivAlbumCover.transitionName = imageTransitionName
        val requestOptions = RequestOptions.placeholderOf(R.drawable.ic_photo)
                .dontTransform()
        GlideApp
                .with(this)
                .load(imageUrl)
                .apply(requestOptions)
                .fitCenter()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                })
                .into(ivAlbumCover)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unbindView()
    }

    override fun setAlbumName(albumName: String) {
        toolbar.title = albumName
        tvAlbumName.text = albumName
    }

    override fun setArtistName(artistName: String) {
        tvArtistName.text = artistName
    }

    override fun setTrackTitles(tracks: List<String>) {
        listAdapter.setItems(tracks)
        listAdapter.notifyDataSetChanged()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar(showBar: Boolean) {
        progressBar.visibility = if (showBar) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_ALBUM_ITEM = "extra_album_item"
        const val EXTRA_ALBUM_IMAGE_TRANSITION_NAME = "extra_album_transition_name"

        fun getAlbumDetailsIntent(context: Context, album: Album, transitionName: String?): Intent {
            val intent = Intent(context, AlbumDetailsActivity::class.java)
            intent.putExtra(EXTRA_ALBUM_ITEM, album)
            if (transitionName != null) {
                intent.putExtra(EXTRA_ALBUM_IMAGE_TRANSITION_NAME, transitionName)
            }
            return intent
        }
    }
}
