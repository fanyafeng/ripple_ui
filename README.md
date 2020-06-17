# 封装一些常用的ui控件
**引入**
`implementation 'com.ripple.component:ui:0.0.1'`
## 一、图片九宫格控件
图片九宫格控件类似微信朋友圈的，但是这个添加了更多的自定义选项，九宫格的横纵个数是可以配置的，内置了一套和微信一样的配置，下面来看看如何使用，高级使用后面会讲
### 1.1 九宫格控件使用
其实抽象九宫格就是将数据的list以九宫格的形式呈现给用户，大体流程就是用户定义九宫格，继而设置list最后去拿回调。
#### 1.1.1 定义控件
首先在`xml`中定义`ui`控件

```
<com.ripple.ui.ninegridview.impl.NineGridView
            android:id="@+id/gridView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />
```
#### 1.1.2 使用控件
需要定义图片加载的实现，基本每个`app`都会使用自己的图片加载框架可能是自己的也可能是第三方的，这时候就需要对图片加载进行统一的管理，这里类比图片选择器的`imagload`：

```
//内部接口定义
interface NineGridLoadFrame : Serializable {
    /**
     * 通过外部图片框架进行图片的显示
     */
    fun displayImage(context: Context, path: String, imageView: RippleImageView)
}
//app内接口实现如果有特殊需求在这里面做就可以了，比如说加圆角或者遮罩
class MyLoadFrame : NineGridLoadFrame {
    override fun displayImage(context: Context, path: String, imageView: RippleImageView) {
        Glide.with(context).load(path).into(imageView)
    }
}
```
另外就是传入的数据结构`item`需要实现接口`NineItem`，加载图片路径是必须的，所以内部会有一个获取路径的方法：

```
interface NineItem : Serializable {
    /**
     * 图片路径是必须的，而且不能为空
     */
    fun getPath(): String
}

class SimpleNineItem(var imagePath: String) : NineItem {
    override fun getPath(): String {
        return imagePath
    }
}
```
而后就可以在`activity`或者`fragment`进行使用了

```
gridView.loadFrame = MyLoadFrame()
        gridView.adapter = NineGridAdapter(this, imageList)
        gridView.nineItemListener = object : NineItemListener.SimpleNineItemListener {
            override fun onClickListener(view: View, item: NineItem, position: Int) {
                println("我被点击了")
            }
        }
```
### 1.2 高级调用
有可能使用者想要最大行数或者最大列数还有空隙等都有自己的定义的需求，这里面将config抽象为一个接口，进行自己定义实现即可，定义完成后对`view`赋值
即可。

```
gridView.nineGridConfig = NineGridImpl()
```
下面是所有配置信息：
PS:如果不知道如何定义请查看内部类：`NineGridImpl()`
```
/**
 * Author: fanyafeng
 * Data: 2020/5/13 13:53
 * Email: fanyafeng@live.cn
 * Description: 九宫格view需要哪些属性和变量
 * simple:[NineGridImpl]
 */
interface NineGrid : Serializable {

    /**
     * 设置九宫格间距
     */
    fun setDivide(divide: Int)

    /**
     * 获取九宫格间距
     */
    fun getDivide(): Int


    /**
     * 设置图片单张时显示的宽度
     */
    fun setSingleWidth(singleWidth: Int)

    /**
     * 获取单张图片时view的显示宽度
     */
    fun getSingleWidth(): Int

    /**
     * 设置单张图片的显示比例
     */
    fun setSingleImageRatio(ratio: Float)

    /**
     * 获取单张图片的显示比例
     */
    fun getSingleImageRatio(): Float

    /**
     * 设置一行最多显示多少张图片
     */
    fun setPerLineCount(count: Int)

    /**
     * 获取一行显示图片最大张数
     */
    fun getPerLineCount(): Int

    /**
     * 设置最大的显示行数
     */
    fun setMaxLine(maxLine: Int)

    /**
     * 获取最大的显示行数
     */
    fun getMaxLine(): Int
}
```
### 1.3 详细说明
下方其实在注释信息中都有：

```
/**
 * Author: fanyafeng
 * Data: 2020/5/13 14:02
 * Email: fanyafeng@live.cn
 * Description: 图片九宫格view
 * 主要是以下两种方式：
 * 单张图片：
 *
 *           屏幕宽度
 * ---------------------------
 * 图片
 * -----------
 * |         |
 * |         |
 * |         |
 * |         |
 * |         |
 * |         |
 * -----------
 *
 * ----------------------------
 *
 * 再有就是多张图
 * 多张图可以设置一行最多显示多少图片 eg:3
 * 再有就是最多显示多少行 eg:3
 * 图片宽高比是一比一
 * eg:如果现在是四张图
 * 那么第一行显示三张，平分view宽度，
 * 三张的话还剩下一张去显示，那样的话就需要和第一行的排布一样
 * 比例还是一比一，但是后面两个空位也会占用三分之二
 * 类似权重，虽然不显示但是需要占位
 * 可以直接类比微信
 *
 *                       屏幕宽度
 * ----------------------------------------
 * ----------    ----------     -----------
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * ----------    ----------     -----------
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * ----------    ----------     -----------
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * ----------    ----------     -----------
 * ----------------------------------------
 *
 * 四张显示如下，未做特殊处理
 *
 *                       屏幕宽度
 * ----------------------------------------
 * ----------    ----------     -----------
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * |        |    |        |     |         |
 * ----------    ----------     -----------
 * |        |
 * |        |
 * |        |
 * |        |
 * ----------
 * ----------------------------------------
 */
```
