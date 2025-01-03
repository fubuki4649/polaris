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

    inner class Metadata(
        var title: String = ""
    ) {

        fun populate() {

            

        }

    }

}