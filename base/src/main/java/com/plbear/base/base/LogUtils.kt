package com.plbear.base.base

import android.util.Log
import com.plbear.base.base.utils.Utils
import java.io.File

/**
 * created by yanyongjun on 2020-04-15
 */
fun logcat(msg: String?) {
    if (msg == null) {
        Log.e("myphone", "null")
        return
    }
    Log.e("myphone", msg)
    val file = File("/sdcard/mahuateng/logs")
    if (!file.exists()) file.mkdir()
    val logFile = File(file, Utils.today())
    logFile.appendText(msg + "\n")
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