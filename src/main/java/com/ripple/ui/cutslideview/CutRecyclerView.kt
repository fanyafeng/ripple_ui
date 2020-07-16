package com.ripple.ui.cutslideview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * Author： fanyafeng
 * Date： 2019/4/11 10:16 AM
 * Email: fanyafeng@live.cn
 */
class CutRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val TAG = CutRecyclerView::class.java.simpleName

    var isLockSlideDirection = false

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        Log.d(TAG, "isLockSlideDirection:" + isLockSlideDirection)
        if (isLockSlideDirection) {
            return true
        }

        when (ev?.actionMasked) {
            MotionEvent.ACTION_MOVE -> run {
                Log.d(TAG, "dispatchTouchEvent ACTION_MOVE")
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}