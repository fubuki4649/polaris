package llm

interface LanguageModel {

    val apiKey: String
    val apiLink: String

    suspend fun addSystemMessage(msg: String)

    suspend fun addUserMessage(msg: String)

    suspend fun sendMessage(): String

    override fun toString(): String

}