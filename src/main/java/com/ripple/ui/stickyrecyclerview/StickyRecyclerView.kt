package com.ripple.ui.stickyrecyclerview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.OverScroller
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.ripple.log.LogFactory
import com.ripple.ui.R
import java.lang.Exception


/**
 * Author: fanyafeng
 * Data: 2020/8/24 09:27
 * Email: fanyafeng@live.cn
 * Description:
 */
class StickyRecyclerView @JvmOverloads constructor(
    val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(mContext, attrs, defStyleAttr),
    NestedScrollingParent
//    , NestedScrollingParent2, NestedScrollingParent3,
//    NestedScrollingChild, NestedScrollingChild2, NestedScrollingChild3
{

    /*
    头部Recyclerview
     */
    private var nestedTopRecyclerView: RecyclerView? = null

    /**
     * 顶部view的高度
     */
    private var topViewHeight: Int = 0

    /*
    指示器
     */
    private var nestedIndicator: View? = null

    /*
    底部ViewPager
     */
    private var nestedViewPager: ViewPager? = null

    /*
     * 可以滚动超出边界的scroller
     */
    private var overScroller = OverScroller(mContext)

    /*
     * 纵向滑动变量增量
     */
    private var deltaY = 0

    init {
        /*
        纵向布局
        分为三个部分：
        头部 Recyclerview
        中部 指示器
        底部 Viewpager
         */
        orientation = VERTICAL
    }

    /**
     * 布局完成
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        /*
        头部必须为Recyclerview
         */
        val topRecyclerView: View = findViewById(R.id.nestedTopRecyclerView)
        if (topRecyclerView !is RecyclerView) {
            throw Exception("nestedTopRecyclerView should used by RecyclerView!")
        }
        nestedTopRecyclerView = topRecyclerView
        nestedIndicator = findViewById(R.id.nestedIndicator)
        /*
        底部必须为ViewPager
         */
        val targetViewPager: View = findViewById(R.id.nestedViewPager)
        if (targetViewPager !is ViewPager) {
            throw Exception("nestedViewPager should used by nestedViewPager!")
        }
        nestedViewPager = targetViewPager
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        val hiddenTop = dy > 0 && scrollY < topViewHeight
        val showTop = dy < 0 && scrollY >= 0 && !target.canScrollVertically(-1)
        if (hiddenTop || showTop) {
            scrollBy(0, dy)
            consumed[1] = dy
        }
    }

    /**
     * 父视图是否接受嵌套滚动操作
     */
    override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean {
        return true
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        if (scrollY >= topViewHeight) {
            return false
        }
        fling(velocityY.toInt())
        return true
    }

    /**
     * 测量view的高度
     *
     * 关于view测量高度的文章：
     * https://www.jianshu.com/p/d16ec64181f2
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        /**
         * 不限定子控件高度
         * 给它想要的高度
         */
        getChildAt(0).measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        /**
         * 给定viewpager的高度
         */
        val params = nestedViewPager?.layoutParams
        /**
         * 需要当前控件的高度减去吸顶view的高度
         */
        params?.height = measuredHeight - (nestedIndicator?.measuredHeight ?: 0)
        setMeasuredDimension(
            measuredWidth,
            (nestedTopRecyclerView?.measuredHeight ?: 0) + (nestedTopRecyclerView?.measuredHeight
                ?: 0) + (nestedTopRecyclerView?.measuredHeight ?: 0)
        )
        /**
         * fix bug if version>7
         */
        topViewHeight = nestedTopRecyclerView?.measuredHeight ?: 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        /**
         * 需要更新顶部view高度
         */
        topViewHeight = nestedTopRecyclerView?.measuredHeight ?: 0
    }

    /**
     * @param velocityY 纵轴滑动加速度
     * 根据纵轴滑动加速度去进行view的fling滑动操作
     * 将当前view滚动到顶部
     * 应对用户fling的滑动操作
     */
    private fun fling(velocityY: Int) {
        overScroller.fling(0, scrollY, 0, velocityY, 0, 0, 0, topViewHeight)
        invalidate()
        Log.d("topViewHeight", "topViewHeight:" + topViewHeight);
        Log.d("nestedViewPager", "nestedViewPager:" + nestedViewPager?.height);
    }


    override fun scrollTo(x: Int, y: Int) {
        var toY = y
        /**
         * 当滚动的纵轴为负值时校正纵轴距离
         * 将其置为0
         */
        if (y < 0) {
            toY = 0
        }

        if (toY > topViewHeight) {
            toY = topViewHeight
            deltaY = toY
        }
        if (toY != scrollY) {
            deltaY = toY
            super.scrollTo(x, toY)
        }

    }

    override fun computeScroll() {
//        super.computeScroll()
        if (overScroller.computeScrollOffset()) {
            LogFactory.d(this.javaClass, "overScroller.currY:" + overScroller.currY)
            scrollTo(0, overScroller.currY)
            invalidate()
        }
    }
}