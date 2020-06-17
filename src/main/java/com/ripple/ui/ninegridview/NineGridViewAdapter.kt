package com.ripple.ui.ninegridview

import android.content.Context
import android.widget.ImageView
import com.ripple.ui.RippleImageView
import java.io.Serializable

/**
 * Author: fanyafeng
 * Data: 2020/5/15 10:29
 * Email: fanyafeng@live.cn
 * Description:
 */
abstract class NineGridViewAdapter @JvmOverloads constructor(
    private val mContext: Context,
    private var list: List<NineItem> = mutableListOf()
) : Serializable {

    fun onCreateView(position: Int, nineGrid: NineGrid): RippleImageView {
        //position一般用于最后一个ImageView的显示
        val itemView = RippleImageView(mContext)
        itemView.scaleType = ImageView.ScaleType.CENTER_CROP
        return itemView
    }

    fun setImageList(list: List<NineItem>) {
        this.list = list
    }

    fun getImageList(): List<NineItem> = list

}