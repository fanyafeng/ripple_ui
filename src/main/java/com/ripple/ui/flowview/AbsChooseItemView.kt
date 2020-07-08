package com.ripple.ui.flowview

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
abstract class AbsChooseItemView @JvmOverloads constructor(
    private var mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(mContext, attrs, defStyleAttr), IChooseItemView {

    private var mChecked = false
    private var mCheckable = true

    /**
     * 标签背景的三种状态
     */
    open var chooseViewUnselectable = R.drawable.choose_view_unselectable
    open var chooseViewSelected = R.drawable.choose_view_selected
    open var chooseViewUnselected = R.drawable.choose_view_unselected

    /**
     * 标签内部字体的三种颜色
     */
    open var unselectableTagColor = Color.parseColor("#cccccc")
    open var selectedTagColor = Color.parseColor("#ff680a")
    open var unselectedTagColor = Color.BLACK

    init {
        initView()
    }

    fun initView() {
        LayoutInflater.from(mContext).inflate(R.layout.item_choose_view_layout, this)
        rippleChooseItemView.setBackgroundResource(chooseViewUnselected)
    }

    fun <T : AbsChooseItemView> updateStatus(newItemView: T) {
        //将新的itemView的属性赋值到旧的itemView上
        setInnerTagLayoutParams(newItemView.getInnerTagLayoutParams())
        //更新标签选中态背景
        this.chooseViewUnselected = newItemView.chooseViewUnselected
        this.chooseViewSelected = newItemView.chooseViewSelected
        this.chooseViewUnselectable = newItemView.chooseViewUnselectable
        //更新标签选中态字体颜色
        this.unselectableTagColor = newItemView.unselectableTagColor
        this.unselectedTagColor = newItemView.unselectedTagColor
        this.selectedTagColor = newItemView.selectedTagColor
    }


    fun <T : IChooseModel> initData(model: T) {
        rippleChooseItemView.text = model.getChooseItemTitle()
        setCheckable(model.getChooseItemCheckable())
        setChecked(model.getChooseItemChecked())
    }

    fun getChooseItemView() = getChildAt(0)

    /**
     * 是否可点击
     */
    override fun isCheckable(): Boolean {
        return mCheckable
    }

    /**
     * 设置点击状态
     */
    override fun setCheckable(isCheckable: Boolean) {
        mCheckable = isCheckable
        if (!mCheckable) {
            rippleChooseItemView.setBackgroundResource(chooseViewUnselectable)
            rippleChooseItemView.setTextColor(unselectableTagColor)
        }
    }

    /**
     * 获取选中状态
     */
    override fun isChecked(): Boolean {
        return if (!mCheckable) {
            false
        } else {
            mChecked
        }
    }

    /**
     * 设置选中状态
     */
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

    /**
     * 变换选中状态
     */
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
     * 获取内部标签LayoutParams
     */
    fun getInnerTagLayoutParams(): RelativeLayout.LayoutParams =
        rippleChooseItemView.layoutParams as RelativeLayout.LayoutParams

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

    fun getTagView() = rippleChooseItemView

    fun getTagLayoutView() = rippleChooseItemViewLayout
}