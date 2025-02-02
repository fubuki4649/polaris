package playlist

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import metadata.LLMMetadataGetter
import metadata.getMetadataFromApple
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Pattern

class Playlist(private val playlistLink: String, private val workPath: String = "${System.getProperty("user.home")}/.cache/polaris", overwrite: Boolean = false) {

    private val ytDlpCommand = "yt-dlp --quiet -f bestaudio/best --extract-audio --audio-format aac --audio-quality 0 -o %(uploader)s<DELIMITER>%(title)s --no-playlist --paths $workPath/audio $playlistLink"

    private val tracks: MutableList<Track> = mutableListOf()

    init {

        // Handle non-empty work path
        if(File(workPath).exists()) {

            if(!overwrite) {
                println("$workPath already exists. Overwrite? (Y/n)")
                if((readlnOrNull()?.getOrNull(0)?.lowercaseChar() ?: 'y') == 'n') throw Exception("Directory already exists.")
            }

            println("Deleting non-empty directory $workPath")
            File(workPath).deleteRecursively()

        }

    }

    /// Extracts JSON data from LLM response
    private fun extractJsonContent(text: String): String {
        val pattern = Pattern.compile("```json(.*?)```", Pattern.DOTALL)
        val matcher = pattern.matcher(text)
        return if (matcher.find())  matcher.group(1) else ""
    }

    /// Download playlist songs from YouTube
    fun download(verbose: Boolean = true) {

        println("Downloading contents from $playlistLink")
        println("Any unavailable tracks will be skipped, use --verbose to see more")

        // Run yt-dlp
        val destination = if(verbose) ProcessBuilder.Redirect.INHERIT else ProcessBuilder.Redirect.DISCARD
        ProcessBuilder(ytDlpCommand.split(" "))
            .redirectOutput(destination)
            .redirectError(destination)
            .start()
            .waitFor()

        // Populate `tracks: MutableList<Track>`
        val audioPath = File("$workPath/audio")
        audioPath.walk().forEach {
            tracks.add(Track(it.toString()))
        }

        println("Finished downloading $playlistLink")

    }

    private fun downloadArtworks(verbose: Boolean = true) {

        val albumArtPath = File("$workPath/albumart")
        if(!albumArtPath.exists()) albumArtPath.mkdirs()

        tracks.forEach { track ->
            // Download album artwork to $workPath/albumart/$ARTIST-$TRACKNAME.jpg
            runBlocking {
                val client = HttpClient()
                val imageData = HttpClient().get(track.metadata.albumArt).readRawBytes()
                File(albumArtPath, "${track.metadata.artists}-${track.metadata.title}.jpg").writeBytes(imageData)
                client.close()
            }
            // Cache the path for the album artwork
            track.albumArtPath = "$workPath/albumart/${track.metadata.artists}-${track.metadata.title}.jpg"
        }

    }

    fun populateMetadata(verbose: Boolean = true) {

        println("Getting metadata for $playlistLink from ${global.languageModel}")

        // Get metadata for each track
        LLMMetadataGetter.getMetadataFromLLM(tracks.map { it.videoName }).mapIndexed { index, metadata ->
            tracks[index].metadata = getMetadataFromApple(metadata)
        }

        println("Downloading album artworks")

        // Get album art for each track
        downloadArtworks(verbose)

        println("Writing metadata to file")

        // Set logging level and write metadata to file
        Logger.getLogger("org.jaudiotagger").level = if(verbose) Level.CONFIG else Level.OFF
        tracks.forEach { it.writeMetadata() }

        println("Finished setting metadata")

    }

}