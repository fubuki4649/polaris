package global

import llm.gemini.Gemini
import kotlin.reflect.KClass

enum class LanguageModel(
    val modelType: KClass<out llm.LanguageModel>,
    val apiKey: String,
    val apiLink: String,
    val sysPrompt: String,
    val userPromptPrefix: String,
) {
    GEMINI(Gemini::class, geminiApiKey, geminiApiLink, geminiSystemPrompt, geminiUserPromptPrefix),
}

// Indicated which LLM to use
var languageModel: LanguageModel = LanguageModel.GEMINI


// Constants for Gemini
val geminiApiKey = System.getenv("GEMINI_API_KEY") ?: ""
const val geminiModelId: String = "gemini-2.0-flash-exp"
const val geminiApiLink = "https://generativelanguage.googleapis.com/v1beta/models/$geminiModelId:generateContent"

const val geminiSystemPrompt =
    """
        Suppose you are an automatic music tagging system. You will be given the title of a music video, and you will try to
        find the title and author(s) of the song. If the song is by a band/group, list the name of the band in addition to the
        individual members of the group. Also tell me if this song is a cover or not.

        For Japanese characters in the names of the song/author(s), do not romanize.

        Reply in JSON format, with the following schema for each song.

        ```json
        {
            "${'$'}schema": "http://json-schema.org/draft-04/schema#",
            "type": "object",
            "properties": {
                "title": {
                    "type": "string"
                },
                "artists": {
                    "type": "array",
                    "items": {
                        "type": "string"
                    }
                },
                "isCover": {
                    "type": "boolean"
                }
            },
            "required": [
                "title",
                "artists",
                "isCover"
            ]
        }
        ```
    """

const val geminiUserPromptPrefix = "List of video titles, separated by newline:\n\n"