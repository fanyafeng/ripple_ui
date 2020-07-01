package com.ripple.ui.flowview

import java.io.Serializable


/**
 * Author: fanyafeng
 * Data: 2020/6/29 09:53
 * Email: fanyafeng@live.cn
 * Description: 选取模式
 * 分为单选和多选
 */
interface IChooseFlowView : Serializable {

    /**
     * 设置最大选取数量
     */
    fun setMaxChooseCount(maxCount: Int)

    /**
     * 最大的选取数量，默认为1
     */
    fun getMaxChooseCount(): Int

    /**
     * 获取最小选取数量
     */
    fun getMinChooseCount(): Int

    /**
     * 设置最小选取数量
     * 默认为0，并且最大数量不能小于最小数量，但是可以相等
     */
    fun setMinChooseCount(minCount: Int)
}