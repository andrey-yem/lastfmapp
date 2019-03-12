package com.example.andrey.lastfmapp.api.albums

import com.example.andrey.lastfmapp.domain.AlbumDetails
import com.example.andrey.lastfmapp.utils.TestHelper
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import junitparams.JUnitParamsRunner
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnitParamsRunner::class)
class AlbumMapperTest {

    private lateinit var sut: AlbumMapper
    private lateinit var fixture: JFixture

    @Before
    fun setUp() {
        fixture = JFixture()
        FixtureAnnotations.initFixtures(this, fixture)
        MockitoAnnotations.initMocks(this)
        sut = AlbumMapper()
    }

    @Test
    fun map() {
        // setup
        val imagesMap = mapOf(
                AlbumDetails.IMAGE_RESOLUTION_SMALL to "image1",
                AlbumDetails.IMAGE_RESOLUTION_MEDIUM to "image2",
                AlbumDetails.IMAGE_RESOLUTION_LARGE to "imageL")
        val imagesDto = imagesMap.map {
            ImageDTO().also { dto -> dto.size = it.key; dto.url = it.value }
        }
        val fixtAlbumDto = fixture.create(AlbumSearchResponseDTO.AlbumSummaryDTO::class.java)
        TestHelper.setField(fixtAlbumDto, "image", imagesDto)

        // invoke
        val spySut = Mockito.spy(sut)
        val actual = spySut.apply(fixtAlbumDto)

        // verify
        assertEquals(fixtAlbumDto.artist, actual.artist)
        assertEquals(fixtAlbumDto.name, actual.name)
        assertEquals(fixtAlbumDto.mbid, actual.mbid)
        assertEquals(imagesMap, actual.images)
    }
}