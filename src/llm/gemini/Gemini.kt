package llm.gemini

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.headers
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import llm.LanguageModel
import llm.LanguageModel.Role

class Gemini(override val apiKey: String, model: String = "gemini-2.0-flash-exp") : LanguageModel {

    val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent"

    private val contentsContainer = ContentsContainer()

    // Private methods

    private fun addPart(part: Part, role: Role) {

        if(contentsContainer.lastOrNull()?.role != role) contentsContainer.add(Content(role))
        contentsContainer.last().parts.add(part)

    }


    // Public methods

    override suspend fun addSystemMessage(msg: String) {
        addPart(Part(msg), Role.SYSTEM)
    }

    override suspend fun addUserMessage(msg: String) {
        addPart(Part(msg), Role.USER)
    }

    override suspend fun sendMessage(): String {

        val client = HttpClient()

        val response: HttpResponse = client.post(url) {
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            parameter("key", apiKey)
            setBody(Json.encodeToString(contentsContainer))
        }

        val responseBody = response.bodyAsText()
        client.close()
        return responseBody

    }

}