package com.plbear.base.base.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringDef
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


/**
 * 集中处理请求运行时权限
 *
 * @author Xu
 */
class RequestPermissionHub : Fragment(), EasyPermissions.PermissionCallbacks {

    private var requestPermission: MutableList<String> = mutableListOf<String>()

    private var onPermissionsGranted: OnPermissionsGrantResult? = null

    @StringDef(WRITE_EXTERNAL_STORAGE_PERMISSION, READ_EXTERNAL_STORAGE_PERMISSION, CAMERA_PERMISSION, READ_PHONE_STATE_PERMISSION, READ_CALENDAR, WRITE_CALENDAR, REQUEST_INSTALL_PACKAGES_PERMISSION, READ_CONTACTS, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, READ_PHONE_STATE, SEND_SMS, READ_SMS)
    annotation class PermissionName

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            requestPermission = savedInstanceState.getStringArray(STATE_PERMISSION_NAME)!!.toMutableList()
        }
        if (requestPermission == null || requestPermission!!.size == 0) {
            detach()
            return
        }
        // Ask for one permission
        val rationale: String
        //        switch (requestPermission) {
        //            case CAMERA_PERMISSION:
        //                rationale = getString(R.string.rationale_camera);
        //                break;
        //            case WRITE_EXTERNAL_STORAGE_PERMISSION:
        //                rationale = getString(R.string.rationale_read_write_external_storage);
        //                break;
        //            default:
        //                rationale = getString(R.string.rationale_ask_again);
        //                break;
        //        }
        rationale = "真的不需要权限吗?"
        EasyPermissions.requestPermissions(this, rationale,
                REQUEST_PERMISSION_CODE, *requestPermission.toTypedArray())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (onPermissionsGranted != null) {
            onPermissionsGranted!!.onPermissionsGrantResult(EasyPermissions.hasPermissions(activity!!, *requestPermission.toTypedArray()), *requestPermission.toTypedArray())
        }
        detach()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // 当用户勾选了 "NEVER ASK AGAIN." 框，弹出去App的设置页面
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                    .setRationale("真的不需要权限吗?")
                    .setTitle("权限提示")
                    .setPositiveButton("设置")
                    .setNegativeButton(getString(android.R.string.cancel))
                    .setRequestCode(REQUEST_SETTINGS_SCREEN_CODE)
                    .build()
                    .show()
        } else if (!EasyPermissions.hasPermissions(activity!!, *requestPermission.toTypedArray()) && onPermissionsGranted != null) {
            onPermissionsGranted!!.onPermissionsGrantResult(false, *requestPermission.toTypedArray())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (onPermissionsGranted != null) {
            onPermissionsGranted!!.onPermissionsGrantResult(EasyPermissions.hasPermissions(activity!!, *requestPermission.toTypedArray()),
                    *requestPermission.toTypedArray())
        }
        if (requestCode == REQUEST_SETTINGS_SCREEN_CODE) {
            detach()
        }
    }

    private fun detach() {
        fragmentManager!!.beginTransaction().remove(this).commitAllowingStateLoss()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray(STATE_PERMISSION_NAME, requestPermission.toTypedArray())
    }

    interface OnPermissionsGrantResult {
        /**
         * @param granted    是否授予成功
         * @param permission 要授予的权限名称
         */
        fun onPermissionsGrantResult(granted: Boolean, vararg permission: String)
    }

    companion object {
        private val STATE_PERMISSION_NAME = "STATE_PERMISSION_NAME"

        const val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        const val READ_PHONE_STATE_PERMISSION = Manifest.permission.READ_PHONE_STATE
        const val READ_CALENDAR = Manifest.permission.READ_CALENDAR
        const val WRITE_CALENDAR = Manifest.permission.WRITE_CALENDAR
        const val REQUEST_INSTALL_PACKAGES_PERMISSION = Manifest.permission.REQUEST_INSTALL_PACKAGES
        const val READ_CONTACTS = Manifest.permission.READ_CONTACTS
        const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE
        const val SEND_SMS = Manifest.permission.SEND_SMS
        const val READ_SMS = Manifest.permission.READ_SMS

        private const val REQUEST_PERMISSION_CODE = 123
        private const val REQUEST_SETTINGS_SCREEN_CODE = 125

        fun requestCameraPermission(context: Context,
                                    fm: FragmentManager,
                                    onPermissionsGranted: OnPermissionsGrantResult?): Boolean {
            return requestPermission(context, fm, onPermissionsGranted, CAMERA_PERMISSION)
        }

        fun requestNecessaryPermission(context: Context,
                                       fm: FragmentManager,
                                       onPermissionsGranted: OnPermissionsGrantResult?): Boolean {
            return requestPermission(context, fm, onPermissionsGranted)
        }

        fun requestPermission(context: Context,
                              fm: FragmentManager,
                              onPermissionsGranted: OnPermissionsGrantResult?,
                              @PermissionName vararg requestPermission: String
        ): Boolean {
            if (EasyPermissions.hasPermissions(context, *requestPermission)) {
                onPermissionsGranted?.onPermissionsGrantResult(true, *requestPermission)
                return true
            }
            val f = RequestPermissionHub()
            for (temp in requestPermission) {
                f.requestPermission.add(temp)
            }
            f.onPermissionsGranted = onPermissionsGranted
            fm.beginTransaction().add(f, RequestPermissionHub::class.java.simpleName).commit()
            return false
        }

        fun hasPermission(context: Context, @PermissionName vararg requestPermission: String): Boolean {
            return EasyPermissions.hasPermissions(context, *requestPermission)
        }
    }
}
