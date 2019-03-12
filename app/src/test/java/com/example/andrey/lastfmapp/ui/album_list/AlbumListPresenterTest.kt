package com.example.andrey.lastfmapp.ui.album_list

import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.domain.IAlbumOrchestrator
import com.example.andrey.lastfmapp.utils.TestSchedulerImpl
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.flextrade.jfixture.utility.SpecimenType
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations


class AlbumListPresenterTest {

    @Mock lateinit var mockAlbumOrchestrator: IAlbumOrchestrator
    @Mock lateinit var mockView: AlbumListContract.View
    @Fixture lateinit var fixtAlbums: List<Album>

    private val testScheduler: TestSchedulerImpl = TestSchedulerImpl()
    lateinit var sut: AlbumListPresenter

    private val fixture = JFixture()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        FixtureAnnotations.initFixtures(this)
        sut = AlbumListPresenter(mockAlbumOrchestrator, testScheduler)
    }

    @Test
    fun progressBarDisplayedWhilstLoading() {
        val fixtQuery = fixture.create(String::class.java)

        whenever(mockAlbumOrchestrator.findAlbums(eq(fixtQuery), any()))
                .thenReturn(Single.just(fixtAlbums))

        sut.bindView(mockView)
        sut.searchQueryUpdated(fixtQuery)

        inOrder(mockView, mockAlbumOrchestrator) {
            verify(mockView).showProgressBar(true)
            verify(mockAlbumOrchestrator).findAlbums(eq(fixtQuery), any())
            verify(mockView).showAlbums(eq(fixtAlbums))
            verify(mockView).showProgressBar(false)
        }
    }

    @Test
    fun emptyQueryYieldsEmptyList() {
        val fixtQuery = ""
        val result = emptyList<Album>()

        sut.bindView(mockView)
        sut.searchQueryUpdated(fixtQuery)

        verify(mockAlbumOrchestrator, never()).findAlbums(any(), any())
        verify(mockView).showAlbums(result)
    }

    @Test
    fun loadMoreProducesNewPages() {
        val fixtQuery = fixture.create(String::class.java)
        val fixtAlbumsPage1 = fixture.create<List<Album>>(object : SpecimenType<List<@JvmSuppressWildcards Album>>() {})
        val fixtAlbumsPage2 = fixture.create<List<Album>>(object : SpecimenType<List<@JvmSuppressWildcards Album>>() {})
        val fixtAlbumsPage3 = fixture.create<List<Album>>(object : SpecimenType<List<@JvmSuppressWildcards Album>>() {})

        whenever(mockAlbumOrchestrator.findAlbums(fixtQuery, 0))
                .thenReturn(Single.just(fixtAlbumsPage1))
        whenever(mockAlbumOrchestrator.findAlbums(fixtQuery, 1))
                .thenReturn(Single.just(fixtAlbumsPage2))
        whenever(mockAlbumOrchestrator.findAlbums(fixtQuery, 2))
                .thenReturn(Single.just(fixtAlbumsPage3))

        val resultListCaptor = argumentCaptor<List<Album>>()
        sut.bindView(mockView)
        sut.searchQueryUpdated(fixtQuery)

        inOrder(mockView, mockAlbumOrchestrator) {
            val result = fixtAlbumsPage1.toMutableList()
            verify(mockAlbumOrchestrator).findAlbums(fixtQuery, 0)
            verify(mockView).showAlbums(resultListCaptor.capture())
            assertEquals(resultListCaptor.lastValue, result)

            sut.loadMore()

            result.addAll(fixtAlbumsPage2)
            verify(mockAlbumOrchestrator).findAlbums(fixtQuery, 1)
            verify(mockView).showAlbums(resultListCaptor.capture())
            assertEquals(resultListCaptor.lastValue, result)

            sut.loadMore()

            result.addAll(fixtAlbumsPage3)
            verify(mockAlbumOrchestrator).findAlbums(fixtQuery, 2)
            verify(mockView).showAlbums(resultListCaptor.capture())
            assertEquals(resultListCaptor.lastValue, result)
        }
        verifyNoMoreInteractions(mockAlbumOrchestrator)
    }

    @Test
    fun searchYieldsErrorIfOrchestratorEmitsError() {
        val fixtException = fixture.create(Exception::class.java)
        val fixtQuery = fixture.create(String::class.java)

        whenever(mockAlbumOrchestrator.findAlbums(eq(fixtQuery), any()))
                .thenReturn(Single.error(fixtException))

        sut.bindView(mockView)
        sut.searchQueryUpdated(fixtQuery)

        verify(mockView).showError(fixtException.message?:"")
    }

    @Test
    fun navigatesToDetaisScreenOnCardClick() {
        val fixtAlbum = fixture.create(Album::class.java)
        val fixtTransition = fixture.create(String::class.java)
        sut.bindView(mockView)
        sut.albumCardSelected(fixtAlbum, fixtTransition, null)
        verify(mockView).goToAlbumDetailsScreen(fixtAlbum, fixtTransition, null)
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
