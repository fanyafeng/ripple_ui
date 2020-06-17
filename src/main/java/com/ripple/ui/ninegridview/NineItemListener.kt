package com.ripple.ui.ninegridview

import android.view.View
import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/13 13:57
 * Email: fanyafeng@live.cn
 * Description: 九宫格单个item view的点击回调
 */
interface NineItemListener : Serializable {
    /**
     * 点击回调
     */
    fun onClickListener(view: View, item: NineItem, position: Int)

    /**
     * 长按回调
     */
    fun onLongClickListener(view: View, item: NineItem, position: Int): Boolean


    /**
     * 简化listener调用，一般只用点击回调，很少用到长按回调
     */
    interface SimpleNineItemListener : NineItemListener {

        override fun onLongClickListener(view: View, item: NineItem, position: Int): Boolean {
            return false
        }
    }
}