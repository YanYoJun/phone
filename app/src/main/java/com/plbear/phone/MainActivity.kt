package com.plbear.phone

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.plbear.base.base.base.App
import com.plbear.phone.databinding.ActivityMainBinding
import com.plbear.phone.service.ForegroundService


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.btnWb.setOnClickListener { WeiboActivity.start(this@MainActivity) }
        ForegroundService.start(App.instance())
    }
}
