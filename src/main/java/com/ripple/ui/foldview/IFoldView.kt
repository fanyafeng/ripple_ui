package com.ripple.ui.foldview


/**
 * Author: fanyafeng
 * Data: 2020/7/2 10:47
 * Email: fanyafeng@live.cn
 * Description: fold view行为抽象
 */
interface IFoldView {
    /**
     * 获取文案折叠状态
     * 如果折叠则返回true
     * 因为通常是关闭状态这里采用的命名为是否文案被折叠
     */
    fun getFoldViewStatus(): Boolean

    /**
     * 设置文案折叠状态
     */
    fun setFoldViewStatus(close: Boolean)

    /**
     * 设置最小显示行数
     * 如果超过则显示【展开】文案
     */
    fun setFoldViewMaxLine(maxLine: Int)

    /**
     * 展开后是否支持关闭
     * 1.当支持关闭时则用户点击关闭需要将其设置为最小显示行数，并且更新view
     * 2.如果不支持关闭则展开后就会一直以此状态显示
     */
    fun setFoldViewOnlyUnfold(unfold: Boolean)

    /**
     * 设置的显示文案
     */
    fun setFoldViewDesc(desc: CharSequence)

    /**
     * 设置展开是文案
     */
    fun setUnfoldText(unFoldText: CharSequence)

    /**
     * 设置关闭时文案
     */
    fun setFoldText(foldText: CharSequence)
}