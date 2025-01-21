package playlist

import global.SongMetadataGetter
import kotlinx.serialization.json.Json
import kotlin.io.path.*
import java.util.regex.Pattern

class Playlist(playlistLink: String, private val workPath: String = "${System.getProperty("user.home")}/.cache/polaris", overwrite: Boolean = false) {

    private val ytDlpCommand = "yt-dlp -f bestaudio/best --extract-audio --audio-format aac --audio-quality 0 -o %(uploader)s<DELIMITER>%(title)s --no-playlist --paths $workPath/audio $playlistLink"

    val tracks: MutableList<Track> = mutableListOf()

    init {

        // Handle non-empty work path
        if(Path(workPath).exists()) {

            if(!overwrite) {
                println("$workPath already exists. Overwrite? (Y/n)")
                if((readlnOrNull()?.getOrNull(0)?.lowercaseChar() ?: 'y') == 'n') throw Exception("Directory already exists.")
            }

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
    fun download() {

        // Run yt-dlp
        ProcessBuilder(ytDlpCommand.split(" "))
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()

        // Populate `tracks: MutableList<Track>`
        val audioPath = Path("$workPath/audio")
        audioPath.walk().forEach {
            tracks.add(Track(it.fileName.toString()))
        }

    }

    fun populateMetadata() {

        // Send an LLM query and get the response as json
        val jsonRawResponse = SongMetadataGetter.getMetadata(tracks.map {
            it.videoName
        })

        val jsonData = extractJsonContent(jsonRawResponse).replace("\\n", "\n").replace("\\\"", "\"")

        // Deserialize json and set metadata for each track
        Json.decodeFromString<List<Track.Metadata>>(jsonData).mapIndexed { index, metadata ->
            tracks[index].metadata = metadata
        }

        // Write Metadata to file
        tracks.forEach { it.writeMetadata() }

    }

}