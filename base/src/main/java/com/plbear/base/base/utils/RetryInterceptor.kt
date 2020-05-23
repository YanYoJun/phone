package com.plbear.base.base.utils

import android.util.Log

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * @author：wangzhicheng
 * @date: 2019/1/22 18:07
 * 失败则重试三次
 * @email： wangzhicheng@lukou.com
 */
class RetryInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // try the request
        var response = chain.proceed(request)

        var tryCount = 0
        while (!response.isSuccessful && tryCount < 3) {

            Log.d("intercept", "Request is not successful - $tryCount")

            tryCount++

            // retry the request
            response = chain.proceed(request)
        }

        // otherwise just pass the original response on
        return response
    }
}
