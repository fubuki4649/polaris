package playlist

import kotlinx.serialization.Serializable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.mp4.Mp4Tag
import java.io.File

class Track(val path: String) {

    var videoName: String
    var channelName: String
    var albumCoverPath: String? = null
    lateinit var metadata: Metadata

    init {
        val splitPath = path.split("<DELIMITER>")
        channelName = splitPath.first()
        videoName = splitPath.last()
    }

    @Serializable
    data class Metadata(
        val title: String = "",
        val artists: List<String> = emptyList(),
        val isCover: Boolean = false,
    )

    fun writeMetadata() {

        if(!File(path).exists()) println("file doesnt exist")
        val audioFile = AudioFileIO.read(File(path))
        val tag = audioFile.tagOrCreateDefault as Mp4Tag

        // Set appropriate metadata fields
        tag.setField(FieldKey.TITLE, metadata.title)
        when(metadata.artists.size) {
            0 -> {}
            1 -> tag.setField(FieldKey.ARTIST, metadata.artists[0])
            else -> tag.setField(FieldKey.ARTISTS, metadata.artists.joinToString("; "))
        }

        audioFile.commit()

    }

}