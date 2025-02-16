import metadata.iTunesMetadataGetter
import metadata.iTunesMetadataGetter.Companion.lookupId
import metadata.iTunesMetadataGetter.Companion.searchSong
import picocli.CommandLine
import picocli.CommandLine.*
import playlist.Playlist
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(
    name = "polaris",
    mixinStandardHelpOptions = true,
    description = ["A CLI tool (for now) for automatic downloading and tagging YouTube music playlists"],
    version = ["0.1.0-dev"],
)
class Polaris: Callable<Int> {

    @Option(names = ["-o", "--overwrite"], description = ["Overwrite existing files"])
    var overwrite: Boolean = false

    @Option(names = ["-v", "--verbose"], description = ["Enable verbose output"])
    var verbose: Boolean = false

    @Parameters(index = "0", description = ["YouTube link for the playlist"], arity = "1")
    lateinit var youtubeLink: String

    @Parameters(index = "1", description = ["Output path to place the music"], arity = "1")
    lateinit var outputPath: String

    override fun call(): Int {

        // Set logging mode for SLF4J (used by ktor)
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", if(verbose) "DEBUG" else "WARN")

        // Get playlist and metadata
        val playlist = Playlist(youtubeLink, outputPath, overwrite)

        playlist.download(verbose)
        playlist.populateMetadata(verbose)

        return 0

    }

}

fun main(args: Array<String>) {

    // exitProcess(CommandLine(Polaris()).execute(*args))

    searchSong("少女レイ")
    lookupId(searchSong("少女レイ")[1], iTunesMetadataGetter.iTunesObjectType.SONG)

}
