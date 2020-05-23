package com.plbear.base.base.network

import com.plbear.base.base.bean.AndroidMsg
import com.plbear.base.base.bean.DataWrapper
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * created by yanyongjun on 2020/5/23
 */
interface ApiService {
//    @GET("/service/alipay/code/create")
//    fun getAlipayRedPackageTask(
//        @Query("timestamp") timestamp: String,
//        @Query("sign") sign: String
//    ):
    @GET("/api/quan/next")
    fun getNextCommodity():Call<DataWrapper<AndroidMsg>>
}