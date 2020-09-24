package com.ripple.ui.nestedrecyclerview

import android.content.Context
import android.hardware.SensorManager
import android.view.ViewConfiguration
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln


/**
 * Author: fanyafeng
 * Data: 2020/8/28 16:43
 * Email: fanyafeng@live.cn
 * Description:
 *
 */
class FlingHelper(val mContext: Context) {

    companion object {
        private var mPhysicalCoeff: Float = 0F
        private const val INFLEXION = 0.3499999940395355f // Tension lines cross at (INFLEXION, 1)

        // Fling friction
        private val mFlingFriction = ViewConfiguration.getScrollFriction()

        private val DECELERATION_RATE = (ln(0.78) / ln(0.9)).toFloat()
    }

    init {
        val ppi: Float = mContext.resources.displayMetrics.density * 160.0f
        mPhysicalCoeff = (SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * ppi
                * 0.84f) // look and feel tuning
    }

    private fun getSplineDeceleration(velocity: Int): Double {
        return ln(INFLEXION * abs(velocity) / (mFlingFriction * mPhysicalCoeff).toDouble())
    }

    /**
     * 根据惯性值获取滑动距离
     */
    fun getSplineFlingDistance(velocity: Int): Double {
        val l: Double = getSplineDeceleration(velocity)
        val decelMinusOne: Double = DECELERATION_RATE - 1.0
        return mFlingFriction * mPhysicalCoeff * exp(DECELERATION_RATE / decelMinusOne * l)
    }

    /**
     * 根据滑动距离获取管性值
     * 与上方搭配使用
     */
    fun getVelocityByDistance(distance: Double): Int {
        val l: Double = getSplineDecelerationByDistance(distance)
        val velocity = (exp(l) * mFlingFriction * mPhysicalCoeff / INFLEXION).toInt()
        return abs(velocity)

    }

    private fun getSplineDecelerationByDistance(distance: Double): Double {
        val decelMinusOne = DECELERATION_RATE - 1.0
        return decelMinusOne * ln(distance / (mFlingFriction * mPhysicalCoeff)) / DECELERATION_RATE
    }

}