package com.plbear.base.base.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * created by yanyongjun on 2020/5/23
 */
object ApiFactory {
    private var service: ApiService

    init {
        val okHttpHttp = OkHttpClient().newBuilder().addInterceptor(HeaderInterceptor()).build()
        val baseUrl = "http://47.105.199.21:8888"
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpHttp).build()
        service = retrofit.create(ApiService::class.java)
    }

    fun apiService() = service
}