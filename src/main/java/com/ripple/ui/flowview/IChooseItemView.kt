package com.ripple.ui.flowview

import java.io.Serializable


/**
 * Author: fanyafeng
 * Data: 2020/6/28 19:24
 * Email: fanyafeng@live.cn
 * Description: 多项选择view控件的单个控件的行为
 */
interface IChooseItemView:Serializable {

    /**
     * 是否可以被选中
     */
    fun isCheckable(): Boolean

    /**
     * 设置其是否可以被选中
     */
    fun setCheckable(isCheckable: Boolean)

    /**
     * 是否被选中
     */
    fun isChecked(): Boolean

    /**
     * 设置其被选中
     */
    fun setChecked(isChecked: Boolean)

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    fun toggle()

}