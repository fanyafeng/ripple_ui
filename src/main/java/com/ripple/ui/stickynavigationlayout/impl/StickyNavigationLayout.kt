package com.ripple.ui.stickynavigationlayout.impl

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.OverScroller
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingParent
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.ViewPager
import com.ripple.log.LogFactory
import com.ripple.tool.extend.forEach
import com.ripple.ui.R
import java.lang.Exception


/**
 * Author: fanyafeng
 * Data: 2020/7/3 14:21
 * Email: fanyafeng@live.cn
 * Description: 吸顶的view
 */
class StickyNavigationLayout @JvmOverloads constructor(
        private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(mContext, attrs, defStyleAttr), NestedScrollingParent {

    /**
     * 顶部view的高度
     */
    private var topViewHeight: Int = 0

    /**
     * 定义顶部view
     */
    private var containerTopView: View? = null

    /**
     * 定义吸顶view
     */
    private var containerNavigationView: View? = null

    /**
     * 定义viewpager
     */
    private var containerViewPager: ViewPager? = null

    /**
     * 可以滚动超出边界的scroller
     */
    private var overScroller = OverScroller(mContext)

    /**
     * 当吸顶view吸顶后是否支持下拉
     * 默认支持
     */
    private var canPullDown = true

    /**
     * 纵向滑动变量增量
     */
    private var deltaY = 0

    init {
        /**
         * 设置布局为纵向布局
         */
        orientation = VERTICAL
    }

    /**
     * 视图加载成功后进行相应view的实例化
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        containerTopView = findViewById(R.id.stickyNavigationLayoutTopView)
        containerNavigationView = findViewById(R.id.stickyNavigationLayoutIndicator)
        val targetViewPager: View = findViewById(R.id.stickyNavigationLayoutViewPager)
        if (targetViewPager !is ViewPager) {
            throw Exception("stickyNavigationLayoutViewPager should used by ViewPager!")
        }
        containerViewPager = targetViewPager
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
        val params = containerViewPager?.layoutParams
        /**
         * 需要当前控件的高度减去吸顶view的高度
         */
        params?.height = measuredHeight - (containerNavigationView?.measuredHeight ?: 0)
        setMeasuredDimension(
                measuredWidth,
                (containerTopView?.measuredHeight ?: 0) + (containerNavigationView?.measuredHeight
                        ?: 0) + (containerViewPager?.measuredHeight ?: 0)
        )
        /**
         * fix bug if version>7
         */
        topViewHeight = containerTopView?.measuredHeight ?: 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        /**
         * 需要更新顶部view高度
         */
        topViewHeight = containerTopView?.measuredHeight ?: 0
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

        if (canPullDown) {
            if (toY > topViewHeight) {
                toY = topViewHeight
                deltaY = toY
            }
            if (toY != scrollY) {
                deltaY = toY
                super.scrollTo(x, toY)
            }
        } else {
            if (toY > topViewHeight) {
                toY = topViewHeight
                deltaY = toY
                super.scrollTo(x, toY)
            } else {
                toY = topViewHeight
                deltaY = toY
                super.scrollTo(x, toY)
            }
        }

    }

    /**
     * 计算滑动结果
     * 实时更新scroller
     */
    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            LogFactory.d(this.javaClass, "overScroller.currY:" + overScroller.currY)
            scrollTo(0, overScroller.currY)
            invalidate()
        }
//        postInvalidate()
    }

}