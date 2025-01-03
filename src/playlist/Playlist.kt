package playlist

import kotlin.io.path.*

class Playlist(val playlistLink: String, val workPath: String = "~/.cache/polaris", val overwrite: Boolean = false) {

    private val ytDlpCommand = "yt-dlp -f bestaudio/best --extract-audio --audio-format aac --audio-quality 0 -o \"%(uploader)s <DELIMITER/> %(title)s.%(ext)s\" --no-playlist --paths $workPath/audio '$playlistLink'"

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

        ProcessBuilder(ytDlpCommand.split(" "))
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()

        val audioPath = Path("$workPath/audio")
        audioPath.walk().forEach {
            tracks.add(Track(it.fileName.toString()))
        }

    }

}