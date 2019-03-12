package com.example.andrey.lastfmapp.api.albums

import com.example.andrey.lastfmapp.api.LastfmService
import com.example.andrey.lastfmapp.api.albums.AlbumApiRetrofitInteractor.Companion.FIRST_PAGE_INDEX
import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.domain.AlbumDetails
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.io.IOException

class AlbumApiRetrofitInteractorTest {

    @Mock lateinit var mockLastFmService: LastfmService
    @Mock lateinit var mockAlbumSearchResponseMapper: AlbumSearchToDomainMapper
    @Mock lateinit var mockAlbumDetailsResponseMapper: AlbumDetailsToDomainMapper

    @Fixture lateinit var fixtAlbums: List<Album>
    @Fixture lateinit var fixtAlbumDetails: AlbumDetails
    @Fixture lateinit var fixtAlbumSearchResponseDTO: AlbumSearchResponseDTO
    @Fixture lateinit var fixtAlbumDetailsResponseDTO: AlbumDetailsResponseDTO

    private lateinit var sut: AlbumApiRetrofitInteractor

    private val fixture = JFixture()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        FixtureAnnotations.initFixtures(this)

        sut = AlbumApiRetrofitInteractor(
                mockLastFmService,
                mockAlbumDetailsResponseMapper,
                mockAlbumSearchResponseMapper
        )
    }

    @Test
    fun searchAlbumEmitsSuccess() {
        val fixtQuery = fixture.create(String::class.java)
        val fixtPageOffset = fixture.create(Int::class.java)
        whenever(mockLastFmService.findAlbums(fixtQuery, FIRST_PAGE_INDEX + fixtPageOffset))
                .thenReturn(Single.just(fixtAlbumSearchResponseDTO))
        whenever(mockAlbumSearchResponseMapper.apply(fixtAlbumSearchResponseDTO))
                .thenReturn(fixtAlbums)
        sut.findAlbums(fixtQuery, fixtPageOffset)
                .test()
                .assertValue(fixtAlbums)
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun searchAlbumEmitsErrorIfApiServiceEmitsError() {
        val fixtException = fixture.create(IOException::class.java)
        val fixtQuery = fixture.create(String::class.java)
        val fixtPageOffset = fixture.create(Int::class.java)
        whenever(mockLastFmService.findAlbums(fixtQuery, FIRST_PAGE_INDEX + fixtPageOffset))
                .thenReturn(Single.error(fixtException))
        whenever(mockAlbumSearchResponseMapper.apply(fixtAlbumSearchResponseDTO))
                .thenReturn(fixtAlbums)
        sut.findAlbums(fixtQuery, fixtPageOffset)
                .test()
                .assertNoValues()
                .assertError(fixtException)
                .assertNotComplete()
    }

    @Test
    fun searchAlbumEmitsErrorIfResponseMapperThrows() {
        val fixtException = fixture.create(RuntimeException::class.java)
        val fixtQuery = fixture.create(String::class.java)
        val fixtPageOffset = fixture.create(Int::class.java)
        whenever(mockLastFmService.findAlbums(fixtQuery, FIRST_PAGE_INDEX + fixtPageOffset))
                .thenReturn(Single.just(fixtAlbumSearchResponseDTO))
        whenever(mockAlbumSearchResponseMapper.apply(fixtAlbumSearchResponseDTO))
                .thenThrow(fixtException)
        sut.findAlbums(fixtQuery, fixtPageOffset)
                .test()
                .assertNoValues()
                .assertError(fixtException)
                .assertNotComplete()
    }

    @Test
    fun fetchAlbumDetailsEmitsSuccess() {
        val fixtArtist = fixture.create(String::class.java)
        val fixtAlbum = fixture.create(String::class.java)
        whenever(mockLastFmService.getAlbumDetails(fixtArtist, fixtAlbum))
                .thenReturn(Single.just(fixtAlbumDetailsResponseDTO))
        whenever(mockAlbumDetailsResponseMapper.apply(fixtAlbumDetailsResponseDTO))
                .thenReturn(fixtAlbumDetails)
        sut.getAlbumDetails(fixtArtist, fixtAlbum)
                .test()
                .assertValue(fixtAlbumDetails)
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun fetchAlbumDetailsEmitsErrorIfApiServiceEmitsError() {
        val fixtException = fixture.create(IOException::class.java)

        val fixtArtist = fixture.create(String::class.java)
        val fixtAlbum = fixture.create(String::class.java)
        whenever(mockLastFmService.getAlbumDetails(fixtArtist, fixtAlbum))
                .thenReturn(Single.error(fixtException))
        whenever(mockAlbumDetailsResponseMapper.apply(fixtAlbumDetailsResponseDTO))
                .thenReturn(fixtAlbumDetails)
        sut.getAlbumDetails(fixtArtist, fixtAlbum)
                .test()
                .assertNoValues()
                .assertError(fixtException)
                .assertNotComplete()
    }

    @Test
    fun fetchAlbumDetailsEmitsErrorIfResponseMapperThrows() {
        val fixtException = fixture.create(RuntimeException::class.java)
        val fixtArtist = fixture.create(String::class.java)
        val fixtAlbum = fixture.create(String::class.java)
        whenever(mockLastFmService.getAlbumDetails(fixtArtist, fixtAlbum))
                .thenReturn(Single.just(fixtAlbumDetailsResponseDTO))
        whenever(mockAlbumDetailsResponseMapper.apply(fixtAlbumDetailsResponseDTO))
                .thenThrow(fixtException)
        sut.getAlbumDetails(fixtArtist, fixtAlbum)
                .test()
                .assertNoValues()
                .assertError(fixtException)
                .assertNotComplete()
    }
}
