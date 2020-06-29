package com.ripple.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Author: fanyafeng
 * Data: 2020/6/29 13:57
 * Email: fanyafeng@live.cn
 * Description: 统一的ImageView
 */

open class RippleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private val TAG = RippleImageView::class.java.simpleName
    }

    interface OnRippleImageViewListener {
        fun onDetach()

        fun onAttach()

        fun onDraw(canvas: Canvas?)

        fun verifyDrawable(who: Drawable): Boolean

        fun onTouchEvent(event: MotionEvent?): Boolean
    }


    /**
     * 兼容Fresco的SimpleDrawView
     * https://www.fresco-cn.org/docs/writing-custom-views.html
     */
    private var onRippleImageViewListener: OnRippleImageViewListener? = null

    fun setOnRippleImageViewListener(onRippleImageViewListener: OnRippleImageViewListener?) {
        this.onRippleImageViewListener = onRippleImageViewListener
    }

    var hintText: String? = null
        set(value) {
            field = value
            value?.let {
                if (it.isNotEmpty()) {
                    invalidate()
                }
            }
        }

    private var paint: TextPaint? = null
    private var currentLayout: StaticLayout? = null

    init {
        paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint?.isAntiAlias = true
        paint?.color = Color.WHITE
        paint?.textSize = 120F
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onRippleImageViewListener?.onDetach()
        drawable?.setVisible(false, false)
    }

    override fun onStartTemporaryDetach() {
        super.onStartTemporaryDetach()
        onRippleImageViewListener?.onDetach()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onRippleImageViewListener?.onAttach()
        drawable?.setVisible(visibility == View.VISIBLE, false)
    }

    override fun onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach()
        onRippleImageViewListener?.onAttach()
    }

    override fun verifyDrawable(dr: Drawable): Boolean {
        if (onRippleImageViewListener?.verifyDrawable(dr) == true) {
            return true
        }
        return super.verifyDrawable(dr)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        onRippleImageViewListener?.onDraw(canvas)


        hintText?.let { message ->
            currentLayout = StaticLayout(
                message,
                paint,
                width,
                Layout.Alignment.ALIGN_CENTER,
                1.2f,
                0.0f,
                false
            )
            currentLayout?.let {
                canvas?.drawColor(Color.parseColor("#66000000"))
                canvas?.translate(0F, (height - it.height).toFloat() / 2)
                it.draw(canvas)
                canvas?.translate(0F, -(height - it.height).toFloat() / 2)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return onRippleImageViewListener?.onTouchEvent(event) ?: false || super.onTouchEvent(event)
    }

}