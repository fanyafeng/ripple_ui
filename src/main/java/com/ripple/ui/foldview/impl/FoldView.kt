package com.ripple.ui.foldview.impl

import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.ripple.tool.density.dp2px
import com.ripple.ui.foldview.IFoldView


/**
 * Author: fanyafeng
 * Data: 2020/7/2 11:14
 * Email: fanyafeng@live.cn
 * Description:
 */
class FoldView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RelativeLayout(mContext, attrs, defStyleAttr), IFoldView {

    private val descTextView: TextView = TextView(mContext)
    private val foldControlTextView: TextView = TextView(mContext)

    /**
     * 默认进来是关闭状态
     */
    private var descClose = true

    /**
     * 最大显示行数，默认两行
     */
    private var maxLines = 2

    /**
     * 只能展开不能关闭
     * 默认为false
     */
    private var onlyUnfold = false

    /**
     * 需要显示的文案
     */
    private var foldViewDesc: CharSequence = ""

    /**
     * 关闭时显示的文案
     */
    private var foldText: CharSequence = "展开"

    /**
     * 打开时显示的文案
     */
    private var unFoldText: CharSequence = Html.fromHtml("<font color=\'#222222\' >关闭<font>")

    /**
     * 实际行数
     */
    private var lineCount = 0


    init {

        descTextView.text = foldViewDesc
        descTextView.ellipsize = TextUtils.TruncateAt.END
        descTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13.toFloat())
        descTextView.setBackgroundColor(Color.TRANSPARENT)
        descTextView.setTextColor(Color.parseColor("#999999"))
        descTextView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        addView(descTextView)

        val foldBuilder = SpannableStringBuilder()
        foldBuilder.append(Html.fromHtml("<font color=\'#999999\' >．．．<font>"))
        foldBuilder.append(Html.fromHtml("<font color=\'#222222\' >　展开<font>"))
        foldText = foldBuilder

        foldControlTextView.text = foldText
        foldControlTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13.toFloat())
        foldControlTextView.gravity = Gravity.END or Gravity.BOTTOM
        val foldLayoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        foldControlTextView.background = this.background
        foldLayoutParams.addRule(ALIGN_PARENT_RIGHT)
        foldLayoutParams.addRule(ALIGN_PARENT_BOTTOM)
        addView(foldControlTextView, foldLayoutParams)


        this.setOnClickListener {
            /**
             * 如果可以关闭的话，
             * 打开之后再点击是关闭，关闭之后再点击是打开
             */
            if (onlyUnfold) {
                if (descClose) {
                    descClose = !descClose
                    post {
                        descTextView.maxLines = lineCount
                        foldControlTextView.visibility = View.GONE
//                        val params = descTextView.layoutParams
//                        params.height = lineCount * descTextView.lineHeight
//                        descTextView.layoutParams = params
                    }
                }
            } else {
                if (descClose) {
                    descTextView.maxLines = lineCount
                    foldControlTextView.text = unFoldText
//                    val params = descTextView.layoutParams
//                    params.height =
//                        (lineCount * (descTextView.lineHeight + descTextView.lineSpacingMultiplier * (3.dp2px + descTextView.lineSpacingExtra))).toInt()
//                    descTextView.layoutParams = params
                } else {
                    descTextView.maxLines = maxLines
                    foldControlTextView.text = foldText
//                    val params = descTextView.layoutParams
//                    params.height =
//                        (maxLines * (descTextView.lineHeight + descTextView.lineSpacingMultiplier * (3.dp2px + descTextView.lineSpacingExtra))).toInt()
//                    descTextView.layoutParams = params
                }
                descClose = !descClose
            }
        }
    }

    override fun getFoldViewStatus(): Boolean {
        return descClose
    }

    override fun setFoldViewStatus(close: Boolean) {
        this.descClose = close
    }

    override fun setFoldViewMaxLine(maxLine: Int) {
        this.maxLines = maxLine

    }

    override fun setFoldViewOnlyUnfold(unfold: Boolean) {
        this.onlyUnfold = unfold
    }

    override fun setFoldViewDesc(desc: CharSequence) {
        this.foldViewDesc = desc
        descTextView.text = foldViewDesc
        post {
            lineCount = descTextView.lineCount
//            descTextView.maxLines = maxLines
        }
        setStatus()
    }

    override fun setUnfoldText(unFoldText: CharSequence) {
        this.unFoldText = unFoldText
    }

    override fun setFoldText(foldText: CharSequence) {
        this.foldText = foldText
    }

    private fun setStatus() {
        postDelayed({
            if (descTextView.lineCount > maxLines) {
                descTextView.maxLines = maxLines
                if (onlyUnfold) {
                    foldControlTextView.visibility = View.GONE
                } else {
                    foldControlTextView.visibility = View.VISIBLE
                }
            } else {
                foldControlTextView.visibility = View.GONE
            }
        }, 10)
    }

    fun getFoldControlTextView() = foldControlTextView

    fun getDescTextView() = descTextView

    fun setFoldControlTextViewBackGroundColor(color: Int) {
        foldControlTextView.setBackgroundColor(color)
    }

    fun setDescTextViewBackGroundColor(color: Int) {
        descTextView.setBackgroundColor(color)
    }

}