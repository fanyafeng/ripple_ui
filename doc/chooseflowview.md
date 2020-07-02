# 标签列表选择view：ChooseFlowView
[移步到源代码github托管地址](https://github.com/fanyafeng/ripple_ui)

主要是针对不规则`ITEM TAG`标签的流式`LIST`布局，如果是规则的用`Recyclerview`就可以完全胜任了，而且还会有很好的内存管理，但是不规则的就需要自己来写了，因为文章可能比较长，这里先放一下效果图，在说之前有个大体的了解。

![标签列表view动图](https://github.com/1181631922/ModuleSample/blob/master/ripple_ui/pic/chooseflowview.gif)

## 一、引入使用
本来想再单独写一篇文章介绍使用的，但是接入比较简单，就直接说了
### 1.1 引入
根目录`build.gradle`
```
maven {
            url "https://dl.bintray.com/fanyafeng/ripple"
        }
```
项目`module`的`build.gradle`

```
implementation 'com.ripple.component:ui:0.0.6'
```
### 1.2 使用
#### 1.2.1 定义数据model
因为是演示，简单定义了数据model

```
data class ChooseModel(var title: String, var checkable: Boolean, var checked: Boolean) :
    IChooseModel {
    override fun getChooseItemTitle(): String {
        return title
    }

    override fun getChooseItemCheckable(): Boolean {
        return checkable
    }

    override fun getChooseItemChecked(): Boolean {
        return checked
    }

    override fun setChooseItemChecked(isChecked: Boolean) {
        checked = isChecked
    }

}
```
#### 1.2.2 activity中使用
下面就是填充数据的操作，类似给`adapter`设置数据
```
5.forEach {
            val model = ChooseModel("我是第$it", it != 3, it == 0)
            list.add(model)
            val itemView = ChooseItemView(this)
            itemView.setInnerTagWrapContent()
            itemView.chooseViewUnselected = R.drawable.choose_view_normal
            chooseItemView.addItemView(itemView, model)
        }
```
#### 1.2.3 获取回调结果

```
        chooseItemView.onItemClickListener={ first, position, third, fourth, fifth ->
            //不论按钮状态，只要点击就会有回调
            Log.d(TAG, "被点击：" + position)
        }

        chooseItemView.onItemAbleClickListener = { view, position, model ->
            Log.d(TAG, "被点击：" + position)
            showToast("被点击：" + position)

        }

        chooseItemView.onItemUnableClickListener = { view, position, model ->
            Log.d(TAG, "不能被点击的被点击了：" + position)
            showToast("不能被点击的被点击了：" + position)
        }
```


## 二、设计原理
老生常谈的一件事，写通用类的东西需要对修改关闭对扩展开放，再者就是使用者接入成本必须要小，最好带有默认实现，但是又需要支持用户对每一个细节的修改可以做到定制化。
### 2.1 需求简介
首先，`gif`图分为三部分，第一部分是像列表选择控件随意添加view，第二个就是这里要说的重点`ChooseFlowView`，第三个是修改布局以及回调结果
下面来细说`ChooseFLowView`，这里就是大家所熟悉的某宝或者某东商品详情页的多规格选择的`view`，需求就是按照他们的需求来的，这样需求就确定了
列一下需求表：
1. 列表的`ITEM`有三种状态：选中，非选中，不可选
2. 列表最大和最小选取数量，超过最大数量后按照`FIFO`算法更新选中列表
3. 三种状态的`ITEM`点击回调都需要监听结果
4. 数据的填充以及样式的定制
5. 列表更新时重用列表（算是内部调优吧）

### 2.2 设计思想
通过以上需求分析可以将这个`ChooseFlowView`的骨架图列出来了，因为是做通用的`view`，这里还是老样子采用**_接口_**和**_泛型_**去进行具体的实现
#### 2.2.1 首先来看ITEM的抽象

```
/**
 * Author: fanyafeng
 * Data: 2020/6/28 19:24
 * Email: fanyafeng@live.cn
 * Description: 多项选择view控件的单个控件的行为
 */
interface IChooseItemView:Serializable {

    /**
     * 是否可以被选中
     */
    fun isCheckable(): Boolean

    /**
     * 设置其是否可以被选中
     */
    fun setCheckable(isCheckable: Boolean)

    /**
     * 是否被选中
     */
    fun isChecked(): Boolean

    /**
     * 设置其被选中
     */
    fun setChecked(isChecked: Boolean)

    /**
     * Change the checked state of the view to the inverse of its current state
     */
    fun toggle()

}
```

#### 2.2.2 再来看数据接口的定义

```
/**
 * Author: fanyafeng
 * Data: 2020/6/29 09:31
 * Email: fanyafeng@live.cn
 * Description: 流式布局item
 *
 * data model需要继承这个接口
 */
interface IChooseModel : Serializable {

    /**
     * 单个view标题
     */
    fun getChooseItemTitle(): String

    /**
     * 是否可以被点击
     */
    fun getChooseItemCheckable(): Boolean

    /**
     * 是否被选中
     */
    fun getChooseItemChecked(): Boolean

    /**
     * 当有最大选取数量时控件会根据FIFO更新data model
     */
    fun setChooseItemChecked(isChecked: Boolean)
}
```
#### 2.2.3 下面就是ChooseFlowView提供的功能

```
/**
 * Author: fanyafeng
 * Data: 2020/6/29 09:53
 * Email: fanyafeng@live.cn
 * Description: 选取模式
 * 分为单选和多选
 */
interface IChooseFlowView : Serializable {

    /**
     * 设置最大选取数量
     */
    fun setMaxChooseCount(maxCount: Int)

    /**
     * 最大的选取数量，默认为1
     */
    fun getMaxChooseCount(): Int

    /**
     * 获取最小选取数量
     */
    fun getMinChooseCount(): Int

    /**
     * 设置最小选取数量
     * 默认为0，并且最大数量不能小于最小数量，但是可以相等
     */
    fun setMinChooseCount(minCount: Int)
}
```
## 三、实现
当时关于`ChooseFlowView`的定制方面有过好多想法，但是没想到好的实现方法，后来一边写一边改，最后选取了一种相对比较好方案
### 3.1 view定制
首先是`tag view`，这个会有一个默认的实现，但是默认实现是实现了`IChooseItemView`接口的，因为要统一行为，所以必须要实现此接口，同时当给`ChooseFlowView`设置`ITEM`时也是需要实现这个接口的，再有就是`data model`，它是对数据类型的抽象，这里说的话比较抽象，先大体来看一下方法的定义：

```
fun <T : ChooseItemView, M : IChooseModel> addItemView(
        itemView: T,
        model: M,
        params: LayoutParams? = null
    )
```
支持用户添加自定义`view`，但是`model`也是要实现`IChooseModel`的。
### 3.2 更新view
更新`ChooseFlowView`有一种简单粗暴的方法就是干掉所有`ITEM`再去新加，但是这样不太好，这里可以仿照`Recyclerview ViewHolder`的方案，有的话就拿来再去更新，没用的话就删除，但是重用的问题还是需要在用之前进行重置，这里要切记。
### 3.3 操作
这里算是文章的重点了，也是`ChooseFlowView`实现的核心代码最多的地方了
1. 用户首次填充数据初始化用户界面之前用户可选的最大数量和最小数量时确定的，为什么呢，因为用户可能过来一堆数据但是里面的选中态超过最大值，这时候就需要控件按照`FIFO`(符合用户的选取习惯)去筛选，同样在用户更新数据事也会遇到相同的问题，这样数据的显示就解决了
2. 以上数据筛选完成后便初始化页面，此时页面是按照用户的要求展示的，并且此时获取的结果是可靠的，而且是符合要求的
3. 涉及到用户点击操作时，当需要有多选，单选，反选的情况时可以去设置最大数量，最小数量来控制**（PS:后面可以添加是否支持反选，但是感觉意义不大暂时先搁置）**
4. 更新`ChooseFlowView`，更新页面时会重新筛选选中数据，这里有三种情况，新数据大于，等于，小于旧数据，相等的话是最好处理的，只需要更新`data list`刷新页面即可，小于的话需要将多余的`view`进行`remove`同时刷新页面，大于的话就需要去新建`ITEM`再将其加入到`ChooseFlowView`中
### 3.4 核心代码
#### 3.4.1 初始化数据

```
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
                setItemCheckStatus(selectList.first,false)
                selectList.removeFirst()
                selectList.addLast(position)
                setItemCheckStatus(position,true)
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
                        setItemCheckStatus(pos,false)
                        itemView.toggle()
                    }
                } else {
                    if (mCount >= maxCount) {
                        //取消第一个加入最后一个
                        val first = selectList.first
                        (getChildAt(first) as ChooseItemView).toggle()
                        itemView.toggle()
                        setItemCheckStatus(selectList.first,false)
                        selectList.removeFirst()
                        selectList.addLast(pos)
                        setItemCheckStatus(pos,true)
                    } else {
                        //添加选中
                        itemView.toggle()
                        selectList.addLast(pos)
                        setItemCheckStatus(pos,true)
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
```

#### 3.4.2 更新数据

```
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
                val chooseModel = model.first
                updateSelectList(index, chooseModel)
                //更新原有的model列表
                allModelList[index] = chooseModel
                //更新原有的view显示
                (getChildAt(index) as ChooseItemView).initData(chooseModel)
            }
        } else if (newCount > oldCount) {
            list.forEachIndexed { index, model ->
                val chooseModel = model.first
                updateSelectList(index, chooseModel)
                //新数据与原数据重叠部分
                if (index < oldCount) {
                    allModelList[index] = chooseModel
                    (getChildAt(index) as ChooseItemView).initData(chooseModel)
                } else {
                    addItemView(model.second, chooseModel)
                }
            }
        } else {
            list.forEachIndexed { index, model ->
                val chooseModel = model.first
                updateSelectList(index, chooseModel)
                //更新原有的model列表
                allModelList[index] = chooseModel
                //更新原有的view显示
                (getChildAt(index) as ChooseItemView).initData(chooseModel)
                //更新选中态
                setItemCheckStatus(index,true)
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
    private fun updateSelectList(selectPosition: Int, chooseModel: IChooseModel) {
        val initCount = selectList.size
        //先去判断当前item是否是选中状态
        if (chooseModel.getChooseItemChecked()) {
            //如果是选中状态，并且被选中的数量大于最大的可选数量
            if (initCount >= maxCount) {
                //首先更新被选中的第一个数据model
                setItemCheckStatus(selectList.first,false)
                //取消选中还需要更新控件状态
                (getChildAt(selectList.first) as ChooseItemView).toggle()
                //此时需要把第一个item删除
                selectList.removeFirst()

                //同时将选中状态的item添加到选中列表的最后位置
                //以下同理
                selectList.addLast(selectPosition)
                //此时更新被选中态item
                setItemCheckStatus(selectPosition,true)
            } else {
                selectList.addLast(selectPosition)
                setItemCheckStatus(selectPosition,true)
            }
        }
    }
```