package com.example.andrey.lastfmapp.ui.album_details

import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.domain.AlbumDetails
import com.example.andrey.lastfmapp.domain.AlbumDetails.Companion.IMAGE_RESOLUTION_LARGE
import com.example.andrey.lastfmapp.domain.AlbumDetails.Companion.IMAGE_RESOLUTION_MEDIUM
import com.example.andrey.lastfmapp.domain.AlbumDetails.Companion.IMAGE_RESOLUTION_SMALL
import com.example.andrey.lastfmapp.domain.IAlbumOrchestrator
import com.example.andrey.lastfmapp.utils.TestHelper
import com.example.andrey.lastfmapp.utils.TestSchedulerImpl
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class AlbumDetailsPresenterTest {

    @Mock
    lateinit var mockAlbumOrchestrator: IAlbumOrchestrator
    @Mock
    lateinit var mockView: AlbumDetailsContract.View
    @Fixture
    lateinit var fixtAlbumDetails: AlbumDetails

    private val testScheduler: TestSchedulerImpl = TestSchedulerImpl()
    private lateinit var sut: AlbumDetailsPresenter

    private val fixture = JFixture()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        FixtureAnnotations.initFixtures(this)
        sut = AlbumDetailsPresenter(mockAlbumOrchestrator, testScheduler)
    }

    @Test
    fun bindDataUpdatesScreenAndFetchesAlbumDetails() {
        val fixtAlbum = fixture.create(Album::class.java)
        val fixtAnimationData = fixture.create(String::class.java)

        val imagesList = mapOf(
                IMAGE_RESOLUTION_SMALL to "imageUrlS",
                IMAGE_RESOLUTION_MEDIUM to "imageUrlM",
                IMAGE_RESOLUTION_LARGE to "imageUrlL")
        val prefrableImageUrl = imagesList[IMAGE_RESOLUTION_LARGE]

        val trackList = (1..6).asSequence()
                .map { AlbumDetails.Track("$it", "url $it", "duration $it", "name $it") }
                .toList()
        val trackTitles = trackList.map { it.name }

        TestHelper.setField(fixtAlbumDetails, "tracks", trackList)
        TestHelper.setField(fixtAlbum, "images", imagesList)

        whenever(mockAlbumOrchestrator.getAlbumDetails(fixtAlbum.artist, fixtAlbum.name))
                .thenReturn(Single.just(fixtAlbumDetails))

        sut.bindView(mockView)
        sut.bindData(fixtAlbum, fixtAnimationData)

        val trackListCaptor = argumentCaptor<List<String>>()

        inOrder(mockView, mockAlbumOrchestrator) {
            verify(mockView).setAlbumName(fixtAlbum.name)
            verify(mockView).setArtistName(fixtAlbum.artist)
            verify(mockView).performEnterScreenAnimation(prefrableImageUrl, fixtAnimationData)
            verify(mockView).showProgressBar(true)
            verify(mockAlbumOrchestrator).getAlbumDetails(fixtAlbum.artist, fixtAlbum.name)
            verify(mockView).setTrackTitles(trackListCaptor.capture())
            assertEquals(trackListCaptor.lastValue, trackTitles)
            verify(mockView).showProgressBar(false)
        }
    }

    @Test
    fun noPreferableResolutionsForAlbumCoverAvailable() {
        val fixtAlbum = fixture.create(Album::class.java)
        val fixtAnimationData = fixture.create(String::class.java)

        val albumCoverUrl = "albumCoverUrl"
        val imagesList = mapOf("newResolution" to albumCoverUrl)
        TestHelper.setField(fixtAlbum, "images", imagesList)

        whenever(mockAlbumOrchestrator.getAlbumDetails(any(), any()))
                .thenReturn(Single.just(fixtAlbumDetails))

        sut.bindView(mockView)
        sut.bindData(fixtAlbum, fixtAnimationData)
        verify(mockView).performEnterScreenAnimation(albumCoverUrl, fixtAnimationData)
    }

    @Test
    fun noImagesForAlbumCover() {
        val fixtAlbum = fixture.create(Album::class.java)
        val fixtAnimationData = fixture.create(String::class.java)

        val imagesList = emptyMap<String, String>()
        TestHelper.setField(fixtAlbum, "images", imagesList)

        whenever(mockAlbumOrchestrator.getAlbumDetails(any(), any()))
                .thenReturn(Single.just(fixtAlbumDetails))

        sut.bindView(mockView)
        sut.bindData(fixtAlbum, fixtAnimationData)
        verify(mockView).performEnterScreenAnimation(null, fixtAnimationData)
    }

    @Test
    fun searchYieldsErrorIfOrchestratorEmitsError() {
        val fixtException = fixture.create(Exception::class.java)
        val fixtAlbum = fixture.create(Album::class.java)
        val fixtAnimationData = fixture.create(String::class.java)

        whenever(mockAlbumOrchestrator.getAlbumDetails(any(), any()))
                .thenReturn(Single.error(fixtException))

        sut.bindView(mockView)
        sut.bindData(fixtAlbum, fixtAnimationData)

        verify(mockView).showError(fixtException.message ?: "")
    }

    @Test
    fun unbindDisposesSubscriptions() {
        val mockSubscription: Disposable = mock()
        sut.subscriptions.add(mockSubscription)
        sut.unbindView()
        verify(mockSubscription).dispose()
        Assert.assertEquals(sut.subscriptions.size(), 0)
    }
}
