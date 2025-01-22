import global.extractResource
import kotlinx.io.files.FileNotFoundException
import playlist.Playlist

// for testing
fun getPlaylist() {

    val playlist = Playlist("https://www.youtube.com/playlist?list=PLa51LDiG3SvwXqmlmniEhkeHMNDlRltrJ", "/home/kaneki/test")

    playlist.download()
    playlist.populateMetadata()

}

fun main() {

    val p = extractResource("/test.py").absolutePath

    ProcessBuilder(("python3 $p").split(" "))
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor()

}
