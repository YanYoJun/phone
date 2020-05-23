package com.plbear.runner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.plbear.base.base.utils.RequestPermissionHub
import com.plbear.base.base.utils.ToastManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
    }

    private fun requestPermission() {
        RequestPermissionHub.requestPermission(
            this,
            supportFragmentManager,
            object : RequestPermissionHub.OnPermissionsGrantResult {
                override fun onPermissionsGrantResult(granted: Boolean, vararg permission: String) {
                    if (!granted) {
                        ToastManager.showToast("申请权限不成功,请重新打开")
                    }
                    if (granted) {
                        ToastManager.showToast("您已经成功申请权限")
                    }
                }
            },
            RequestPermissionHub.WRITE_EXTERNAL_STORAGE_PERMISSION,
            RequestPermissionHub.READ_EXTERNAL_STORAGE_PERMISSION
        )
    }
}
