package com.plbear.base.base.utils


import android.util.Log

import com.google.gson.FieldNamingStrategy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader

import java.lang.reflect.Field
import java.lang.reflect.Type
import java.util.Date

/**
 * @author yanyongjun 20190614
 */
class GsonManager private constructor(){
    var gson: Gson = GsonBuilder()
            .setFieldNamingStrategy {
                if (it.name == "_package") {
                    "package"
                } else {
                    it.name
                }
            }
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()

    fun gson(): Gson {
        return gson
    }

    fun toJson(src: Any?): String {
        if (src == null) {
            return "null"
        }
        return gson.toJson(src)
    }

    inline fun <reified T> fromJson(json: String): T? {
        return gson.fromJson(json, T::class.java)
    }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GsonManager()
        }
    }
}