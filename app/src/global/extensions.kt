package global

import kotlinx.serialization.json.*


inline fun <reified T> Map<String, JsonElement>.getValue(key: String): T? {
    return when (T::class) {
        Boolean::class -> this[key]?.jsonPrimitive?.boolean
        String::class -> this[key]?.jsonPrimitive?.content
        Double::class -> this[key]?.jsonPrimitive?.double
        Float::class -> this[key]?.jsonPrimitive?.float
        Int::class -> this[key]?.jsonPrimitive?.int
        Long::class -> this[key]?.jsonPrimitive?.long
        else -> null
    } as T?
}