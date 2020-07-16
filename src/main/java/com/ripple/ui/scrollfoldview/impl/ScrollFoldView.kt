package com.ripple.ui.scrollfoldview.impl

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout


/**
 * Author: fanyafeng
 * Data: 2020/7/8 18:48
 * Email: fanyafeng@live.cn
 * Description:
 */
class ScrollFoldView @JvmOverloads constructor(
    private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(mContext, attrs, defStyleAttr) {

    init {
        /**
         * 设置纵向布局
         */
        orientation = VERTICAL

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> run {
//                Log.d(TAG, "onTouchEvent ACTION_DOWN")
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> run {
//                Log.d(TAG, "onTouchEvent ACTION_UP_CANCEL")
            }

            MotionEvent.ACTION_MOVE -> run {
//                Log.d(TAG, "onTouchEvent ACTION_MOVE")
            }
        }


        return super.onTouchEvent(event)
    }

}