package com.ripple.ui.flowview.impl

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.ripple.tool.kttypelians.OnItemModelClickListener
import com.ripple.tool.kttypelians.PentaLambda
import com.ripple.tool.kttypelians.QuadraLambda
import com.ripple.tool.kttypelians.TripleLambda
import com.ripple.ui.flowview.IChooseFlowView
import com.ripple.ui.flowview.IChooseModel
import java.util.*


/**
 * Author: fanyafeng
 * Data: 2020/6/28 19:46
 * Email: fanyafeng@live.cn
 * Description: taglistview具体实现类
 *
 * 1.包含可点击，不可点击，所有点击的回调
 * 2.获取用户点击回调
 * 3.设置最大选取数量
 *  选取的替换逻辑为当数量达到最大值时会取消掉第一个，内部维护的为一个有序数组
 *  每次都加到最后，如果达到阈值则取消第一个否则继续添加选中项
 *
 */
class ChooseFlowView @JvmOverloads constructor(
    val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FlowView(mContext, attrs, defStyleAttr), IChooseFlowView {

    /**
     * 可选项的点击回调
     */
    var onItemAbleClickListener: OnItemModelClickListener<IChooseModel> = null

    /**
     * 不可选项的点击回调
     */
    var onItemUnableClickListener: OnItemModelClickListener<IChooseModel> = null

    /**
     * 所有的点击回调
     * 会有一个标记是否可点击的字段
     */
    var onItemClickListener: PentaLambda<View, Int, IChooseModel, Boolean, Boolean> = null

    private var position = -1

    private var maxCount = 1

    private var minCount = 0

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
        resultList.clear()
//        println("已选中：" + selectList.toString())
        allModelList.forEachIndexed { index, iChooseModel ->
            if (selectList.contains(index)) {
                resultList.add(iChooseModel)
//                println("标志位：" + index)
//                println("标志位title:" + iChooseModel.getChooseItemTitle())
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
    @JvmOverloads
    fun <T : ChooseItemView, M : IChooseModel> addItemView(
        itemView: T,
        model: M,
        params: LayoutParams? = null
    ) {
        position++
        allModelList.add(model)
        itemView.initData(model)
        itemView.tag = position

        val initCount = selectList.size

        if (model.getChooseItemChecked()) {
            if (initCount >= maxCount) {
                val first = selectList.first
                (getChildAt(first) as ChooseItemView).toggle()
                selectList.removeFirst()
                selectList.addLast(position)
            } else {
                selectList.addLast(position)
            }
        }

        itemView.setOnClickListener {
            val pos = it.tag as Int

            val isCheckable: Boolean

            /**
             * 小于最小数量想取消选中但是不可以
             * 标记为重复选取，不相应操作
             */
            var checkRepeat = true

            if (itemView.isCheckable()) {
                isCheckable = true
                val mCount = selectList.size
                if (itemView.isChecked()) {
                    //取消选中
                    if (mCount <= minCount) {
                        //当用户选取数量小于最小数量时不允许取消选中
                        checkRepeat = false
                    } else {
                        selectList.remove(pos)
                        itemView.toggle()
                    }
                } else {
                    if (mCount >= maxCount) {
                        //取消第一个加入最后一个
                        val first = selectList.first
                        (getChildAt(first) as ChooseItemView).toggle()
                        itemView.toggle()
                        selectList.removeFirst()
                        selectList.addLast(pos)
                    } else {
                        //添加选中
                        itemView.toggle()
                        selectList.addLast(pos)
                    }
                }
                onItemAbleClickListener?.invoke(it, pos, model)
            } else {
                isCheckable = false
                onItemUnableClickListener?.invoke(it, pos, model)
            }

            onItemClickListener?.invoke(it, pos, model, isCheckable, checkRepeat)


        }
        if (params != null) {
            addView(itemView, params)
        } else {
            addView(itemView)
        }
    }

    override fun setMaxChooseCount(maxCount: Int) {
        this.maxCount = maxCount
    }

    override fun getMaxChooseCount(): Int {
        return maxCount
    }

    override fun getMinChooseCount(): Int {
        return minCount
    }

    override fun setMinChooseCount(minCount: Int) {
        this.minCount = minCount
    }


}