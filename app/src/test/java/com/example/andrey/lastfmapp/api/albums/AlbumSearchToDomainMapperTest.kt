package com.example.andrey.lastfmapp.api.albums

import com.example.andrey.lastfmapp.domain.Album
import com.example.andrey.lastfmapp.utils.TestHelper
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.nhaarman.mockitokotlin2.whenever
import junitparams.JUnitParamsRunner
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnitParamsRunner::class)
class AlbumSearchToDomainMapperTest {

    @Mock lateinit var mockAlbumMapper: AlbumMapper

    private lateinit var sut: AlbumSearchToDomainMapper
    private lateinit var fixture: JFixture

    @Before
    fun setUp() {
        fixture = JFixture()
        FixtureAnnotations.initFixtures(this, fixture)
        MockitoAnnotations.initMocks(this)
        sut = AlbumSearchToDomainMapper(mockAlbumMapper)
    }

    @Test
    fun map() {
        // setup
        val fixtAlbum1 = fixture.create(Album::class.java)
        val fixtAlbum2 = fixture.create(Album::class.java)
        val fixtAlbum3 = fixture.create(Album::class.java)
        val fixtAlbum1Dto = fixture.create(AlbumSearchResponseDTO.AlbumSummaryDTO::class.java)
        val fixtAlbum2Dto = fixture.create(AlbumSearchResponseDTO.AlbumSummaryDTO::class.java)
        val fixtAlbum3Dto = fixture.create(AlbumSearchResponseDTO.AlbumSummaryDTO::class.java)
        val fixtAlbumSearchResponse = fixture.create(AlbumSearchResponseDTO::class.java)
        TestHelper.setField(fixtAlbumSearchResponse.results!!.matches!!,
                "albums",
                listOf(fixtAlbum1Dto, fixtAlbum2Dto, fixtAlbum3Dto))
        whenever(mockAlbumMapper.apply(fixtAlbum1Dto)).thenReturn(fixtAlbum1)
        whenever(mockAlbumMapper.apply(fixtAlbum2Dto)).thenReturn(fixtAlbum2)
        whenever(mockAlbumMapper.apply(fixtAlbum3Dto)).thenReturn(fixtAlbum3)

        val spySut = Mockito.spy(sut)

        // invoke
        val actual = spySut.apply(fixtAlbumSearchResponse)

        // verify
        assertEquals(listOf(fixtAlbum1, fixtAlbum2, fixtAlbum3), actual)
    }
}