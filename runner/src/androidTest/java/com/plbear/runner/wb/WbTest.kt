package com.plbear.runner.wb

import androidx.test.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.plbear.base.base.logcat
import org.junit.Before
import org.junit.Test

class WbTest {
    private lateinit var mDevice: UiDevice
    @Before
    fun init() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun test() {
        logcat("start test")
        val stateMachine = WbStateMachine(mDevice)
        stateMachine.start()
        stateMachine.sendMessage(stateMachine.obtainMessage(WbStateMachine.MSG_INIT))
        while (stateMachine.isStart) {
            runCatching {
                Thread.sleep((60 * 1000).toLong())
            }
        }
    }
}
