package com.plbear.base.base.utils

import android.widget.Toast
import com.plbear.base.base.base.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * created by yanyongjun on 2020/5/23
 */
object ToastManager {
    fun showToast(msg: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(App.instance(), msg, Toast.LENGTH_SHORT).show()
        }
    }
}