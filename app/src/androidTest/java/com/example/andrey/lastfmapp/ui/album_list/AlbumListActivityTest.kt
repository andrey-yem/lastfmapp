package com.example.andrey.lastfmapp.ui.album_list

import android.app.Instrumentation
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
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
import com.example.andrey.lastfmapp.domain.IAlbumOrchestrator
import com.example.andrey.lastfmapp.ui.ToastMatcher
import com.example.andrey.lastfmapp.ui.album_details.AlbumDetailsActivity
import com.example.andrey.lastfmapp.ui.album_details.AlbumDetailsActivity.Companion.EXTRA_ALBUM_ITEM
import com.example.andrey.lastfmapp.utils.IScheduler
import com.example.andrey.lastfmapp.utils.SchedulerImpl
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.utility.SpecimenType
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_album_list.*
import org.hamcrest.Matchers.allOf
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
class AlbumListActivityTest {

    @get:Rule
    val testRule = IntentsTestRule<AlbumListActivity>(AlbumListActivity::class.java, false, false)

    @Mock lateinit var mockAlbumOrchestrator: IAlbumOrchestrator
    private lateinit var testAppComponent: ApplicationComponent
    private lateinit var fixture: JFixture

    val testScheduler = object : IScheduler {
        override fun background(): Scheduler = AndroidSchedulers.mainThread()
        override fun main(): Scheduler = AndroidSchedulers.mainThread()
    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        fixture = JFixture()

        val app = InstrumentationRegistry.getTargetContext().applicationContext as LastFmApp

        testAppComponent = DaggerApplicationComponent.builder()
                .applicationModule(object : ApplicationModule() {
                    override fun provideAlbumOrchestrator(interactor: AlbumApiInteractor): IAlbumOrchestrator = mockAlbumOrchestrator
                    override fun provideScheduler(scheduler: SchedulerImpl): IScheduler = testScheduler
                }).build()

        app.appComponent = testAppComponent
    }

    @Test
    fun searchResultsAreShowOnScreen() {
        val query = "query"
        fixture.customise().repeatCount(20)
        val fixtAlbums = fixture.create<List<Album>>(object : SpecimenType<List<@JvmSuppressWildcards Album>>() {})

        whenever(mockAlbumOrchestrator.findAlbums(any(), any()))
                .thenReturn(Single.just(fixtAlbums))

        testRule.launchActivity(null)

        onView(ViewMatchers.withId(R.id.searchView)).perform(ViewActions.click())
        onView(ViewMatchers.withId(android.support.design.R.id.search_src_text)).perform(ViewActions.typeText(query))
        onView(ViewMatchers.withId(R.id.rvAlbums)).check(albumListContainsExpectedItems(fixtAlbums))
    }

    @Test
    fun moreResultsDisplayedOnScroll() {
        val query = "a"
        fixture.customise().repeatCount(20)
        val fixtAlbumsPage1 = fixture.create<List<Album>>(object : SpecimenType<List<@JvmSuppressWildcards Album>>() {})
        val fixtAlbumsPage2 = fixture.create<List<Album>>(object : SpecimenType<List<@JvmSuppressWildcards Album>>() {})

        whenever(mockAlbumOrchestrator.findAlbums(query, 0))
                .thenReturn(Single.just(fixtAlbumsPage1))
        whenever(mockAlbumOrchestrator.findAlbums(query, 1))
                .thenReturn(Single.just(fixtAlbumsPage2))

        val activity = testRule.launchActivity(null)

        onView(ViewMatchers.withId(R.id.searchView)).perform(ViewActions.click())
        onView(ViewMatchers.withId(android.support.design.R.id.search_src_text)).perform(ViewActions.typeText(query))

        onView(withId(R.id.rvAlbums))
                .check(albumListContainsExpectedItems(fixtAlbumsPage1))
        onView(withId(R.id.rvAlbums))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(activity.rvAlbums.adapter.itemCount - 1))
        onView(withId(R.id.rvAlbums))
                .check(albumListContainsExpectedItems(listOf(fixtAlbumsPage1, fixtAlbumsPage2).flatten()))
    }

    @Test
    fun errorsAreShownInToast() {
        val query = "a"
        val fixtException = Exception("exception test message")

        whenever(mockAlbumOrchestrator.findAlbums(any(), any()))
                .thenReturn(Single.error(fixtException))

        testRule.launchActivity(null)

        onView(ViewMatchers.withId(R.id.searchView)).perform(ViewActions.click())
        onView(ViewMatchers.withId(android.support.design.R.id.search_src_text)).perform(ViewActions.typeText(query))

        onView(withText(fixtException.message)).inRoot(ToastMatcher())
                .check(matches(isDisplayed()))
    }

    @Test
    fun navigatesToDetailsScreenOnClick() {
        val query = "q"
        val fixtAlbums = fixture.create<List<Album>>(object : SpecimenType<List<@JvmSuppressWildcards Album>>() {})
        val fixtSelectedAlbum = fixtAlbums[1]

        whenever(mockAlbumOrchestrator.findAlbums(any(), any()))
                .thenReturn(Single.just(fixtAlbums))

        testRule.launchActivity(null)

        onView(ViewMatchers.withId(R.id.searchView)).perform(ViewActions.click())
        onView(ViewMatchers.withId(android.support.design.R.id.search_src_text)).perform(ViewActions.typeText(query))

        val expectedIntent = allOf(
                hasComponent(AlbumDetailsActivity::class.java.name),
                hasExtra(EXTRA_ALBUM_ITEM, fixtSelectedAlbum))

        // return stub result when the intent is fired
        intending(expectedIntent).respondWith(Instrumentation.ActivityResult(0, null))

        onView(ViewMatchers.withId(R.id.rvAlbums))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(1, ViewActions.click()))

        intended(expectedIntent)
    }

    private fun albumListContainsExpectedItems(expected :List<Album>) : ViewAssertion {
        return ViewAssertion { view, _ ->
            val recyclerView = view as RecyclerView
            val items = (recyclerView.adapter as AlbumRecyclerViewAdapter).getItems()
            Assert.assertEquals(items, expected)
        }
    }
}
