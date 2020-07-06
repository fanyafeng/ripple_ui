package com.ripple.ui.stickynavigationlayout


/**
 * Author: fanyafeng
 * Data: 2020/7/3 14:19
 * Email: fanyafeng@live.cn
 * Description:
 */
interface IStickyNavigationLayout {

    /**
     * 吸顶后是否还能拉下来
     */
    fun setCanPullDown(canPullDown: Boolean)

    fun canPullDown(): Boolean
}