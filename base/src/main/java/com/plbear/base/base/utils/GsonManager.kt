package com.plbear.base.base.utils

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.lang.reflect.Type

/**
 * @author Xu
 */
class GsonManager {
    val gson = GsonBuilder().setFieldNamingStrategy {
        if (it.name == "_package") {
            "packages"
        } else {
            it.name
        }
    }.create()

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GsonManager()
        }
    }

    inline fun <reified T> fromJson(json: String?): T? {
        return GsonManager.instance.gson.fromJson(json, T::class.java)
    }

    fun <T> fromJson(json: String, classOfT: Class<T>): T? {
        return gson.fromJson(json, classOfT)
    }

    fun <T> fromJson(json: String, type: Type): T? {
        return gson.fromJson(json, type)
    }

    fun toJsonObject(json: String): JsonObject {
        return gson.fromJson(json, JsonObject::class.java)
    }

    fun toJson(src: Any?): String {
        return gson.toJson(src)
    }
}