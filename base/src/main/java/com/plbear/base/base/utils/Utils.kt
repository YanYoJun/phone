package com.plbear.base.base.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import com.plbear.base.base.logcat
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * created by yanyongjun on 2020/5/23
 */
object Utils {
    private val format = SimpleDateFormat("yyyy_MM_dd", Locale.CHINA)
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(50, TimeUnit.SECONDS)
        .writeTimeout(50, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .addInterceptor(RetryInterceptor())
        .build()

    fun today(): String {
        return format.format(Date())
    }

    fun downloadImage(context: Context, url: String, path: String, imgFileName: String): Boolean {
        logcat("下载图片:$url")
        logcat("$path$imgFileName")
        val request = Request.Builder().get()
            .url(url)
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            val bytes = response.body()!!.bytes() as ByteArray
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            val filePic = File("$path$imgFileName")
            if (filePic.exists()) {
                filePic.delete()
            }
            logcat("make dirs")
            File(path).mkdirs()
            filePic.createNewFile()
            val out = FileOutputStream(filePic)
            if (imgFileName.endsWith("png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            out.flush()
            out.close()

            MediaScannerConnection.scanFile(context, arrayOf("$path$imgFileName"), null, null)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            logcat(e)
            return false
        }

    }
}