package playlist

import global.SongMetadataGetter
import kotlinx.serialization.json.Json
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.io.path.*
import java.util.regex.Pattern

class Playlist(private val playlistLink: String, private val workPath: String = "${System.getProperty("user.home")}/.cache/polaris", overwrite: Boolean = false) {

    private val ytDlpCommand = "yt-dlp --quiet -f bestaudio/best --extract-audio --audio-format aac --audio-quality 0 -o %(uploader)s<DELIMITER>%(title)s --no-playlist --paths $workPath/audio $playlistLink"

    private val tracks: MutableList<Track> = mutableListOf()

    init {

        // Handle non-empty work path
        if(Path(workPath).exists()) {

            if(!overwrite) {
                println("$workPath already exists. Overwrite? (Y/n)")
                if((readlnOrNull()?.getOrNull(0)?.lowercaseChar() ?: 'y') == 'n') throw Exception("Directory already exists.")
            }

            println("Deleting non-empty directory $workPath")
            @OptIn(ExperimentalPathApi::class)
            Path(workPath).deleteRecursively()

        }

    }

    /// Extracts JSON data from LLM response
    private fun extractJsonContent(text: String): String {
        val pattern = Pattern.compile("```json(.*?)```", Pattern.DOTALL)
        val matcher = pattern.matcher(text)
        return if (matcher.find())  matcher.group(1) else ""
    }

    /// Download playlist songs from YouTube
    @OptIn(ExperimentalPathApi::class)
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
        val audioPath = Path("$workPath/audio")
        audioPath.walk().forEach {
            tracks.add(Track(it.toString()))
        }

        println("Finished downloading $playlistLink")

    }

    fun populateMetadata(verbose: Boolean = true) {

        println("Getting metadata for $playlistLink from ${global.languageModel}")

        // Send an LLM query and get the response as json
        val jsonRawResponse = SongMetadataGetter.getMetadata(tracks.map {
            it.videoName
        })

        println("Parsing JSON response from ${global.languageModel}${if(verbose)":" else ""}")

        // Isolate only the json content from the LLM response
        val jsonData = extractJsonContent(jsonRawResponse).replace("\\n", "\n").replace("\\\"", "\"")

        // Log LLM response
        if(verbose) {
            if(jsonData.isNotBlank()) println(jsonData) else println(jsonRawResponse)
        }

        // Deserialize json and set metadata for each track
        Json.decodeFromString<List<Track.Metadata>>(jsonData).mapIndexed { index, metadata ->
            tracks[index].metadata = metadata
        }

        println("Writing metadata to file")

        // Set logging for jaudiotagger
        Logger.getLogger("org.jaudiotagger").level = if(verbose) Level.CONFIG else Level.OFF

        // Write Metadata to file
        tracks.forEach { it.writeMetadata() }

        println("Finished populating metadata")

    }

}