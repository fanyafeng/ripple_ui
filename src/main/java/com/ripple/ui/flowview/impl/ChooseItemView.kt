package com.ripple.ui.flowview.impl

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.ripple.ui.R
import com.ripple.ui.flowview.IChooseItemView
import com.ripple.ui.flowview.IChooseModel
import kotlinx.android.synthetic.main.item_choose_view_layout.view.*


/**
 * Author: fanyafeng
 * Data: 2020/6/28 19:53
 * Email: fanyafeng@live.cn
 * Description:
 */
open class ChooseItemView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(mContext, attrs, defStyleAttr), IChooseItemView {

    init {
        LayoutInflater.from(mContext).inflate(R.layout.item_choose_view_layout, this)
        rippleChooseItemView.setBackgroundResource(R.drawable.choose_view_unselected)
    }

    private var checked = false
    private var checkable = true

    fun <T : IChooseModel> initData(model: T) {
        rippleChooseItemView.text = model.getChooseItemTitle()
        setCheckable(model.getChooseItemCheckable())
        setChecked(model.getChooseItemChecked())
    }

    fun getChooseItemView() = getChildAt(0)

    override fun isCheckable(): Boolean {
        return checkable
    }

    override fun setCheckable(isCheckable: Boolean) {
        checkable = isCheckable
        if (!checkable) {
            rippleChooseItemView.setBackgroundResource(R.drawable.choose_view_unselectable)
        }
    }

    override fun isChecked(): Boolean {
        return if (!checkable) {
            false
        } else {
            checked
        }
    }

    override fun setChecked(isChecked: Boolean) {
        if (checkable) {
            checked = isChecked
            if (checked) {
                rippleChooseItemView.setBackgroundResource(R.drawable.choose_view_selected)
            } else {
                rippleChooseItemView.setBackgroundResource(R.drawable.choose_view_unselected)
            }
        } else {
            //不能进行操作，可以在此添加回调
        }
    }

    override fun toggle() {
        if (checkable) {
            setChecked(!checked)
        }
    }
}