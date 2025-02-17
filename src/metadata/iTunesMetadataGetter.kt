package metadata

import global.getValue
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import playlist.Track

@Suppress("ClassName")
class iTunesMetadataGetter {


    enum class iTunesObjectType(private val str: String) {
        SONG("song"),
        ALBUM("album");

        override fun toString(): String {
            return str
        }
    }

    companion object {

        /// Returns a new Track.Metadata object with updated metadata
        fun getMetadataFromApple(track: Track.Metadata): Track.Metadata {

            // Search Apple Music for matching titles
            val candidates = querySongTitle(track.title)

            // Find an appropriate match
            candidates.forEach { id ->

                val songData = lookupId(id, iTunesObjectType.SONG)
                val albumData = lookupId(songData.getValue("collectionId") ?: return@forEach, iTunesObjectType.ALBUM)

                // Verify that the song and album are from the same artist, this should filter out non-official releases
                val songArtist: String = songData.getValue("artistName") ?: return@forEach
                val albumArtist: String = albumData.getValue("artistName") ?: return@forEach
                if (!(songArtist in albumArtist || albumArtist in songArtist)) return@forEach

                return track.copy(
                    album = songData.getValue("collectionName") ?: track.album,
                    trackNumber = songData.getValue("trackNumber") ?: 1,
                    trackTotal = songData.getValue("trackCount") ?: 1,
                    albumArt = songData.getValue<String>("artworkUrl100")?.replace("100", "1400") ?: ""
                )

            }

            return track

        }

        private fun querySongTitle(query: String): List<Long> {

            val itunesUrl = "https://amp-api-edge.music.apple.com/v1/catalog/jp/search/suggestions?art%5Burl%5D=f&fields%5Balbums%5D=artwork%2Cname%2CplayParams%2Curl%2CartistName&fields%5Bartists%5D=url%2Cname%2Cartwork&format%5Bresources%5D=map&kinds=terms%2CtopResults&l=ja&limit%5Bresults%3Aterms%5D=5&limit%5Bresults%3AtopResults%5D=10&omit%5Bresource%5D=autos&platform=web&term=${query.replace(" ", "%20")}&types=activities%2Calbums%2Cartists%2Ceditorial-items%2Cmusic-movies%2Cmusic-videos%2Cplaylists%2Crecord-labels%2Csongs%2Cstations%2Ctv-episodes&with=naturalLanguage"

            // Query Apple
            var response: String
            runBlocking {
                val client = HttpClient()
                response = client.get(itunesUrl) {
                    headers {
                        append("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:135.0) Gecko/20100101 Firefox/135.0")
                        append("Accept", "*/*")
                        append("Referer", "https://music.apple.com/")
                        append("Authorization", "Bearer eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IldlYlBsYXlLaWQifQ.eyJpc3MiOiJBTVBXZWJQbGF5IiwiaWF0IjoxNzM3NDgwNzIxLCJleHAiOjE3NDQ3MzgzMjEsInJvb3RfaHR0cHNfb3JpZ2luIjpbImFwcGxlLmNvbSJdfQ.n_2fV0lbEYcLIKyt590X3A0oH8VFugS53cmTFiGHYxy1ilDRd-rWp9K1Ka2r5aAf-cRdsMaHx7VZSJ4IwG9UnQ")
                        append("Origin", "https://music.apple.com")
                    }
                }.bodyAsText()
                client.close()
            }

            // Extract JSON objects
            val suggestions = Json.parseToJsonElement(response).jsonObject["results"]!!.jsonObject["suggestions"]!!.jsonArray

            // Extract IDs from JSON
            val idList: List<Long> = suggestions.filter { (it.jsonObject.getValue("kind") ?: "") == "topResults" }
                .map { (it.jsonObject["content"]?.jsonObject?.getValue("id") ?: 0) }


            println("Song ID matches for $query : $idList")

            return idList

        }

        private fun lookupId(id: Long, type: iTunesObjectType): Map<String, JsonElement> {

            val itunesUrl = "https://itunes.apple.com/lookup?id=$id&entity=$type&country=JP"

            var response: String
            runBlocking {
                val client = HttpClient()
                response = client.get(itunesUrl).bodyAsText()
                client.close()
            }

            return Json.parseToJsonElement(response).jsonObject["results"]!!.jsonArray.first().jsonObject

        }

    }

}