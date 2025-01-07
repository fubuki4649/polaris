package llm.gemini

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import llm.LanguageModel.Role


@Serializable
data class Part(
    @SerialName("text") val text: String
)

@Serializable
data class Content(
    @Transient val role: Role = Role.ASSISTANT,
    @SerialName("parts") val parts: MutableList<Part> = ArrayList()
) : MutableList<Part> by parts

@Serializable
data class ContentsContainer(
    @SerialName("contents") val contents: MutableList<Content> = ArrayList()
) : MutableList<Content> by contents