import llm.gemini.Gemini
import playlist.Playlist

// for testing
fun getPlaylist() {

    val playlist = Playlist("https://www.youtube.com/playlist?list=PLa51LDiG3SvwXqmlmniEhkeHMNDlRltrJ", "/home/kaneki/test")

    playlist.download()
    playlist.populateMetadata()

}

fun main() {


    getPlaylist()


}
