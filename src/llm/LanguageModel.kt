package llm

interface LanguageModel {

    val apiKey: String

    suspend fun addSystemMessage(msg: String)

    suspend fun addUserMessage(msg: String): String

    suspend fun sendMessage(reply: String): String

}