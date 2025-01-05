package llm

interface LanguageModel {

    enum class Role {
        SYSTEM, USER, ASSISTANT
    }

    val apiKey: String
    val apiLink: String

    suspend fun addSystemMessage(msg: String)

    suspend fun addUserMessage(msg: String)

    suspend fun sendMessage(): String

}