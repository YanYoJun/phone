package com.plbear.base.base

import android.util.Log

/**
 * created by yanyongjun on 2020-04-15
 */
fun logcat(msg: String?) {
    if (msg == null) {
        Log.e("myphone", "null")
        return
    }
    Log.e("myphone", msg)
}

fun logcat(e: Throwable?) {
    if (e == null) {
        logcat("throwable == null")
        return
    }
    val builder = StringBuilder()
    builder.append(e.message + "\n")
    for (ele in e.stackTrace) {
        builder.append(ele.toString() + "\n")
    }
    logcat(builder.toString())
}