package llm

class Gemini(override val apiKey: String) : LanguageModel {

    val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    override suspend fun addSystemMessage(msg: String) {
        TODO("Not yet implemented")
    }

    override suspend fun addUserMessage(msg: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun sendMessage(reply: String): String {
        TODO("Not yet implemented")
    }

}