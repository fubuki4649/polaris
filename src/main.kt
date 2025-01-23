import global.extractResource
import playlist.Playlist

// for testing
fun getPlaylist() {

    val playlist = Playlist("https://www.youtube.com/playlist?list=PLa51LDiG3SvwXqmlmniEhkeHMNDlRltrJ", "/home/kaneki/test")

    playlist.download()
    playlist.populateMetadata()

}

fun main() {

    val p = extractResource("/sync_ipod.py").absolutePath

    ProcessBuilder(("python3 $p").split(" "))
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor()

}
