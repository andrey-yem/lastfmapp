package com.example.andrey.lastfmapp.api.albums

import com.example.andrey.lastfmapp.domain.AlbumDetails
import com.example.andrey.lastfmapp.domain.AlbumDetails.Companion.IMAGE_RESOLUTION_LARGE
import com.example.andrey.lastfmapp.domain.AlbumDetails.Companion.IMAGE_RESOLUTION_MEDIUM
import com.example.andrey.lastfmapp.domain.AlbumDetails.Companion.IMAGE_RESOLUTION_SMALL
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
class AlbumDetailsToDomainMapperTest {

    private lateinit var sut: AlbumDetailsToDomainMapper
    private lateinit var fixture: JFixture

    @Before
    fun setUp() {
        fixture = JFixture()
        FixtureAnnotations.initFixtures(this, fixture)
        MockitoAnnotations.initMocks(this)
        sut = AlbumDetailsToDomainMapper()
    }

    @Test
    fun map() {
        // setup
        val tracks = (1..3).asSequence()
                .map { AlbumDetails.Track("rank $it", "url $it", "duration $it", "name $it") }
                .toList()
        val tracksDto = tracks.map {
            AlbumDetailsResponseDTO.TrackDTO().also { albumDto ->
                albumDto.url = it.url
                albumDto.duration = it.duration
                albumDto.name = it.name
                albumDto.attr = AlbumDetailsResponseDTO.RankDTO().also { rankDto -> rankDto.rank = it.rank }
            }
        }
        val imagesMap = mapOf(
                IMAGE_RESOLUTION_SMALL to "image1",
                IMAGE_RESOLUTION_MEDIUM to "image2",
                IMAGE_RESOLUTION_LARGE to "imageL")
        val imagesDto = imagesMap.map {
            ImageDTO().also { dto ->
                dto.size = it.key
                dto.url = it.value
            }
        }
        val fixtAlbumDetailsDto = fixture.create(AlbumDetailsResponseDTO::class.java)
        TestHelper.setField(fixtAlbumDetailsDto.album, "image", imagesDto)
        TestHelper.setField(fixtAlbumDetailsDto.album, "tracks",
                AlbumDetailsResponseDTO.TracksDTO().also { it.track = tracksDto })
        val fixtWikiEntry = AlbumDetails.WikiEntry(
                fixtAlbumDetailsDto.album.wiki!!.content,
                fixtAlbumDetailsDto.album.wiki!!.published,
                fixtAlbumDetailsDto.album.wiki!!.summary)

        // invoke
        val spySut = Mockito.spy(sut)
        val actual = spySut.apply(fixtAlbumDetailsDto)

        // verify
        assertEquals(fixtAlbumDetailsDto.album.name, actual.name)
        assertEquals(fixtAlbumDetailsDto.album.artist, actual.artist)
        assertEquals(fixtAlbumDetailsDto.album.mbid, actual.mbid)
        assertEquals(fixtAlbumDetailsDto.album.url, actual.url)
        assertEquals(fixtWikiEntry, actual.wiki)
        assertEquals(imagesMap, actual.images)
        assertEquals(tracks, actual.tracks)
    }
}