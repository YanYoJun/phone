package com.plbear.base.base.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * created by yanyongjun on 2020/5/23
 */
class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .method(original.method(), original.body())
        try {
            builder.header("x-device", "android")
                .addHeader("x-platform", "app")
//                    .addHeader("token", LibApplication.instance().accountService().token())
//                    .addHeader("x-app-version", Environment.versionCodeStr())
//                    .addHeader("x-source", Environment.source())
//                    .addHeader("x-os-version", Environment.getOsReleaseVer())
//                    .addHeader("x-bundle-id", Environment.getAppId())
//                    .addHeader("x-model", Environment.getOsModel())
//                    .addHeader("x-user-group", String.valueOf(LibApplication.instance().configService().userGroup().type()))
//                    .addHeader("x-taobao-installed", AliTradeInstance.getInstance().tbAppExistStatus)
//                .addHeader("x-device-id", BaseApplication.instance().device?.deviceId ?: "")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return chain.proceed(builder.build())
    }
}