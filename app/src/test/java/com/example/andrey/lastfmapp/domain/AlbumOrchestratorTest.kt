package com.example.andrey.lastfmapp.domain

import com.example.andrey.lastfmapp.api.albums.AlbumApiInteractor
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.IOException

class AlbumOrchestratorTest {

    @Mock lateinit var mockApiInteractor: AlbumApiInteractor

    @Fixture lateinit var fixtAlbums: List<Album>
    @Fixture lateinit var fixtAlbumDetails: AlbumDetails

    @InjectMocks lateinit var sut: AlbumOrchestrator
    private val fixture = JFixture()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        FixtureAnnotations.initFixtures(this)
    }

    @Test
    fun searchAlbumEmitsSuccess() {
        val fixtQuery = fixture.create(String::class.java)
        val fixtPageOffset = fixture.create(Int::class.java)
        whenever(mockApiInteractor.findAlbums(fixtQuery,  fixtPageOffset))
                .thenReturn(Single.just(fixtAlbums))
        sut.findAlbums(fixtQuery, fixtPageOffset)
                .test()
                .assertValue(fixtAlbums)
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun searchAlbumEmitsErrorIfInteractorEmitsError() {
        val fixtException = fixture.create(IOException::class.java)
        val fixtQuery = fixture.create(String::class.java)
        val fixtPageOffset = fixture.create(Int::class.java)
        whenever(mockApiInteractor.findAlbums(fixtQuery,  fixtPageOffset))
                .thenReturn(Single.error(fixtException))
        sut.findAlbums(fixtQuery, fixtPageOffset)
                .test()
                .assertNoValues()
                .assertError(fixtException)
                .assertNotComplete()
    }

    @Test
    fun getAlbumDetailsEmitsSuccess() {
        val fixtArtist = fixture.create(String::class.java)
        val fixtAlbum = fixture.create(String::class.java)
        whenever(mockApiInteractor.getAlbumDetails(fixtArtist, fixtAlbum))
                .thenReturn(Single.just(fixtAlbumDetails))
        sut.getAlbumDetails(fixtArtist, fixtAlbum)
                .test()
                .assertValue(fixtAlbumDetails)
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun fetchAlbumDetailsEmitsErrorIfInteractorEmitsError() {
        val fixtException = fixture.create(IOException::class.java)
        val fixtArtist = fixture.create(String::class.java)
        val fixtAlbum = fixture.create(String::class.java)
        whenever(mockApiInteractor.getAlbumDetails(fixtArtist, fixtAlbum))
                .thenReturn(Single.error(fixtException))
        sut.getAlbumDetails(fixtArtist, fixtAlbum)
                .test()
                .assertNoValues()
                .assertError(fixtException)
                .assertNotComplete()
    }
}
