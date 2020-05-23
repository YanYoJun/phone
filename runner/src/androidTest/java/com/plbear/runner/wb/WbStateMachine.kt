package com.plbear.runner.wb

import android.os.Message
import android.widget.TextView
import androidx.test.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import com.plbear.base.base.Shell
import com.plbear.base.base.base.App
import com.plbear.base.base.bean.AndroidMsg
import com.plbear.base.base.logcat
import com.plbear.base.base.network.ApiFactory
import com.plbear.base.base.utils.GsonManager
import com.plbear.base.base.utils.ToastManager
import com.plbear.base.base.utils.Utils
import com.plbear.runner.base.StateMachine
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsNull
import java.io.File

/**
 * created by yanyongjun on 2020-04-15
 */
class WbStateMachine(device: UiDevice) : StateMachine(device) {
    companion object {
        const val PACKAGE_NAME = "com.sina.weibo"

        const val MSG_INIT = 0
    }

    private var mLastSendTime = 0L
    private var mAndroidMsg: AndroidMsg? = null
    private val mImgPath = "/sdcard/mahuateng/WeiBoImg/"

    private val mHomePage: PageState2 =
        object : PageState2("首页", By.res("com.sina.weibo:id/titleSave")) {
            override fun dealMessage(msg: Message) {
                if (!isPageInFont) {
                    restart()
                    return
                }
                when (msg.what) {
                    MSG_INIT -> {
                        mAndroidMsg = null
                        if (mLastSendTime > System.currentTimeMillis() - 5 * 60 * 1000) {
                            ToastManager.showToast("等待5分钟再次获取任务")
                            Thread.sleep(5000)
                            sendMessage(MSG_INIT)
                            return
                        }
                        val mahuateng = File("/sdcard/mahuateng")
                        if (!mahuateng.exists()){
                            logcat("create mahuateng")
                            mahuateng.mkdirs()
                        }
                        val img = File("/sdcard/mahuateng/WeiBoImg")
                        if (!img.exists()){
                            logcat("create WeiBoImg")
                            img.mkdirs()
                        }

                        val request = ApiFactory.apiService().getNextCommodity().execute().body()
                        if (request == null || request.success == false) {
                            logcat("request:${GsonManager.instance.toJson(request)}")
                            ToastManager.showToast("请求结果为空")
                            Thread.sleep(5000)
                            sendMessage(MSG_INIT)
                            return
                        }
                        mAndroidMsg = request.data
                        mLastSendTime = System.currentTimeMillis()
                        //下载图片
                        File(mImgPath).deleteRecursively()
                        File(mImgPath).mkdirs()
                        var i = 0
                        mAndroidMsg?.imgList?.forEach {
                            val name = "${mLastSendTime}_${i++}.${it.substringAfterLast(".")}"
                            Utils.downloadImage(App.instance(), it, mImgPath, name)
                        }

                        logcat("到首页了")
                        mDevice.click(111, 1850) //首页的按钮
                        logcat("点击首页完毕")
                        sleep(5000)
                        wait(By.res("com.sina.weibo:id/titleSave"))
                        val selector = UiSelector().apply {
                            this.resourceId("com.sina.weibo:id/titleSave")
                            this.className(TextView::class.java)
                        }
//                        val add = mDevice.findObject(selector)
                        val add = findObject(By.res("com.sina.weibo:id/titleSave").clazz(TextView::class.java).enabled(true))
                        if (add == null) {
                            logcat("没有找到发送按钮")
                            restart()
                            return
                        }

                        logcat("点击")
                        mDevice.click(993,136)
//                        add.click()
//                        val bounds = add.visibleBounds
//                        logcat("left:${bounds.left} right:${bounds.right} top:${bounds.top} bottom:${bounds.bottom}")
//                        mDevice.click(
//                            (bounds.left + bounds.right) / 2,
//                            (bounds.top + bounds.bottom) / 2
//                        )
                        wait(By.text("写微博"))
                        val send = findObject(By.text("写微博"))
                        send?.click()
                        wait(mSendPage)
                        if (!mSendPage.isPageInFont) {
                            logcat("无法到达发送微博页面, 关闭")
                            restart()
                            return
                        }
                        transToStateWithMsg(mSendPage, msg)
                    }
                }
            }
        }

    private val mSendPage: PageState2 =
        object : PageState2("发微博", By.res("com.sina.weibo:id/titleText").text("发微博")) {
            override fun dealMessage(msg: Message) {
                findObject(By.res("com.sina.weibo:id/delete_poi_location"))?.click()
                findObject(By.res("com.sina.weibo:id/edit_view"))?.click()
                findObject(By.res("com.sina.weibo:id/edit_view"))?.text = mAndroidMsg?.content
                findObject(By.desc("插入图片"))?.click()
//                findObject(By.res("com.sina.weibo:id/photo_album_title_text"))?.click()
                sleep(2000)
                mDevice.click(576,146)
                sleep(2000)
                findObject(
                    By.res("com.sina.weibo:id/photo_album_listview_item_name").text("WeiBoImg")
                )?.click()
                sleep(2000)
                val imgList =
                    findObjects(By.res("com.sina.weibo:id/photo_album_grideview_item_select"))
                imgList.forEach {
                    it.click()
                    sleep(1000)
                }
                mDevice.click(225, 2079) //todo 不发原图
//                findObject(By.textContains("下一步"))?.click()
//                findObject(By.textContains("下一步"))?.click()
                sleep(1000)
                mDevice.click(989,145)
                sleep(1000)
                mDevice.click(989,145)
//                sleep()
//                findObject(By.text("发送"))?.click()
                sleep(1000)
                mDevice.click(1000,128)
                sleep(10 * 1000)
                transToStateWithMsg(mHomePage, msg)
            }
        }

    init {
        logcat("init2")
        addState(mHomePage, mSendPage)
        setInitState(mHomePage)

        mDevice.registerWatcher("评分") {
            if (mDevice.hasObject(By.text("不了，谢谢"))) {
                val obj = mDevice.findObject(By.text("不了，谢谢"))
                obj.click()
                true
            }
            false
        }
    }

    private fun restart() {
        logcat("restart")
        launchInitState()
        transToStateWithMsg(mHomePage, MSG_INIT)
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
        mDevice.wait(Until.hasObject(mInitState.flagSelector), 4000)
        logcat("launch over")
    }

    override fun getMsgDes(msg: Int): String {
        return when (msg) {
            MSG_INIT -> "MSG_INIT"
            else -> ""
        }
    }

    override fun onMachineCrashed(e: Exception) {
        super.onMachineCrashed(e)
        restart()
    }
//adb shell am instrument -w -r   -e debug false -e class 'com.plbear.runner.wb.WbTest#test' com.plbear.runner.test/androidx.test.runner.AndroidJUnitRunner
}