import llm.gemini.Gemini

// for testing
suspend fun generateContent(): String {

    val apiKey = System.getenv("API_KEY")

    val gemini = Gemini(apiKey)

    gemini.addUserMessage("Explain how AI works")

    return gemini.sendMessage()

}

suspend fun main() {


    println(generateContent())


}
