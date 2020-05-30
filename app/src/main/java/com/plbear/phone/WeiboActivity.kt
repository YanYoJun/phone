package com.plbear.phone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.plbear.base.base.utils.CmdUtils
import com.plbear.phone.databinding.ActivityWbBinding

/**
 * created by yanyongjun on 2020-04-15
 */
class WeiboActivity : BaseActivity() {
    companion object {
        fun start(context: Context) {
            val i = Intent(context, WeiboActivity::class.java)
            context.startActivity(i)
        }
    }

    private lateinit var binding: ActivityWbBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wb)
        binding.btnStartSend.setOnClickListener {
            CmdUtils.startProtectTest("am instrument -w -r    -e debug false -e class 'com.plbear.runner.wb.WbTest#send' com.plbear.runner.test/androidx.test.runner.AndroidJUnitRunner")
        }
        binding.btnStopSend.setOnClickListener {
            CmdUtils.stop()
        }
    }
}