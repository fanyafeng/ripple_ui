package com.ripple.ui.flowview

import java.io.Serializable


/**
 * Author: fanyafeng
 * Data: 2020/6/28 17:12
 * Email: fanyafeng@live.cn
 * Description: 流式布局的行为
 */
interface IFlowView : Serializable {


    /**
     * 设置gravity
     */
    fun setGravity(gravity: FlowViewGravity)

    /**
     * 设置最大行数
     */
    fun setMaxLine(maxLine: Int)


    /**
     * 流式布局Gravity位置显示
     */
    enum class FlowViewGravity {
        LEFT,
        CENTER,
        RIGHT
    }
}