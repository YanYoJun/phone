package com.plbear.runner.wb

import android.os.Message
import androidx.test.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.plbear.base.base.Shell
import com.plbear.base.base.logcat
import com.plbear.runner.base.StateMachine
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsNull

/**
 * created by yanyongjun on 2020-04-15
 */
class WbStateMachine(device: UiDevice) : StateMachine(device) {
    companion object {
        const val PACKAGE_NAME = "com.sina.weibo"

        const val MSG_INIT = 0
    }

    private val mHomePage: PageState2 =
        object : PageState2("首页", By.res("com.sina.weibo:id/titleSave")) {
            override fun dealMessage(msg: Message) {
                logcat("到首页了")
            }
        }

    init {
        logcat("init2")
        addState(mHomePage)
        setInitState(mHomePage)
    }

    override fun launchInitState() {
        logcat("launche init start")
        Shell.execCommand(String.format("am force-stop %s", PACKAGE_NAME), true)
        logcat("launche init start 3")

        val launcherPackage = mDevice.launcherPackageName
        MatcherAssert.assertThat(launcherPackage, IsNull.notNullValue())
        val context = InstrumentationRegistry.getContext()
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)
        context.startActivity(intent)
        transToState(mInitState)
        mDevice.wait(Until.hasObject(mInitState.flagSelector), 3000)
        logcat("launche over")
    }

    override fun getMsgDes(msg: Int): String {
        return when (msg) {
            MSG_INIT -> "MSG_INIT"
            else -> ""
        }
    }
    //adb shell am instrument -w -r   -e debug false -e class 'com.plbear.runner.wb.WbTest#test' com.plbear.runner.test/androidx.test.runner.AndroidJUnitRunner
}