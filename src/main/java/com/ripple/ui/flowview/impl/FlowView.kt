package com.ripple.ui.flowview.impl

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.ripple.ui.flowview.IFlowView


/**
 * Author: fanyafeng
 * Data: 2020/6/28 17:46
 * Email: fanyafeng@live.cn
 * Description: 流式布局
 */
open class FlowView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ViewGroup(
        mContext,
        attrs,
        defStyleAttr
    ), IFlowView {

    /**
     * 所有的横行和纵行view
     */
    val allViews: MutableList<MutableList<View>> = arrayListOf()

    /**
     * 每一行的高度
     */
    private val mLineHeight: MutableList<Int> = arrayListOf()

    /**
     * 每一行的宽度
     */
    private val mLineWidth: MutableList<Int> = arrayListOf()

    /**
     * 每一横行的view
     */
    private var lineViews: MutableList<View> = arrayListOf()

    var mGravity = IFlowView.FlowViewGravity.LEFT

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        /**
         * 获取宽度尺寸
         */
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)

        /**
         * 获取宽度模式
         */
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)

        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        val modeHeight = MeasureSpec.getMode(heightMeasureSpec)

        var width = 0
        var height = 0

        var lineWidth = 0
        var lineHeight = 0


        val mCount = childCount
        (0 until mCount).forEach {
            val child = getChildAt(it)
            if (child.visibility == View.GONE) {
                if (it == mCount - 1) {
                    width = lineWidth.coerceAtLeast(width)
                    height += lineHeight
                    return@forEach
                }
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin

            if (lineWidth + childWidth > sizeWidth - paddingLeft - paddingRight) {
                width = width.coerceAtLeast(lineWidth)
                lineWidth = childWidth
                height += lineHeight
                lineHeight = childHeight
            } else {
                lineWidth += childWidth
                lineHeight = lineHeight.coerceAtLeast(childHeight)
            }

            if (it == mCount - 1) {
                width = lineWidth.coerceAtLeast(width)
                height += lineHeight
            }
        }
        setMeasuredDimension(
            if (modeWidth == MeasureSpec.EXACTLY) sizeWidth else width + paddingLeft + paddingRight,
            if (modeHeight == MeasureSpec.EXACTLY) sizeHeight else height + paddingTop + paddingRight
        )

    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        allViews.clear()
        mLineHeight.clear()
        mLineWidth.clear()
        lineViews.clear()

        val mWidth = width
        var lineWidth = 0
        var lineHeight = 0
        val mCount = childCount

        (0 until mCount).forEach {
            val child = getChildAt(it)
            if (child.visibility == View.GONE) return@forEach

            val lp = child.layoutParams as MarginLayoutParams
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - paddingLeft - paddingRight) {
                mLineHeight.add(lineHeight)
                allViews.add(lineViews)
                mLineWidth.add(lineWidth)

                lineWidth = 0
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin
                lineViews = mutableListOf()
            }

            lineWidth += childWidth + lp.leftMargin + lp.rightMargin
            lineHeight = lineHeight.coerceAtLeast(childHeight + lp.topMargin + lp.bottomMargin)
            lineViews.add(child)
        }

        mLineHeight.add(lineHeight)
        mLineWidth.add(lineWidth)
        allViews.add(lineViews)

        var mPaddingLeft = paddingLeft
        var mPaddingTop = paddingTop
        val lineNum = allViews.size

        (0 until lineNum).forEach {
            lineViews = allViews[it]
            lineHeight = mLineHeight[it]

            val currentLineWidth = mLineWidth[it]
            mPaddingLeft = when (mGravity) {
                IFlowView.FlowViewGravity.LEFT ->
                    paddingLeft
                IFlowView.FlowViewGravity.CENTER ->
                    (width - currentLineWidth) / 2 + paddingLeft
                IFlowView.FlowViewGravity.RIGHT ->
                    width - currentLineWidth + paddingLeft
            }

            val lineCount = lineViews.size
            (0 until lineCount).forEach innerForEach@{ i ->
                val child = lineViews[i]
                if (child.visibility == View.GONE) {
                    return@innerForEach
                }

                val lp = child.layoutParams as MarginLayoutParams
                val lc = mPaddingLeft + lp.leftMargin
                val tc = mPaddingTop + lp.topMargin
                val rc = lc + child.measuredWidth
                val bc = tc + child.measuredHeight

                child.layout(lc, tc, rc, bc)

                mPaddingLeft += child.measuredWidth + lp.leftMargin + lp.rightMargin
            }

            mPaddingTop += lineHeight
        }

    }

    override fun setGravity(gravity: IFlowView.FlowViewGravity) {
        this.mGravity = gravity
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(mContext, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }


}