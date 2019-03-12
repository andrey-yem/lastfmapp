package com.example.andrey.lastfmapp.ui.album_details

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.example.andrey.lastfmapp.LastFmApp
import com.example.andrey.lastfmapp.R
import com.example.andrey.lastfmapp.api.albums.AlbumApiInteractor
import com.example.andrey.lastfmapp.di.ApplicationComponent
import com.example.andrey.lastfmapp.di.ApplicationModule
import com.example.andrey.lastfmapp.di.DaggerApplicationComponent
import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.domain.AlbumDetails
import com.example.andrey.lastfmapp.domain.IAlbumOrchestrator
import com.example.andrey.lastfmapp.ui.ToastMatcher
import com.example.andrey.lastfmapp.utils.IScheduler
import com.example.andrey.lastfmapp.utils.SchedulerImpl
import com.example.andrey.lastfmapp.utils.TestHelper
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
class AlbumDetailsActivityTest {

    @get:Rule
    val testRule = AlbumDetailsActivityRule()

    @Mock lateinit var mockAlbumOrchestrator: IAlbumOrchestrator
    @Fixture lateinit var fixtAlbum: Album
    @Fixture lateinit var fixtTransitionName: String

    private lateinit var testAppComponent: ApplicationComponent
    private lateinit var fixture: JFixture

    val testScheduler = object : IScheduler {
        override fun background(): Scheduler = AndroidSchedulers.mainThread()
        override fun main(): Scheduler = AndroidSchedulers.mainThread()
    }

    inner class AlbumDetailsActivityRule : IntentsTestRule<AlbumDetailsActivity>(AlbumDetailsActivity::class.java, false, false) {
        override fun getActivityIntent(): Intent {
            return AlbumDetailsActivity.getAlbumDetailsIntent(getTargetContext(), fixtAlbum, fixtTransitionName)
        }
    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        fixture = JFixture()
        FixtureAnnotations.initFixtures(this, fixture)

        val app = InstrumentationRegistry.getTargetContext().applicationContext as LastFmApp
        testAppComponent = DaggerApplicationComponent.builder()
                .applicationModule(object : ApplicationModule() {
                    override fun provideAlbumOrchestrator(interactor: AlbumApiInteractor): IAlbumOrchestrator = mockAlbumOrchestrator
                    override fun provideScheduler(scheduler: SchedulerImpl): IScheduler = testScheduler
                }).build()
        app.appComponent = testAppComponent
    }

    @Test
    fun onLoadUpdatesScreenAndFetchedDetails() {
        val fixtAlbumDetails = fixture.create(AlbumDetails::class.java)
        val tracks = (1..6)
                .asSequence()
                .map {
                    AlbumDetails.Track("rank $it", "url $it",
                            "duration $it", "name $it")
                }
                .toList()
        TestHelper.setField(fixtAlbumDetails, "tracks", tracks)

        whenever(mockAlbumOrchestrator.getAlbumDetails(fixtAlbum.artist, fixtAlbum.name))
                .thenReturn(Single.just(fixtAlbumDetails))

        testRule.launchActivity(null)

        onView(ViewMatchers.withId(R.id.tvArtistName)).check(matches(withText(fixtAlbum.artist)))
        onView(ViewMatchers.withId(R.id.tvAlbumName)).check(matches(withText(fixtAlbum.name)))
        verify(mockAlbumOrchestrator).getAlbumDetails(fixtAlbum.artist, fixtAlbum.name)
        onView(withId(R.id.rvTracks)).check { view, _ ->
            val recyclerView = view as RecyclerView
            val items = (recyclerView.adapter as TrackRecyclerViewAdapter).getItems()
            Assert.assertEquals(items, fixtAlbumDetails.tracks.map { it.name })
        }
    }

    @Test
    fun errorsAreShownInToast() {
        val fixtException = Exception("exception test message")

        whenever(mockAlbumOrchestrator.getAlbumDetails(any(), any()))
                .thenReturn(Single.error(fixtException))

        testRule.launchActivity(null)

        onView(withText(fixtException.message)).inRoot(ToastMatcher())
                .check(matches(isDisplayed()))
    }
}
