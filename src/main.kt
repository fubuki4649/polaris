import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters
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

        val playlist = Playlist(youtubeLink, outputPath, overwrite, verbose)

        playlist.download()
        playlist.populateMetadata()

        return 0

    }

}

fun main(args: Array<String>) {

    exitProcess(CommandLine(Polaris()).execute(*args))

}
