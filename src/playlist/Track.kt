package playlist

class Track(val path: String) {

    var videoName: String
    var channelName: String
    var thumbnailPath: String? = null
    val metadata: Metadata = Metadata()

    init {
        val splitPath = path.split("<DELIMITER/>")
        channelName = splitPath.first()
        videoName = splitPath.last()
    }

    data class Metadata(
        var title: String = "",
        var artists: List<String> = emptyList(),
        var isCover: Boolean = false,
    )

}