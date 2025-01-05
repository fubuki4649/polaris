package playlist

import kotlinx.serialization.Serializable

class Track(val path: String) {

    var videoName: String
    var channelName: String
    var thumbnailPath: String? = null
    lateinit var metadata: Metadata

    init {
        val splitPath = path.split("<DELIMITER/>")
        channelName = splitPath.first()
        videoName = splitPath.last()
    }

    @Serializable
    data class Metadata(
        val title: String = "",
        val artists: List<String> = emptyList(),
        val isCover: Boolean = false,
    )

}