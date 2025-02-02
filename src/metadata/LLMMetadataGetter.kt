package metadata

import global.languageModel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import playlist.Track
import java.util.regex.Pattern
import kotlin.reflect.full.createInstance

class LLMMetadataGetter {

    companion object {

        private val llm = languageModel.modelType.createInstance()
        private val sysPrompt = languageModel.sysPrompt
        private val userPromptPrefix = languageModel.userPromptPrefix

        init {

            runBlocking {
                llm.addSystemMessage(sysPrompt)
            }

        }

        /// Extracts JSON data from LLM response
        private fun extractJsonContent(text: String): String {
            val pattern = Pattern.compile("```json(.*?)```", Pattern.DOTALL)
            val matcher = pattern.matcher(text)
            return if (matcher.find())  matcher.group(1) else ""
        }

        /// Retrieve title and artist using YT video titles from an LLM for a list of tracks
        fun getMetadataFromLLM(tracks: List<String>): List<Track.Metadata> {

            val userPrompt: StringBuilder = StringBuilder(userPromptPrefix)

            // Add each video title to user prompt
            tracks.forEach {
                userPrompt.append(it + "\n")
            }

            // Set user prompt and send query
            var rawResponse: String
            runBlocking {
                llm.addUserMessage(userPrompt.toString())
                rawResponse = llm.sendMessage()
            }

            // Isolate only the json content from the LLM response
            val jsonData = extractJsonContent(rawResponse).replace("\\n", "\n").replace("\\\"", "\"")

            // Deserialize json and return result
            return Json.decodeFromString<List<Track.Metadata>>(jsonData)

        }

    }

}