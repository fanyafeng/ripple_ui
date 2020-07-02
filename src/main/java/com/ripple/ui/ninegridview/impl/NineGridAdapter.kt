package com.ripple.ui.ninegridview.impl

import android.content.Context
import android.widget.ImageView
import com.ripple.tool.density.dp2pxF
import com.ripple.ui.ninegridview.NineGrid
import com.ripple.ui.ninegridview.NineGridViewAdapter
import com.ripple.ui.ninegridview.NineItem
import com.ripple.ui.widget.RippleImageView

/**
 * Author: fanyafeng
 * Data: 2020/5/15 10:45
 * Email: fanyafeng@live.cn
 * Description: 九宫格实例adapter
 * 以下为demo可以据此进行改版
 */
open class NineGridAdapter(
    private val mContext: Context,
    private val list: List<NineItem>,
    private val maxCount: Int
) :
    NineGridViewAdapter(mContext, list) {


    override fun onCreateView(position: Int, nineGrid: NineGrid): RippleImageView {
        return if (position + 1 == maxCount && list.size > maxCount) {
            val itemView = RippleImageView(mContext)
            itemView.scaleType = ImageView.ScaleType.CENTER_CROP
            itemView.roundRadio = 4.dp2pxF
            itemView.hintText = "+" + (list.size - maxCount)
            itemView
        } else {
            super.onCreateView(position, nineGrid)
        }
    }
}