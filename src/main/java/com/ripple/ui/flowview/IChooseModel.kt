package com.ripple.ui.flowview

import java.io.Serializable


/**
 * Author: fanyafeng
 * Data: 2020/6/29 09:31
 * Email: fanyafeng@live.cn
 * Description: 流式布局item
 *
 * data model需要继承这个接口
 */
interface IChooseModel : Serializable {

    /**
     * 单个view标题
     */
    fun getChooseItemTitle(): String

    /**
     * 是否可以被点击
     */
    fun getChooseItemCheckable(): Boolean

    /**
     * 是否被选中
     */
    fun getChooseItemChecked(): Boolean
}