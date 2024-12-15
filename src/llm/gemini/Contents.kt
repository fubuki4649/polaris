package llm.gemini

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import llm.LanguageModel.Role


@Serializable
data class Part(
    @SerialName("text") val text: String
)

@Serializable
data class Content(
    @Transient val role: Role,
    @SerialName("parts") val parts: MutableList<Part> = ArrayList()
) : MutableList<Part> by parts

@Serializable
data class ContentsContainer(
    @SerialName("contents") val contents: MutableList<Content> = ArrayList()
) : MutableList<Content> by contents