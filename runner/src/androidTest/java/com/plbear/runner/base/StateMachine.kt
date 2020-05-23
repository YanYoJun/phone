package com.plbear.runner.base

/**
 * created by yanyongjun on 2020-04-15
 */
import android.os.*
import androidx.test.uiautomator.*
import com.plbear.base.base.logcat
import java.util.*

/**
 * 全面启动UiObject2
 */
abstract class StateMachine(var mDevice: UiDevice) :
    HandlerThread("StateMachineThread", Process.myTid()) {
    private lateinit var mHandler: Handler
    private lateinit var mCurrentState: PageState2
    protected lateinit var mInitState: PageState2
    private val mAllState = ArrayList<PageState2>()
    var isStart = false

    fun addState(vararg states: PageState2) {
        mAllState.addAll(states)
    }


    fun removeState(state: PageState2) {
        if (mAllState.contains(state) || isStart) {
            mAllState.remove(state)
        }
    }

    fun setInitState(state: PageState2) {
        if (mAllState.contains(state) && !isStart) {
            mCurrentState = state
            mInitState = state
        }
    }

    fun sendMessage(msg: Message) {
        logcat("send message:$isStart")
        if (!isStart) {
            return
        }
        logcat("send Message")
        mHandler.sendMessage(msg)
    }

    fun sendMessage(what: Int) {
        if (!isStart) {
            return
        }
        mHandler.sendMessage(obtainMessage(what))
    }

    fun sendMessageDelay(msg: Message, delay: Int) {
        if (!isStart) {
            return
        }
        mHandler.sendMessageDelayed(msg, delay.toLong())
    }


    fun transToState(state: PageState2): Boolean {
        if (!isStart) {
            return false
        }
        if (!mAllState.contains(state) || mCurrentState === state) {
            return false
        }
        mCurrentState.exitState()
        mCurrentState = state
        mCurrentState.enterState()
        return true
    }

    fun transToState(state: PageState2, msg: Message, selector: BySelector) {
        val object2 = mDevice.findObject(selector)
        if (object2 != null) {
            object2.click()
        }
        mDevice.wait(Until.hasObject(state.flagSelector), 10 * 1000)
        transToStateWithMsg(state, msg)
        object2.recycle()
    }

    fun transToStateWithMsg(state: PageState2, msg: Message): Boolean {
        if (!transToState(state)) {
            return false
        }
        deferMessage(msg)
        return true
    }

    fun transToStateWithMsg(state: PageState2, msg: Int): Boolean {
        if (!transToState(state)) {
            return false
        }
        deferMessage(obtainMessage(msg))
        return true
    }


    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mHandler = object : Handler(Looper.myLooper()) {
            override fun handleMessage(msg: Message) {
                if (!isStart) {
                    return
                }
                try {
                    logcat("handle messsage")
                    mCurrentState.handleMessage(msg)
                } catch (e: Exception) {
                    onMachineCrashed(e)
                }

            }
        }
    }

    override fun start() {
        logcat("start")
        super.start()
        isStart = true
        launchInitState()
        try {
            Thread.sleep(6000)
        } catch (e: Exception) {
            logcat(e)
        }

    }

    fun myStop() {
        isStart = false
        System.exit(10)
    }

    fun destory() {
        stop()
        mAllState.clear()
    }

    fun recyle(object2: UiObject2?) {
        try {
            object2?.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun obtainMessage(what: Int): Message {
        return mHandler.obtainMessage(what)
    }

    fun obtainMessage(msg: Message): Message {
        return mHandler.obtainMessage(msg.what, msg.arg1, msg.arg2, msg.obj)
    }

    fun obtainMessage(what: Int, msg: Message): Message {
        return mHandler.obtainMessage(what, msg.arg1, msg.arg2, msg.obj)
    }

    fun obtainMessage(what: Int, obj: Any): Message {
        return mHandler.obtainMessage(what, obj)
    }

    fun obtainMessage(what: Int, arg1: Int, obj: Any): Message {
        return mHandler.obtainMessage(what, arg1, 0, obj)
    }

    fun obtainMessage(what: Int, arg1: Int, arg2: Int, obj: Any): Message {
        return mHandler.obtainMessage(what, arg1, arg2, obj)
    }

    fun deferMessage(msg: Message) {
        sendMessage(obtainMessage(msg))
    }

    abstract fun launchInitState()

    abstract fun getMsgDes(msg: Int): String

    @Deprecated("")
    fun click(selector: BySelector, tryTime: Int, vararg targetPage: PageState2): Boolean {
        if (tryTime <= 0) {
            return false
        }
        try {
            val temp = mDevice.findObject(selector)
            temp.click(10)
            temp.recycle()
        } catch (e: StaleObjectException) {
            logcat(e.message)
            e.printStackTrace()
        } catch (e: NullPointerException) {
            logcat(e.message)
            e.printStackTrace()
        } catch (e: Exception) {
            logcat(e.message)
        }

        sleep(200)
        for (state in targetPage) {
            if (state.isPageInFont) {
                return true
            }
        }
        logcat("点击没有找到，等待1s")
        sleep(1000)
        return click(selector, tryTime - 1, *targetPage)
    }

    @Deprecated("")
    fun clickOnce(object2: UiObject2?, sleepTime: Int, vararg targetPage: PageState2): Boolean {
        if (object2 != null) {
            try {
                //click(object);
                object2.click(10)
            } catch (e: StaleObjectException) {
                logcat(e.message)
                e.printStackTrace()
            } catch (e: NullPointerException) {
                logcat(e.message)
                e.printStackTrace()
            } catch (e: Exception) {
                logcat(e.message)
            }
        }

        sleep(200)
        for (i in 0 until sleepTime) {
            for (state in targetPage) {
                if (state.isPageInFont) {
                    return true
                }
            }
            sleep(1000)
        }
        logcat("点击没有找到")
        return false
    }

    @Deprecated("")
    fun clickOnce(selector: BySelector, sleepTime: Int, vararg targetPage: PageState2): Boolean {
        try {
            //click(object);
            mDevice.findObject(selector).click(10)
        } catch (e: StaleObjectException) {
            logcat(e.message!!)
            e.printStackTrace()
        } catch (e: NullPointerException) {
            logcat(e.message!!)
            e.printStackTrace()
        } catch (e: Exception) {
            logcat(e.message!!)
        }

        sleep(200)
        for (i in 0 until sleepTime) {
            for (state in targetPage) {
                if (state.isPageInFont) {
                    return true
                }
            }
            sleep(500)
        }
        logcat("点击没有找到")
        return false
    }

    @Deprecated("")
    fun click(`object`: BySelector, tryTime: Int, vararg selectors: BySelector): Boolean {
        if (tryTime <= 0) {
            return false
        }
        try {
            val temp = mDevice.findObject(`object`)
            temp.click(10)
            temp.recycle()
        } catch (e: StaleObjectException) {
            logcat(e.message)
            e.printStackTrace()
        } catch (e: Exception) {
            logcat(e.message)
        }

        sleep(200)
        for (temp in selectors) {
            if (mDevice.hasObject(temp)) {
                return true
            }
        }
        sleep(1000)
        return click(`object`, tryTime - 1, *selectors)
    }

    @Deprecated("")
    fun clickOnce(`object`: UiObject2?, sleepTime: Int, vararg bySelectors: BySelector): Boolean {
        if (sleepTime <= 0) {
            return false
        }
        try {
            `object`!!.click(10)
        } catch (e: StaleObjectException) {
            logcat(e.message!!)
            e.printStackTrace()
        } catch (e: Exception) {
            logcat(e.message!!)
        }

        sleep(200)
        for (i in 0 until sleepTime) {
            for (temp in bySelectors) {
                if (mDevice.hasObject(temp)) {
                    return true
                }
            }
            sleep(500)
        }
        return false
    }

    fun backUntilPage(maxTimes: Int, vararg pages: PageState2): Boolean {
        var num = 0
        while (num < maxTimes) {
            for (page in pages) {
                if (page.isPageInFont) {
                    return true
                }
            }
            mDevice.pressBack()
            sleep(200)
            num++
        }
        return false
    }

    fun sleep(millis: Int) {
        try {
            Thread.sleep(millis.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Deprecated("")
    fun hasObject(selector: BySelector): Boolean {
        mDevice.wait(Until.hasObject(selector), 5 * 1000)
        return mDevice.hasObject(selector)
    }

    fun wait(page: PageState2) {
        wait(3, page)
    }

    fun wait(selector: BySelector) {
        wait(3, selector)
    }

    fun wait(second: Int, vararg selector: BySelector) {
        sleep(200)
        for (temp in selector) {
            if (mDevice.hasObject(temp)) {
                return
            }
        }
        if (second <= 0) {
            return
        }
        sleep(800)
        wait(second - 1, *selector)
    }

    fun wait(second: Int, vararg page: PageState2) {
        sleep(200)
        for (temp in page) {
            if (mDevice.hasObject(temp.flagSelector)) {
                return
            }
        }
        if (second <= 0) {
            return
        }
        sleep(800)
        wait(second - 1, *page)
    }

    open fun onMachineCrashed(e: Exception) {
        logcat(e)
    }


    open inner class PageState2(val name: String, val flagSelector: BySelector) {
        private val TAG = this.javaClass.simpleName
        private val caches = ArrayList<UiObject2>(10)

        //exitMaskPage();
        val isPageInFont: Boolean
            get() = mDevice.hasObject(flagSelector)

        open fun enterState() {
            logcat("进入----$name")
        }

        fun exitState() {
            logcat("退出****$name")
            for (temp in caches) {
                temp.recycle()
            }
            caches.clear()
        }

        open fun handleMessage(msg: Message): Boolean {
            logcat(name + " handleMsg:" + getMsgDes(msg.what))
            dealMessage(msg)
            return false
        }

        protected open fun dealMessage(msg: Message) {}

        fun findObject(selector: BySelector): UiObject2? {
            val temp = mDevice.findObject(selector)
            if (temp != null) {
                caches.add(temp)
            }
            return temp
        }

        fun findObjects(selector: BySelector): List<UiObject2> {
            val result = mDevice.findObjects(selector)
            if (result != null && result.size > 0) {
                caches.addAll(result)
            }
            return result
        }

        private fun findObject(parentObject: UiObject2, selector: BySelector): UiObject2? {
            val result = parentObject.findObject(selector)
            if (result != null) {
                caches.add(result)
            }
            return result
        }

        private fun findObjects(parentObject: UiObject2, selector: BySelector): List<UiObject2>? {
            val result = parentObject.findObjects(selector)
            if (result.isNotEmpty()) {
                caches.addAll(result)
            }
            return result
        }

        fun UiObject2?.myFindObject(selector: BySelector): UiObject2? {
            if (this == null) {
                return null
            }
            return findObject(this, selector)
        }

        fun UiObject2?.myFindObjects(selector: BySelector): List<UiObject2>? {
            if (this == null) {
                return null
            }
            return findObjects(this, selector)
        }
    }
}
