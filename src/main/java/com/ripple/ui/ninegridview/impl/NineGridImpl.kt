package com.ripple.ui.ninegridview.impl

import com.ripple.ui.ninegridview.NineGrid

/**
 * Author: fanyafeng
 * Data: 2020/5/13 14:35
 * Email: fanyafeng@live.cn
 * Description: 配置的示例化代码
 */
class NineGridImpl @JvmOverloads constructor(
    private var divide: Int = 8,
    private var singleWidth: Int = 1000,
    private var ratio: Float = 1F,
    private var perLineCount: Int = 3,
    private var maxLine: Int = 3
) : NineGrid {
    override fun setDivide(divide: Int) {
        this.divide = divide
    }

    override fun getDivide(): Int {
        return divide
    }

    override fun setSingleWidth(singleWidth: Int) {
        this.singleWidth = singleWidth
    }

    override fun getSingleWidth(): Int {
        return singleWidth
    }

    override fun setSingleImageRatio(ratio: Float) {
        this.ratio = ratio
    }

    override fun getSingleImageRatio(): Float {
        return ratio
    }

    override fun setPerLineCount(count: Int) {
        this.perLineCount = count
    }

    override fun getPerLineCount(): Int {
        return perLineCount
    }

    override fun setMaxLine(maxLine: Int) {
        this.maxLine = maxLine
    }

    override fun getMaxLine(): Int {
        return maxLine
    }

    override fun toString(): String {
        return "NineGridImpl(divide=$divide, singleWidth=$singleWidth, ratio=$ratio, perLineCount=$perLineCount, maxLine=$maxLine)"
    }


}