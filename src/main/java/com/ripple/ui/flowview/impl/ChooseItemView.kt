package com.ripple.ui.flowview.impl

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.ripple.tool.density.dp2px
import com.ripple.ui.R
import com.ripple.ui.flowview.IChooseItemView
import com.ripple.ui.flowview.IChooseModel
import kotlinx.android.synthetic.main.item_choose_view_layout.view.*


/**
 * Author: fanyafeng
 * Data: 2020/6/28 19:53
 * Email: fanyafeng@live.cn
 * Description: 单个item的选中view默认实现
 *
 * 1.包含单个tag三种状态的背景，字体颜色设置
 * 2.内部tag的layoutparams设置
 * 3.内部tag的padding设置
 *
 */
open class ChooseItemView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(mContext, attrs, defStyleAttr), IChooseItemView {

    protected var mChecked = false
    protected var mCheckable = true

    /**
     * 标签背景的三种状态
     */
    var chooseViewUnselectable = R.drawable.choose_view_unselectable
    var chooseViewSelected = R.drawable.choose_view_selected
    var chooseViewUnselected = R.drawable.choose_view_unselected

    /**
     * 标签内部字体的三种颜色
     */
    var unselectableTagColor = Color.BLACK
    var selectedTagColor = Color.parseColor("#ff680a")
    var unselectedTagColor = Color.BLACK

    init {
        LayoutInflater.from(mContext).inflate(R.layout.item_choose_view_layout, this)
        rippleChooseItemView.setBackgroundResource(chooseViewUnselected)
    }



    fun <T : IChooseModel> initData(model: T) {
        rippleChooseItemView.text = model.getChooseItemTitle()
        setCheckable(model.getChooseItemCheckable())
        setChecked(model.getChooseItemChecked())
    }

    fun getChooseItemView() = getChildAt(0)

    override fun isCheckable(): Boolean {
        return mCheckable
    }

    override fun setCheckable(isCheckable: Boolean) {
        mCheckable = isCheckable
        if (!mCheckable) {
            rippleChooseItemView.setBackgroundResource(chooseViewUnselectable)
            rippleChooseItemView.setTextColor(unselectableTagColor)
        }
    }

    override fun isChecked(): Boolean {
        return if (!mCheckable) {
            false
        } else {
            mChecked
        }
    }

    override fun setChecked(isChecked: Boolean) {
        if (mCheckable) {
            mChecked = isChecked
            if (mChecked) {
                rippleChooseItemView.setBackgroundResource(chooseViewSelected)
                rippleChooseItemView.setTextColor(selectedTagColor)
            } else {
                rippleChooseItemView.setBackgroundResource(chooseViewUnselected)
                rippleChooseItemView.setTextColor(unselectedTagColor)
            }
        } else {
            rippleChooseItemView.setBackgroundResource(chooseViewUnselectable)
            rippleChooseItemView.setTextColor(unselectableTagColor)
        }
    }

    override fun toggle() {
        if (mCheckable) {
            setChecked(!mChecked)
        }
    }

    /**
     * 设置内部标签LayoutParams
     */
    fun setInnerTagLayoutParams(layoutParams: RelativeLayout.LayoutParams) {
        rippleChooseItemView.layoutParams = layoutParams
    }

    /**
     * 设置内部标签match_parent
     * 用于单行平分
     */
    fun setInnerTagMatchParent() {
        val layoutParams =
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 28.dp2px)
        setInnerTagLayoutParams(layoutParams)
    }

    /**
     * 设置内部标签wrap_content
     * 用于自适应
     */
    fun setInnerTagWrapContent() {
        val layoutParams =
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 28.dp2px)
        setInnerTagLayoutParams(layoutParams)
    }

    /**
     * 设置内部标签的padding
     * 左上右下
     */
    fun setInnerTagPadding(left: Int, top: Int, right: Int, bottom: Int) {
        rippleChooseItemView.setPadding(left, top, right, bottom)
    }
}