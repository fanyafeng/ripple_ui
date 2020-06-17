package com.ripple.ui.ninegridview.impl

import android.content.Context
import com.ripple.ui.ninegridview.NineGridViewAdapter
import com.ripple.ui.ninegridview.NineItem

/**
 * Author: fanyafeng
 * Data: 2020/5/15 10:45
 * Email: fanyafeng@live.cn
 * Description: 九宫格实例adapter
 */
class NineGridAdapter(private val context: Context, private val list: List<NineItem>) :
    NineGridViewAdapter(context, list) {

}