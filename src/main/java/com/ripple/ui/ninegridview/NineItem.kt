package com.ripple.ui.ninegridview

import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/13 13:43
 * Email: fanyafeng@live.cn
 * Description: 图片九宫格单张需要哪些信息
 */
interface NineItem : Serializable {
    /**
     * 图片路径是必须的，而且不能为空
     */
    fun getPath(): String
}

class SimpleNineItem(var imagePath: String) : NineItem {
    override fun getPath(): String {
        return imagePath
    }
}