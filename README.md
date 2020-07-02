# 封装一些常用的ui控件
**引入**

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
## 1.图片九宫格控件
图片九宫格控件类似微信朋友圈的，[九宫格库使用以及设计原理](https://github.com/1181631922/ModuleSample/blob/master/ripple_ui/doc/ninegrid.md)
实际效果图有待补充
## 2.标签列表选择控件
标签列表选择控件类似于淘宝，京东的多规格选择[多规格选择控件使用以及设计原理](https://github.com/1181631922/ModuleSample/blob/master/ripple_ui/doc/chooseflowview.md)