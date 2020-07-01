package com.ripple.ui.ninegridview.impl

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.ripple.tool.kttypelians.TripleLambda
import com.ripple.ui.widget.RippleImageView
import com.ripple.ui.ninegridview.*

/**
 * Author: fanyafeng
 * Data: 2020/5/13 14:02
 * Email: fanyafeng@live.cn
 * Description: 图片九宫格view
 * 主要是以下两种方式：
 * 单张图片：
 *
 *           屏幕宽度
 * ---------------------------
 * 图片
 * -----------
 * |         |
 * |         |
 * |         |
 * |         |
 * |         |
 * |         |
 * -----------
 *
 * ----------------------------
 *
 * 再有就是多张图
 * 多张图可以设置一行最多显示多少图片 eg:3
 * 再有就是最多显示多少行 eg:3
 * 图片宽高比是一比一
 * eg:如果现在是四张图
 * 那么第一行显示三张，平分view宽度，
 * 三张的话还剩下一张去显示，那样的话就需要和第一行的排布一样
 * 比例还是一比一，但是后面两个空位也会占用三分之二
 * 类似权重，虽然不显示但是需要占位
 * 可以直接类比微信
 *
 *                       屏幕宽度
 * ----------------------------------------
 * ----------    ----------     -----------
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * ----------    ----------     -----------
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * ----------    ----------     -----------
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * ----------    ----------     -----------
 * ----------------------------------------
 *
 * 四张显示如下，未做特殊处理
 *
 *                       屏幕宽度
 * ----------------------------------------
 * ----------    ----------     -----------
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * ----------    ----------     -----------
 * |        |
 * |        |
 * |        |
 * |        |
 * ----------
 * ----------------------------------------
 */
class NineGridView @JvmOverloads constructor(
    private val mContext: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ViewGroup(mContext, attrs, defStyleAttr), NineGrid {

    companion object {
        private val TAG = NineGridView::class.java.simpleName

//        /**
//         * 一行最多显示多少张图片
//         */
//        private const val PER_LINE_COUNT = 3
//
//        /**
//         * 最多显示多少行
//         */
//        private const val MAX_LINE = 3
//
//        /**
//         * 图片最大显示数量
//         */
//        private const val MAX_SIZE = PER_LINE_COUNT * MAX_LINE
    }

    private var currentLine = 0

    private val viewList: ArrayList<RippleImageView> = arrayListOf()

    var nineItemListener: NineItemListener? = null

    var loadFrame: NineGridLoadFrame? = null

    /**
     * 回调监听
     * 为了简便添加了kt的点击回调lambda
     * 长按就按照正常的java调用就可以
     */
    var onItemClickListener: TripleLambda<View, NineItem, Int> = null

    /**
     * 属性配置
     */
    var nineGridConfig: NineGrid = NineGridImpl()

    /**
     * 可以自定义配置
     */

    init {

    }

    override fun setDivide(divide: Int) {
        nineGridConfig.setDivide(divide)
    }

    override fun getDivide(): Int {
        return nineGridConfig.getDivide()
    }

    override fun setSingleWidth(singleWidth: Int) {
        nineGridConfig.setSingleWidth(singleWidth)
    }

    override fun getSingleWidth(): Int {
        return nineGridConfig.getSingleWidth()
    }

    override fun setSingleImageRatio(ratio: Float) {
        nineGridConfig.setSingleImageRatio(ratio)
    }

    override fun getSingleImageRatio(): Float {
        return nineGridConfig.getSingleImageRatio()
    }

    override fun setPerLineCount(count: Int) {
        nineGridConfig.setPerLineCount(count)
    }

    override fun getPerLineCount(): Int {
        return nineGridConfig.getPerLineCount()
    }

    override fun setMaxLine(maxLine: Int) {
        nineGridConfig.setMaxLine(maxLine)
    }

    override fun getMaxLine(): Int {
        return nineGridConfig.getMaxLine()
    }

    private var mImageList: List<NineItem>? = null

    var adapter: NineGridViewAdapter? = null
        set(value) {
            field = value
            val perLineCount = getPerLineCount()
            val maxSize = getMaxLine() * getPerLineCount()
            var size = value?.getImageList()?.size ?: 0
            currentLine = if (size % perLineCount == 0) {
                size / perLineCount
            } else {
                size / perLineCount + 1
            }

            val imageList = value?.getImageList()
            if (imageList?.isNotEmpty() == true) {
                visibility = View.VISIBLE
                val trueList = if (size > maxSize) imageList.subList(0, maxSize) else imageList
                size = trueList.size

                if (mImageList != null) {
                    val oldViewCount = mImageList!!.size
                    val newViewCount = size

                    if (oldViewCount > newViewCount) {
                        removeViews(newViewCount, oldViewCount - newViewCount)
                    } else if (oldViewCount < newViewCount) {
                        for (i in oldViewCount until newViewCount) {
                            val item = getItemView(i)
                            item?.let {
                                addView(it, generateDefaultLayoutParams())
                            }
                        }
                    }

                } else {
                    (0 until size).forEachIndexed { _, i ->
                        val item = getItemView(i)
                        item?.let {
                            addView(it, generateDefaultLayoutParams())
                        }
                    }
                }


            } else {
                visibility = View.GONE
            }

            if (adapter?.getImageList()?.size ?: 0 > maxSize) {
                val lastItem = getChildAt(maxSize - 1)
//                if (lastItem is RippleImageView) {
//                    val trueCount = adapter?.getImageList()?.size ?: 0
//                    val excessCount = trueCount - maxSize
//                    lastItem.hintText = "+$excessCount"
//                }
            }

            mImageList = imageList
            requestLayout()
        }

    init {
        /**
         * 需要添加view
         */
    }

    private var itemWidth: Int? = null
    private var itemHeight: Int? = null


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val perLineCount = getPerLineCount()
        var width = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0
        val totalWidth = width - paddingLeft - paddingRight
        val imageList = adapter?.getImageList()
        val singleWidth = getSingleWidth()
        val imageRatio = getSingleImageRatio()
        val divide = getDivide()
        if (imageList?.isNotEmpty() == true) {
            if (imageList.size == 1) {
                itemWidth = if (singleWidth > totalWidth) totalWidth else singleWidth
                itemWidth?.let { nineGridWidth ->
                    itemHeight = (nineGridWidth / imageRatio).toInt()
                    itemHeight?.let { nineGridHeight ->
                        if (nineGridHeight > singleWidth) {
                            val ratio = singleWidth * 1F / nineGridHeight
                            itemWidth = (nineGridWidth * ratio).toInt()
                            itemHeight = singleWidth
                        }
                    }
                }
            } else {
                itemWidth = (totalWidth - divide * (perLineCount - 1)) / perLineCount
                itemHeight = itemWidth
            }
            itemWidth?.let {
                width =
                    it * perLineCount + divide * (perLineCount - 1) + paddingLeft + paddingRight
            }
            itemHeight?.let {
                height = it * currentLine + divide * (currentLine - 1) + paddingTop + paddingBottom
            }
        }
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
//        Log.d(TAG, "onLayout 获取宽度：" + width)
//        Log.d(TAG, "onLayout 获取高度：" + height)

        /**
         * 需要在此处添加view
         * 根据图片列表的数量进行不同形式的布局
         */

        val divide = getDivide()
        val perLineCount = getPerLineCount()

//        Log.d(TAG, "看看divide的值：" + divide)
//        Log.d(TAG, "看看itemWidth的值：" + itemWidth)
//        Log.d(TAG, "看看itemHeight的值：" + itemHeight)

        val imageList = adapter?.getImageList()
        imageList?.forEachIndexed { index, item ->
            val itemView = getChildAt(index)
            if (itemView != null) {
                val rowNumber: Int = index / perLineCount
                val columnNumber: Int = index % perLineCount
                val mLeft = ((itemWidth ?: 0) + divide) * columnNumber + paddingLeft
                val mTop = ((itemHeight ?: 0) + divide) * rowNumber + paddingTop
                val mRight = mLeft + (itemWidth ?: 0)
                val mBottom = mTop + (itemHeight ?: 0)
//            Log.d(TAG, "left的值：" + mLeft)
//            Log.d(TAG, "top的值：" + mTop)
//            Log.d(TAG, "right的值：" + mRight)
//            Log.d(TAG, "bottom的值：" + mBottom)

                (itemView as RippleImageView).layout(mLeft, mTop, mRight, mBottom)

                loadFrame?.displayImage(context, item.getPath(), itemView)
            }
        }
    }

    private fun getItemView(position: Int): RippleImageView? {
        adapter?.let {
            val itemView: RippleImageView
            val listSize = viewList.size
            val model = it.getImageList()[position]
            if (position < listSize) {
                itemView = viewList[position]
            } else {
                itemView =
                    adapter?.onCreateView(position, nineGridConfig) ?: RippleImageView(
                        context
                    )
                itemView.setOnClickListener { view ->
                    nineItemListener?.onClickListener(
                        view,
                        model,
                        position
                    )

                    onItemClickListener?.invoke(view, model, position)
                }

                itemView.setOnLongClickListener { view: View ->
                    nineItemListener?.onLongClickListener(view, model, position) ?: false
                }

                viewList.add(itemView)
            }
            return itemView
        }
        return null
    }
}
