package com.ripple.ui.ninegridview

import android.content.Context
import com.ripple.ui.RippleImageView
import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/13 14:00
 * Email: fanyafeng@live.cn
 * Description: 显示图片代理
 */
interface NineGridLoadFrame : Serializable {
    /**
     * 通过外部图片框架进行图片的显示
     */
    fun displayImage(context: Context, path: String, imageView: RippleImageView)
}