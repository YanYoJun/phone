package com.plbear.base.base.utils

import com.plbear.base.base.Shell
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * created by yanyongjun on 2020/5/23
 */
object CmdUtils {
    private var sRunningTest = false
    fun startProtectTest(command: String) {
        sRunningTest = true
        GlobalScope.launch {
            while (sRunningTest) {
                kotlin.runCatching {
                    if (!isProcessRunning("com.plbear.runner")) {
                        Shell.execCommand(command, true)
                    }
                    Thread.sleep(7000)
                }
                ToastManager.showToast("潜行运行中")
            }
        }
    }

    fun stop() {
        sRunningTest = false
        killProcess("com.plbear.runner")
    }

    private fun isProcessRunning(packageName: String): Boolean {
        val commandResult = Shell.execCommand("ps -ef |grep $packageName", true, true)
        var successMsg = commandResult.successMsg
        successMsg = successMsg.replace("grep $packageName", "")
        return successMsg.contains(packageName)
    }

    fun killProcess(packageName: String) {
        val msg = Shell.execCommand("ps|grep $packageName", true, true).successMsg
        var PID = ""
        if (msg == null) {
            return
        }
        for (i in 0 until msg.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray().size) {
            if (i > 0 && msg.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[i].length > 1) {
                PID = msg.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[i]
                break
            }
        }
        if (PID.length > 0) {
            Shell.execCommand("kill $PID", true)
        }
    }
}