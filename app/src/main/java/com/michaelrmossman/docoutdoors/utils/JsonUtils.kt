package com.michaelrmossman.docoutdoors.utils

import com.google.gson.JsonArray
import com.google.gson.JsonObject

object JsonUtils {

    fun parseJsonArrayAsObjects(jsonArray: JsonArray): String {
        val sb = StringBuilder()

        for (i in 0 until jsonArray.size()) {
            val region = jsonArray.get(i).asJsonObject
            sb.append(region.get("id").asString)
            if (i < jsonArray.size().minus(1)) {
                sb.append(ITEM_SEPARATOR)
            }
        }

        return sb.toString()
    }

    fun parseJsonArrayAsStrings(jsonArray: JsonArray): String {
        val sb = StringBuilder()

        for (i in 0 until jsonArray.size()) {
            jsonArray.get(i).asString?.let { region ->
                sb.append(region.replaceApos())
                if (i < jsonArray.size().minus(1)) {
                    sb.append(ITEM_SEPARATOR)
                }
            }
        }

        return sb.toString()
    }
}