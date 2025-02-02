package metadata

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import playlist.Track


/// Returns a new Track.Metadata object with updated metadata
fun getMetadataFromApple(track: Track.Metadata): Track.Metadata {

    // Get metadata
    val itunesUrl = "https://itunes.apple.com/search?term=${
        track.title.replace(' ', '+') +
        if(!track.isCover) '+' + track.artists.first().replace(' ', '+') else ""
    }&entity=song&limit=1&country=jp"


    var response: String
    runBlocking {
        val client = HttpClient()
        response = client.get(itunesUrl).bodyAsText()
        client.close()
    }

    println(itunesUrl)
    println(response)
    println("meow")

    val jsonResponse = Json.parseToJsonElement(response).jsonObject["results"]!!.jsonArray.first().jsonObject

    val album = jsonResponse.jsonObject["collectionName"]?.jsonPrimitive?.content ?: track.title
    val trackNumber = jsonResponse.jsonObject["trackNumber"]?.jsonPrimitive?.int ?: 1
    val trackTotal = jsonResponse.jsonObject["trackCount"]?.jsonPrimitive?.int ?: 1
    val coverArt = jsonResponse.jsonObject["artworkUrl100"]?.jsonPrimitive?.content?.replace("100", "1400") ?: ""

    println("album $album")
    println("trackNumber $trackNumber")
    println("coverArt $coverArt")

    return track.copy(album = album, trackNumber = trackNumber, trackTotal = trackTotal, albumArt = coverArt)

}
