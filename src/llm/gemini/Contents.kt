package llm.gemini

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Part(
    @SerialName("text") val text: String
)

@Serializable
data class Content(
    @SerialName("role") val role: Role = Role.USER,
    @SerialName("parts") val parts: MutableList<Part> = ArrayList()
) : MutableList<Part> by parts {
    @Serializable
    enum class Role {
        @SerialName("model") MODEL,
        @SerialName("user") USER
    }
}

@Serializable
data class ContentsContainer(
    @SerialName("contents") val contents: MutableList<Content> = ArrayList()
) : MutableList<Content> by contents