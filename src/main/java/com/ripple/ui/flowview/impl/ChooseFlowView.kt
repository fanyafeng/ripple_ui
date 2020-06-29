package com.ripple.ui.flowview.impl

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ripple.tool.kttypelians.OnItemClickListener
import com.ripple.tool.kttypelians.OnItemModelClickListener
import com.ripple.tool.kttypelians.SuccessLambda
import com.ripple.tool.kttypelians.TripleLambda
import com.ripple.ui.flowview.IChooseFlowView
import com.ripple.ui.flowview.IChooseItemView
import com.ripple.ui.flowview.IChooseModel
import java.util.*


/**
 * Author: fanyafeng
 * Data: 2020/6/28 19:46
 * Email: fanyafeng@live.cn
 * Description:
 */
class ChooseFlowView @JvmOverloads constructor(
    val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FlowView(mContext, attrs, defStyleAttr), IChooseFlowView {

    var onItemClickListener: OnItemModelClickListener<IChooseModel> = null

    var onItemUnableClickListener: OnItemModelClickListener<IChooseModel> = null

    private var position = -1

    private var maxCount = 1

    private var selectList = LinkedList<Int>()

    private var allModelList = LinkedList<IChooseModel>()

    private var resultList = arrayListOf<IChooseModel>()

    init {
        resetView()
    }

    /**
     * 重置view
     */
    fun resetView() {
        removeAllViews()
        position = -1
        selectList.clear()
        allModelList.clear()
        resultList.clear()
    }

    /**
     * 获取选中的结果
     */
    fun getSelectedResult(): List<IChooseModel> {
        allModelList.forEachIndexed { index, iChooseModel ->
            if (selectList.contains(index)) {
                resultList.add(iChooseModel)
//                println("标志位：" + index)
//                println(iChooseModel.getChooseItemTitle())
            }
        }
        return resultList
    }

    /**
     * 更新当前的view
     * 为了不去每次都添加删除单个的view
     * 进行原有view的重用
     */
    fun <T : ChooseItemView> updateView(list: List<Pair<IChooseModel, T>>) {
        selectList.clear()
        val newCount = list.size
        val oldCount = allModelList.size
        if (newCount == oldCount) {
            list.forEachIndexed { index, model ->
                //更新原有的model列表
                allModelList[index] = model.first
                //更新原有的view显示
                (getChildAt(index) as ChooseItemView).initData(model.first)
            }
        } else if (newCount > oldCount) {
            list.forEachIndexed { index, model ->
                //新数据与原数据重叠部分
                if (index < oldCount) {
                    allModelList[index] = model.first
                    (getChildAt(index) as ChooseItemView).initData(model.first)
                } else {
                    addItemView(model.second, model.first)
                }
            }
        } else {
            list.forEachIndexed { index, model ->
                //更新原有的model列表
                allModelList[index] = model.first
                //更新原有的view显示
                (getChildAt(index) as ChooseItemView).initData(model.first)
            }

            (newCount until oldCount).forEach {
                allModelList.removeAt(it)
                removeViewAt(it)
            }
        }

    }


    /**
     * 填充数据
     * 一般都是动态填充
     */
    fun <T : ChooseItemView, M : IChooseModel> addItemView(itemView: T, model: M) {
        position++
        allModelList.add(model)
        itemView.initData(model)
        itemView.tag = position
        itemView.setOnClickListener {
            val pos = it.tag as Int

            if (itemView.isCheckable()) {
                itemView.toggle()
                val mCount = selectList.size

                if (mCount >= maxCount) {
                    val first = selectList.first
                    (getChildAt(first) as ChooseItemView).toggle()
                    selectList.removeFirst()
                    selectList.addLast(pos)
                } else {
                    selectList.addLast(pos)
                }
                onItemClickListener?.invoke(it, pos, model)
            } else {
                onItemUnableClickListener?.invoke(it, pos, model)
            }



        }
        addView(itemView)
    }

    override fun setMaxChooseCount(maxCount: Int) {
        this.maxCount = maxCount
    }

    override fun getMaxChooseCount(): Int {
        return maxCount
    }

}