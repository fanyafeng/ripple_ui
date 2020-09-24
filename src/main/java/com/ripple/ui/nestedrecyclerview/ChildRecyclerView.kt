package com.ripple.ui.nestedrecyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.NestedScrollingChildHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs


/**
 * Author: fanyafeng
 * Data: 2020/8/28 16:41
 * Email: fanyafeng@live.cn
 * Description:
 *
 */
open class ChildRecyclerView @JvmOverloads constructor(open var mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(mContext, attrs, defStyleAttr) {

    private val flingHelper = FlingHelper(mContext)

    private var totalDy = 0
    private var mVelocity = 0
    private var parentRecyclerView: ParentRecyclerView? = null
    private var isStartFling = false
    private var childHelper: NestedScrollingChildHelper? = null

    init {
        /**
         * [android.widget.OverScroller]
         */
        overScrollMode = View.OVER_SCROLL_NEVER
        initScrollListener()
    }

    private fun initScrollListener() {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == SCROLL_STATE_IDLE) {
                    dispatchParentFling()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isStartFling) {
                    totalDy = 0
                    isStartFling = false
                }
                totalDy += dy
            }
        })
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        val isScrollUp = !canScrollVertically(-1)
        if (isScrollUp) {
            parent.requestDisallowInterceptTouchEvent(false)
        } else {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        e?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> run {
                    oldX = it.x
                    oldY = it.y
                }
                MotionEvent.ACTION_MOVE -> run {
                    newX = it.x
                    newY = it.y
                    if (!isScrollUp) {
                        if (abs(newX - oldX) > abs(newY - oldY)) {
                            parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    private var oldX = 0F
    private var oldY = 0F

    private var newX = 0F
    private var newY = 0F

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                mVelocity = 0
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        if (!isAttachedToWindow) {
            return false
        }
        val fling = super.fling(velocityX, velocityY)
        if (!fling || velocityY >= 0) {
            mVelocity = 0
        } else {
            isStartFling = true
            mVelocity = velocityY
        }
        return fling
    }

    private fun dispatchParentFling() {
        parentRecyclerView = findParentRecyclerView()
        if (isScrollTop() && mVelocity != 0) {
            val flingDistance = flingHelper.getSplineFlingDistance(mVelocity)
            parentRecyclerView?.fling(0, -flingHelper.getVelocityByDistance(flingDistance + totalDy))
        }
        totalDy = 0
        mVelocity = 0
    }

    fun isScrollTop() = !canScrollVertically(-1)

    private fun findParentRecyclerView(): ParentRecyclerView? {
        var parentView = parent
        while (parentView !is ParentRecyclerView) {
            parentView = parentView.parent
        }
        return parentView
    }

    private fun getChildHelper(): NestedScrollingChildHelper {
        if (childHelper == null) {
            childHelper = NestedScrollingChildHelper(this)
        }
        return childHelper!!
    }
}