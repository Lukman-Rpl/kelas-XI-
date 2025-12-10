package com.example.aplikasimonitoringkelas

import com.google.gson.*
import java.lang.reflect.Type

class SafeListDeserializer<T>(
    private val elementType: Class<T>
) : JsonDeserializer<List<T>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<T> {
        if (json == null || json.isJsonNull) return emptyList()

        return if (json.isJsonArray) {
            json.asJsonArray.map { context!!.deserialize(it, elementType) }
        } else if (json.isJsonObject) {
            emptyList()
        } else {
            emptyList()
        }
    }
}
