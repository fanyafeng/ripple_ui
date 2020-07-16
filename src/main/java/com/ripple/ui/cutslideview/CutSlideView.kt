package com.ripple.ui.cutslideview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Scroller
import com.ripple.tool.density.dp2px

/**
 * Author： fanyafeng
 * Date： 2019/3/29 4:36 PM
 * Email: fanyafeng@live.cn
 *
 * 左右滑动->
 * 当用户手指按下时并且手指不离开屏幕，此时需要锁定滑动方向，
 * 不让其进行上下滑动，当用户手指离开屏幕时需要把滑动锁定解开
 *
 * 上下滑动->
 * 同上
 */
class CutSlideView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RelativeLayout(mContext, attrs, defStyleAttr) {

    private val TAG = CutSlideView::class.java.simpleName

    var defaultMaxOffsetX: Float = dp2px(mContext, 180F)
    var defaultMinOffsetX: Float = dp2px(mContext, 46F)

    private var lastPosX = 0F
    private var lastPosY = 0F
    private var subOffsetX = defaultMinOffsetX

    var edgeThreshold = 80
    var slidable = true

    /**
     * 用户是否可以滑动屏幕
     */
    var canSlide = true
        set(value) {
            field = value
            if (!field) {
                scrollTo(-defaultMinOffsetX.toInt() + 45.dp2px, 0)
            }
        }

    private var isSlideOpen = false

    private var isTransmit = false

    private var isSideslip = false
    private var isLockDirection = false

    //是否是横向滑动
    private var isLandscape = false

    //是否是纵向滑动
    private var isPortrait = false

    private val scroller = Scroller(context)

    var onSlidingListener: ((distanceGapScale: Float) -> Unit)? = null

    private val onSlidingInnerListener: ((distanceGapScale: Float) -> Unit) = {
        onSlidingListener?.invoke(it)
    }

    var onCloseListener: (() -> Unit)? = null

    private val onCloseClickListenerInner: (() -> Unit) = {
        onCloseListener?.invoke()
        closeListener?.onClose()
    }

    private var closeListener: CloseListener? = null

    fun setOnCloseListener(closeListener: CloseListener) {
        this.closeListener = closeListener
    }

    interface CloseListener {
        fun onClose()
    }

    var onOpenListener: (() -> Unit)? = null

    private val onOpenClickListenerInner: (() -> Unit) = {
        onOpenListener?.invoke()
        openListener?.onOpen()
    }

    private var openListener: OpenListener? = null

    fun setOnOpenListener(openListener: OpenListener) {
        this.openListener = openListener
    }

    interface OpenListener {
        fun onOpen()
    }

    init {
        isClickable = true
    }


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val interceptX: Float = ev?.x ?: 0f
        val interceptY: Float = ev?.y ?: 0f

        lastPosX = interceptX
        lastPosY = interceptY
        return super.onInterceptTouchEvent(ev)
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        val result = super.drawChild(canvas, child, drawingTime)
        return result
    }

    private var offsetX = 0
    private var offsetY = 0

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (!canSlide) {
            return super.dispatchTouchEvent(event)
        }
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> run {
                Log.d(TAG, "dispatchTouchEvent ACTION_DOWN")

                downX = event.rawX.toInt()
                downY = event.rawY.toInt()
//                Log.d(TAG, "dispatchTouchEvent downX:" + downX)
//                Log.d(TAG, "dispatchTouchEvent downY:" + downY)
            }
            MotionEvent.ACTION_MOVE -> run {
                Log.d(TAG, "dispatchTouchEvent ACTION_MOVE")

                offsetX = (event.rawX - downX).toInt()
                offsetY = (event.rawY - downY).toInt()

                if (Math.abs(offsetX) > 50 || Math.abs(offsetY) > 50) {
                    Log.d(TAG, "初次滑动")
                    offsetX = 0
                    offsetY = 0
                }

                downX = event.rawX.toInt()
                downY = event.rawY.toInt()

                Log.d(TAG, "X偏移量 offsetX:" + offsetX)
                Log.d(TAG, "Y偏移量 offsetY:" + offsetY)

                if (!isLockDirection) {
                    isLockDirection = true
                    if (Math.abs(offsetX) < Math.abs(offsetY)) {
                        /**纵向滑动 抽屉开关，X轴移动距离比Y轴移动距离要大**/
                        Log.d(TAG, "!isSlideOpen dispatchTouchEvent offsetX < offsetY")
                        Log.d(TAG, "滑动方向：纵向滑动")
                        isTransmit = true
                        isPortrait = true
                    } else if (Math.abs(offsetX) > Math.abs(offsetY)) {
                        //横向滑动
                        Log.d(TAG, "滑动方向：横向滑动")
                        isTransmit = false
                        isLandscape = true
                    } else {
                        isLockDirection = false
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> run {
                Log.d(TAG, "dispatchTouchEvent ACTION_UP_CANCEL")
                if (!isLockDirection && offsetX == 0 && offsetY == 0) {
                    event.action = MotionEvent.ACTION_UP
                } else {
//                    event.action = MotionEvent.ACTION_CANCEL
                    if (!isPortrait) {
                        event.action = MotionEvent.ACTION_CANCEL
                    }
//                    else{
//                        event.action = MotionEvent.ACTION_CANCEL
//                    }
                }
                offsetX = 0
                offsetY = 0
                isPortrait = false
                isLandscape = false
                onTouchEvent(event)
                isLockDirection = false
            }
        }

        if (!isTransmit) {
            onTouchEvent(event)
        }


        /**isLandscape，isPortrait同时为false则是用户刚按下手指，此时不能确定用户滑动方向
         * 只有当其中一个值改变才可以确定用户手的滑动方向
         * 此时就可以进行相应的事件分发
         */

        if (!isLandscape && !isPortrait) {
            return super.dispatchTouchEvent(event)
        }

        if (isLandscape) {
            return true
        }


        return super.dispatchTouchEvent(event)

    }

    private fun findTargetView(view: View): View? {
        if (view is ViewGroup) {
            if (view is CutRecyclerView) {
                return view
            }

            val viewGroup = view
            val childCount = viewGroup.childCount
            (0 until childCount).forEachIndexed { index, i ->
                val target = findTargetView(viewGroup.getChildAt(i))
                if (target != null) {
                    return target
                }
            }
        }
        return null
    }

    private var downX = 0
    private var downY = 0

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!canSlide) {
            return super.onTouchEvent(event)
        }

        var offset: Int
//        var offsetY: Int

        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> run {
                Log.d(TAG, "onTouchEvent ACTION_DOWN")

                onStart()

                downX = event.x.toInt()
                downY = event.y.toInt()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> run {
                Log.d(TAG, "onTouchEvent ACTION_UP_CANCEL")

                if (!isSlideOpen) {
                    if (subOffsetX > defaultMaxOffsetX / 2) {
                        openSlideBoard()
                    } else {
                        closeSlideBoard()
                    }
                } else {
                    if (subOffsetX < (defaultMaxOffsetX / 2)) {
                        closeSlideBoard()
                    } else {
                        openSlideBoard()
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> run {
                Log.d(TAG, "onTouchEvent ACTION_MOVE")

                offset = (event.x - lastPosX).toInt()
//                offsetY = (event.y - lastPosY).toInt()

//                Log.d(TAG, "onTouchEvent offset:" + offset)
//                Log.d(TAG, "onTouchEvent offsetY:" + offsetY)

                subOffsetX = Math.max(0f, subOffsetX + offset)
                subOffsetX = Math.min(subOffsetX, defaultMaxOffsetX)
//                Log.d(TAG, "subOffsetX:" + subOffsetX)

                when {
                    subOffsetX >= defaultMaxOffsetX -> {
                        offset = (defaultMaxOffsetX + scrollX).toInt()
                        onSlideOpen()
                    }
                    subOffsetX <= 0 -> {
                        offset = scrollX
                        onSlideClose()
                    }
                    else -> {
                        onSliding()
                    }
                }

                Log.d(TAG, "offset值:" + offset)
                Log.d(TAG, "offset值: scrollX" + scrollX)
                Log.d(TAG, "offset值: defaultMaxOffsetX" + defaultMaxOffsetX)
                Log.d(TAG, "offset值: defaultMinOffsetX" + defaultMinOffsetX)

                if (subOffsetX >= defaultMinOffsetX) {

                    scrollBy(-offset, 0)
                }
                lastPosX = event.x
            }
        }

        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val slideWidth = defaultMaxOffsetX.toInt()
//        Log.d(TAG, "slideWidth:" + slideWidth)
        val slideWidthMeasureSpec =
            MeasureSpec.makeMeasureSpec(slideWidth, MeasureSpec.getMode(widthMeasureSpec))

        try {
            getChildAt(0).measure(slideWidthMeasureSpec, heightMeasureSpec)
        } catch (e: Exception) {
        }

        for (i in 1 until childCount) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec)
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        try {
            getChildAt(0).layout(-defaultMaxOffsetX.toInt(), 0, 0, b)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        for (i in 1 until childCount) {
            getChildAt(i).layout(0, 0, r, b)
        }
    }

    /**
     * 打开侧滑抽屉
     */
    fun openSlideBoard() {

        if (!isSlideOpen) {
            onOpenClickListenerInner()
        }
        isSlideOpen = true
        if (!slidable) {
            return
        }
        onStart()
        subOffsetX = defaultMaxOffsetX
        scrollToDestination()
    }

    /**
     * 关闭抽屉
     */
    fun closeSlideBoard() {
        if (isSlideOpen) {
            onCloseClickListenerInner()
        }
        isSlideOpen = false
        if (!slidable) {
            return
        }
        onStart()
        subOffsetX = defaultMinOffsetX
        scrollToDestination()
    }

    private fun scrollToDestination() {
        val dx = (-subOffsetX - scrollX).toInt()
        scroller.startScroll(scrollX, 0, dx, 0)
        invalidate()
    }

    private fun onStart() {

    }

    private fun onSlideOpen() {
        if (!isSlideOpen) {
            isSlideOpen = true
            isTransmit = true
            onOpenClickListenerInner()
        }
    }

    private fun onSlideClose() {
        if (isSlideOpen) {
            isSlideOpen = false
            isTransmit = false
            onCloseClickListenerInner()
        }
    }

    /**
     * 滑动监听
     */
    private fun onSliding() {
        /**打开抽屉    1
         * 关闭抽屉    0
         * 闭区间      [0，1]*/
        Log.d(TAG, "横向滑动scrollX：" + Math.abs(scrollX))
        Log.d(TAG, "横向滑动defaultMaxOffsetX：" + defaultMaxOffsetX)
        Log.d(TAG, "横向滑动defaultMinOffsetX：" + defaultMinOffsetX)

        val distanceGapScale =
            (Math.abs(scrollX) - defaultMinOffsetX.toInt()).toFloat() / (defaultMaxOffsetX.toInt() - defaultMinOffsetX.toInt()).toFloat()
        Log.d(TAG, "横向滑动比例distanceGapScale：" + distanceGapScale)
        onSlidingInnerListener(distanceGapScale)
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            when {
                scroller.currX <= -defaultMaxOffsetX -> onSlideOpen()
                scroller.currX >= 0 -> onSlideClose()
                else -> onSliding()
            }
            invalidate()
        }
    }

    private fun px2dp(context: Context, px: Float): Float {
        val scale = context.resources.displayMetrics.density
        return px / scale + 0.5F
    }

    private fun dp2px(context: Context, dp: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dp * scale + 0.5F
    }
}