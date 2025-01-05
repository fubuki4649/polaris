package global

import kotlinx.coroutines.runBlocking
import kotlin.reflect.full.createInstance

class SongMetadataGetter {

    companion object {

        private val llm = languageModel.modelType.createInstance()
        private val sysPrompt = languageModel.sysPrompt
        private val userPromptPrefix = languageModel.userPromptPrefix

        init {

            runBlocking {
                llm.addSystemMessage(sysPrompt)
            }

        }

        fun getMetadata(tracks: List<String>): String {

            val userPrompt: StringBuilder = StringBuilder(userPromptPrefix)

            // Add each video title to user prompt
            tracks.forEach {
                userPrompt.append(it + "\n")
            }

            // Set user prompt and send query
            var retVal: String
            runBlocking {
                llm.addUserMessage(userPrompt.toString())
                retVal = llm.sendMessage()
            }

            // Return result
            return retVal

        }

    }

}