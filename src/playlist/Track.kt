package playlist

import kotlinx.serialization.Serializable
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.images.ArtworkFactory
import org.jaudiotagger.tag.mp4.Mp4Tag
import org.slf4j.LoggerFactory
import java.io.File

class Track(val path: String) {

    var videoName: String
    var channelName: String
    var albumArtPath: String = ""
    lateinit var metadata: Metadata

    private val logger = LoggerFactory.getLogger(Track::class.java)

    init {
        val splitPath = path.split("<DELIMITER>")
        channelName = splitPath.first()
        videoName = splitPath.last()
    }

    @Serializable
    data class Metadata(
        val title: String = "",
        val artists: List<String> = emptyList(),
        val album: String = "",
        val trackNumber: Int = 1,
        val trackTotal: Int = 1,
        val albumArt: String = "",
        val isCover: Boolean = false,
    )

    fun writeMetadata() {

        logger.info("Writing metadata to $path")

        if(!File(path).exists()) {
            logger.error("File does not exist: $path")
            return
        }

        val audioFile = AudioFileIO.read(File(path))
        val tag = audioFile.tagOrCreateDefault as Mp4Tag

        // Set appropriate metadata text fields
        tag.setField(FieldKey.TITLE, metadata.title)
        tag.setField(FieldKey.ALBUM, metadata.album)
        tag.setField(FieldKey.TRACK, metadata.trackNumber.toString())
        tag.setField(FieldKey.TRACK_TOTAL, metadata.trackTotal.toString())
        when(metadata.artists.size) {
            0 -> {}
            1 -> tag.setField(FieldKey.ARTIST, metadata.artists[0])
            else -> tag.setField(FieldKey.ARTISTS, metadata.artists.joinToString("; "))
        }

        // Write album artwork
        val artwork = File(albumArtPath)
        if(artwork.exists()) {
            tag.deleteArtworkField()
            tag.setField(ArtworkFactory.createArtworkFromFile(artwork))
        }

        audioFile.commit()

        logger.info("Successfully written metadata for $path")

    }

}