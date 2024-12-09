import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*


suspend fun generateContent(): String {

    val apiKey = "AIzaSyDyZPPZwHGSRs0N9v9uzloVcHgL5-RxPRU"

    val client = HttpClient()

    val response: HttpResponse = client.post("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent") {
        headers {
            append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
        parameter("key", apiKey)
        setBody("""
            {
              "contents": [
                {
                  "parts": [
                    {"text": "Explain how AI works"}
                  ]
                }
              ]
            }
        """.trimIndent())
    }

    val responseBody = response.bodyAsText()
    client.close() // Make sure to close the client after use
    return responseBody
}

suspend fun main() {


    println(generateContent())


}
