package com.ripple.ui.nestedrecyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Author: fanyafeng
 * Data: 2020/8/28 16:40
 * Email: fanyafeng@live.cn
 * Description:
 */
open class ParentRecyclerView @JvmOverloads constructor(
        val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(mContext, attrs, defStyleAttr) {

    private val flingHelper = FlingHelper(mContext)
    private var velocityY = 0
    private var lastY = 0F
    private var totalDy = 0
    private var isStartFling = false
    var canScrollVertically = AtomicBoolean(true)

    init {
        initScrollListener()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            val action = it.action
            if (action == MotionEvent.ACTION_DOWN) {
                velocityY = 0
                stopScroll()
            }
        }
        if (!(ev == null || ev.action == MotionEvent.ACTION_MOVE)) {
            //在非ACTION_MOVE的情况下，将lastY置为0
            lastY = 0f
        }
        return try {
            super.dispatchTouchEvent(ev)
        } catch (e: Exception) {
            false
        }
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (lastY == 0f) {
            lastY = e.y
        }
        if (isScrollEnd()) {
            //如果父RecyclerView已经滑动到底部，需要让子RecyclerView滑动剩余的距离
            val childRecyclerView = findNestedScrollingChildRecyclerView()
            if (childRecyclerView != null) {
                val deltaY = (lastY - e.y).toInt()
                canScrollVertically.set(false)
                childRecyclerView.scrollBy(0, deltaY)
            }
        }
        if (e.action == MotionEvent.ACTION_UP) {
            canScrollVertically.set(true)
        }
        lastY = e.y
        return try {
            super.onTouchEvent(e)
        } catch (e: Exception) {
            false
        }
    }

    override fun fling(velX: Int, velY: Int): Boolean {
        val fling = super.fling(velX, velY)
        if (!fling || velY <= 0) {
            velocityY = 0
        } else {
            isStartFling = true
            velocityY = velY
        }
        return fling
    }

    override fun scrollToPosition(position: Int) {
        val childRecyclerView = findNestedScrollingChildRecyclerView()
        childRecyclerView?.scrollToPosition(position)
        postDelayed({ super@ParentRecyclerView.scrollToPosition(position) }, 50)
    }

    override fun smoothScrollToPosition(position: Int) {
//        super.smoothScrollToPosition(position)
        val childRecyclerView = findNestedScrollingChildRecyclerView()
        childRecyclerView?.smoothScrollToPosition(position)
        postDelayed({ super@ParentRecyclerView.smoothScrollToPosition(position) }, 50)
    }

    override fun onNestedPreFling(target: View?, velocityX: Float, velocityY: Float): Boolean {
        val childRecyclerView = findNestedScrollingChildRecyclerView()
        val isParentCanFling = velocityY > 0f && !isScrollEnd()
        val isChildCanNotFling = !(velocityY >= 0 || childRecyclerView == null || !childRecyclerView.isScrollTop())

        if (!isParentCanFling && !isChildCanNotFling) {
            return false
        }
        fling(0, velocityY.toInt())
        return true
    }

    override fun onNestedFling(target: View?, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return true
    }

    override fun onStartNestedScroll(child: View?, target: View?, nestedScrollAxes: Int): Boolean {
        return target != null && target is ChildRecyclerView
    }

    override fun onNestedPreScroll(target: View?, dx: Int, dy: Int, consumed: IntArray?) {
        val childRecyclerView = findNestedScrollingChildRecyclerView()
        val isParentCanScroll = dy > 0 && !isScrollEnd()
        val isChildCanNotScroll = !(dy >= 0 || childRecyclerView == null || !childRecyclerView.isScrollTop())
        if (isParentCanScroll || isChildCanNotScroll) {
            scrollBy(0, dy)
            consumed!![1] = dy
        }
    }

    private fun dispatchChildFling() {
        if (isScrollEnd() && velocityY != 0) {
            val splineFlingDistance: Double = flingHelper.getSplineFlingDistance(velocityY)
            if (splineFlingDistance > totalDy) {
                childFling(flingHelper.getVelocityByDistance(splineFlingDistance - totalDy))
            }
        }
        totalDy = 0
        velocityY = 0
    }

    private fun childFling(velocityByDistance: Int) {
        val childRecyclerView = findNestedScrollingChildRecyclerView()
        childRecyclerView?.fling(0, velocityByDistance)
    }

    fun isScrollEnd(): Boolean {
        return !canScrollVertically(1)
    }

    private fun initScrollListener() {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    dispatchChildFling()
                }
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

    fun initLayoutManager() {
        val layoutManager: LinearLayoutManager = object : LinearLayoutManager(mContext) {
            override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: State): Int {
                return try {
                    super.scrollVerticallyBy(dy, recycler, state)
                } catch (e: Exception) {
                    e.printStackTrace()
                    0
                }
            }

            override fun onLayoutChildren(recycler: Recycler, state: State) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun canScrollVertically(): Boolean {
                val childRecyclerView: ChildRecyclerView? = findNestedScrollingChildRecyclerView()
                return canScrollVertically.get() || childRecyclerView == null || childRecyclerView.isScrollTop()
//                return super.canScrollVertically()
            }

            override fun addDisappearingView(child: View) {
                try {
                    super.addDisappearingView(child)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun supportsPredictiveItemAnimations(): Boolean {
                return false
            }
        }

        layoutManager.orientation = VERTICAL
        setLayoutManager(layoutManager)
    }

    fun findNestedScrollingChildRecyclerView(): ChildRecyclerView? {
        return if (adapter != null && adapter is ParentAdapter) {
            (adapter!! as ParentAdapter).getCurrentChildRecyclerView()
        } else null
    }


}