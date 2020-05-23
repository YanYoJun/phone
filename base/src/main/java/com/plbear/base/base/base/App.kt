package com.plbear.base.base.base

import android.app.Application

/**
 * created by yanyongjun on 2020/5/23
 */
class App : Application() {
    companion object {
        private var application: Application? = null
        fun instance(): Application = application!!
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }
}