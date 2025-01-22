package global

import kotlinx.io.files.FileNotFoundException
import java.io.File
import java.nio.file.Files

fun extractResource(resourcePath: String): File {
    val inputStream = object {}.javaClass.getResourceAsStream(resourcePath)
        ?: throw FileNotFoundException("Resource Not Found. Corrupt JAR?")

    // Create a temp file with the same extension
    val tempFile = Files.createTempFile("script", ".py").toFile()

    // Copy InputStream to the temp file
    inputStream.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    tempFile.deleteOnExit() // Ensure the temp file gets deleted on exit
    return tempFile
}