package com.plbear.base.base.bean

/**
 * created by yanyongjun on 2020/5/23
 */
data class AndroidMsg(
    val accountId: String,
    val content: String,
    val imgList: List<String>,
    val qrCodeStr: String,
    val sendTime: Long
)