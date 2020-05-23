package com.plbear.base.base.bean

/**
 * created by yanyongjun on 2020/5/23
 */
class DataWrapper<T> {
    var code = 0
    var msg: String? = null
    var success: Boolean = false
    var data: T? = null
}