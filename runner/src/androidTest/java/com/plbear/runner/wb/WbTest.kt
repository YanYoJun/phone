package com.plbear.runner.wb

import android.widget.TextView
import androidx.test.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import com.plbear.base.base.logcat
import org.junit.Before
import org.junit.Test
import java.io.File

class WbTest {
    private lateinit var mDevice: UiDevice
    @Before
    fun init() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun send() {
        logcat("start send")
        val stateMachine = WbStateMachine(mDevice)
        stateMachine.start()
        stateMachine.sendMessage(stateMachine.obtainMessage(WbStateMachine.MSG_INIT))
        while (stateMachine.isStart) {
            runCatching {
                Thread.sleep((60 * 1000).toLong())
            }
        }
    }

    @Test
    fun test() {
//        logcat("start test")
//        mDevice.click(1000,1000)
//        File("/sdcard/mahuateng/1.txt").createNewFile()
        val add = mDevice.findObject(
            By.res("com.sina.weibo:id/titleSave"))
        add.click()

    }
}
