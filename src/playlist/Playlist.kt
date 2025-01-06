package playlist

import global.SongMetadataGetter
import kotlinx.serialization.json.Json
import kotlin.io.path.*

class Playlist(playlistLink: String, private val workPath: String = "~/.cache/polaris", overwrite: Boolean = false) {

    private val ytDlpCommand = "yt-dlp -f bestaudio/best --extract-audio --audio-format aac --audio-quality 0 -o \"%(uploader)s <DELIMITER> %(title)s\" --no-playlist --paths $workPath/audio '$playlistLink'"

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

    /// Download playlist songs from YouTube
    @OptIn(ExperimentalPathApi::class)
    fun download() {

        // Run yt-dlp
        ProcessBuilder(ytDlpCommand.split(" "))
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()

        // populate `tracks: MutableList<Track>`
        val audioPath = Path("$workPath/audio")
        audioPath.walk().forEach {
            tracks.add(Track(it.fileName.toString()))
        }

    }

    fun populateMetadata() {

        // Get the LLM response as json
        val json = SongMetadataGetter.getMetadata(tracks.map {
            it.videoName
        })

        // Deserialize json and set metadata for each track
        Json.decodeFromString<List<Track.Metadata>>(json).mapIndexed { index, metadata ->
            tracks[index].metadata = metadata
        }

    }

}