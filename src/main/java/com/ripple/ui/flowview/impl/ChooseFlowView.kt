package com.ripple.ui.flowview.impl

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import com.ripple.tool.kttypelians.OnItemModelClickListener
import com.ripple.tool.kttypelians.PentaLambda
import com.ripple.tool.kttypelians.QuadraLambda
import com.ripple.tool.kttypelians.TripleLambda
import com.ripple.ui.flowview.IChooseFlowView
import com.ripple.ui.flowview.IChooseItemView
import com.ripple.ui.flowview.IChooseModel
import java.lang.Exception
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

    fun getAllDataList() = allModelList

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
            /**
             * 如果新旧数据个数相同
             */
            list.forEachIndexed { index, model ->
                val chooseModel = model.first
                //更新原有的model列表
                allModelList[index] = chooseModel
                updateSelectList(index, chooseModel)
                //获取原有的itemView
                val oldItemView = getChildAt(index) as ChooseItemView
                //获取新的itemView
                val newItemView = model.second
                //更新tag，方法内进行封装
                if (newItemView != null) {
                    oldItemView.updateStatus(newItemView)
                }
                //更新原有的view显示
                oldItemView.initData(chooseModel)
            }
        } else if (newCount > oldCount) {
            /**
             * 如果新数据比旧数据个数多
             */
            list.forEachIndexed { index, model ->
                val chooseModel = model.first
                //新数据与原数据重叠部分
                if (index < oldCount) {
                    allModelList[index] = chooseModel
                    updateSelectList(index, chooseModel)
                    //获取新的itemView
                    val newItemView = model.second
                    //获取原有的itemView
                    val oldItemView = getChildAt(index) as ChooseItemView
                    //更新tag，方法内进行封装
                    if (newItemView != null) {
                        oldItemView.updateStatus(newItemView)
                    }
                    //更新原有的view显示
                    oldItemView.initData(chooseModel)
                } else {
                    if (model.second != null) {
                        addItemView(model.second, chooseModel)
                    }
                }
            }
        } else {
            list.forEachIndexed { index, model ->
                val chooseModel = model.first
                //更新原有的model列表
                allModelList[index] = chooseModel
                updateSelectList(index, chooseModel)
                //获取原有的itemView
                val oldItemView = getChildAt(index) as ChooseItemView
                //获取新的itemView
                val newItemView = model.second
                //更新tag，方法内进行封装
                if (newItemView != null) {
                    oldItemView.updateStatus(newItemView)
                }
                //更新原有的view显示
                oldItemView.initData(chooseModel)
                //更新选中态
                setItemCheckStatus(index, true)
            }

            (newCount until oldCount).forEach {
                allModelList.removeAt(it)
                removeViewAt(it)
            }
        }
    }

    /**
     * 更新选中列表
     * 正常情况应该是外部控制，但是因为显示的问题内部进行了重新的筛选
     * 按理说控件不能修改用户的datamodel，可是如果用户传入的数据有问题的话需要用户自己去检查
     * 此时控件会更新数据的选中态
     * 但是本地选中的结果是正常的，算法是FIFO
     * 所以在此时获取的选择用户是完全可以信任的
     */
    @JvmOverloads
    private fun updateSelectList(
        selectPosition: Int,
        chooseModel: IChooseModel,
        isUpdateData: Boolean = true
    ) {
        val initCount = selectList.size
        //首先去判断是否是可选装态
        if (chooseModel.getChooseItemCheckable()) {
            setItemCheckableStatus(selectPosition, true)
            //然后去判断当前item是否是选中状态
            if (chooseModel.getChooseItemChecked()) {
                //如果是选中状态，并且被选中的数量大于最大的可选数量
                if (initCount >= maxCount) {
                    //首先更新被选中的第一个数据model
                    setItemCheckStatus(selectList.first, false)
                    //取消选中还需要更新控件状态
                    (getChildAt(selectList.first) as ChooseItemView).toggle()
                    //此时需要把第一个item删除
                    selectList.removeFirst()

                    //同时将选中状态的item添加到选中列表的最后位置
                    //以下同理
                    selectList.addLast(selectPosition)
                    //此时更新被选中态item
                    setItemCheckStatus(selectPosition, true)
                } else {
                    selectList.addLast(selectPosition)
                    setItemCheckStatus(selectPosition, true)
                }
            } else {
                //如果不是选中状态并且选中的最小数量还小于最小可选数量则不去更新当前列表
            }
        } else {
            //如果是不可选则需要更新当前item
            setItemCheckableStatus(selectPosition, false)
        }
        if (!isUpdateData) {
            val itemView = getChildAt(selectPosition)
            if (itemView != null) {
                (itemView as ChooseItemView).initData(chooseModel)
            }
        }
    }

    fun notifyItem(selectPosition: Int, chooseModel: IChooseModel) {
        updateSelectList(selectPosition, chooseModel, false)
    }

    /**
     * 更新data model为不可选状态
     */
    private fun setItemCheckableStatus(position: Int, checkable: Boolean) {
        allModelList[position].setChooseItemCheckable(checkable)
    }


    /**
     * 更新data model的选中状态
     */
    private fun setItemCheckStatus(position: Int, isChecked: Boolean) {
        allModelList[position].setChooseItemChecked(isChecked)
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

        if (model.getChooseItemCheckable()) {
            itemView.setCheckable(true)
            if (model.getChooseItemChecked()) {
                if (initCount >= maxCount) {
                    val first = selectList.first
                    (getChildAt(first) as ChooseItemView).toggle()
                    setItemCheckStatus(selectList.first, false)
                    selectList.removeFirst()
                    selectList.addLast(position)
                    setItemCheckStatus(position, true)
                } else {
                    selectList.addLast(position)
                }
            }
        } else {
            itemView.setCheckable(false)
            /**
             * 如果不可选但是是选中状态，这时候控件会去校验数据
             */
            if (model.getChooseItemChecked()) {
                model.setChooseItemChecked(false)
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
                        setItemCheckStatus(pos, false)
                        itemView.toggle()
                    }
                } else {
                    if (mCount >= maxCount) {
                        //取消第一个加入最后一个
                        val first = selectList.first
                        (getChildAt(first) as ChooseItemView).toggle()
                        itemView.toggle()
                        setItemCheckStatus(selectList.first, false)
                        selectList.removeFirst()
                        selectList.addLast(pos)
                        setItemCheckStatus(pos, true)
                    } else {
                        //添加选中
                        itemView.toggle()
                        selectList.addLast(pos)
                        setItemCheckStatus(pos, true)
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
        if (maxCount < minCount)
            throw Exception("最大选取数量不可以小于最小选取数量")
        this.maxCount = maxCount
    }

    override fun getMaxChooseCount(): Int {
        return maxCount
    }

    override fun getMinChooseCount(): Int {
        return minCount
    }

    override fun setMinChooseCount(minCount: Int) {
        if (maxCount < minCount)
            throw Exception("最大选取数量不可以小于最小选取数量")
        this.minCount = minCount
    }


}