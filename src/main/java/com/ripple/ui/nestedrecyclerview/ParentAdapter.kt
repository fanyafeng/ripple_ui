package com.ripple.ui.nestedrecyclerview

import com.ripple.ui.nestedrecyclerview.ChildRecyclerView


/**
 * Author: fanyafeng
 * Data: 2020/8/28 17:57
 * Email: fanyafeng@live.cn
 * Description:
 */
interface ParentAdapter {
    fun getCurrentChildRecyclerView(): ChildRecyclerView?
}