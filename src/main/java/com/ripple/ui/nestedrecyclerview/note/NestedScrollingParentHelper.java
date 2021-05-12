/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ripple.ui.nestedrecyclerview.note;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewCompat.NestedScrollType;
import androidx.core.view.ViewCompat.ScrollAxis;

/**
 * Helper class for implementing nested scrolling parent views compatible with Android platform
 * versions earlier than Android 5.0 Lollipop (API 21).
 *
 * <p>{@link ViewGroup ViewGroup} subclasses should instantiate a final instance
 * of this class as a field at construction. For each <code>ViewGroup</code> method that has
 * a matching method signature in this class, delegate the operation to the helper instance
 * in an overridden method implementation. This implements the standard framework policy
 * for nested scrolling.</p>
 *
 * <p>Views invoking nested scrolling functionality should always do so from the relevant
 * {@link ViewCompat}, {@link androidx.core.view.ViewGroupCompat} or
 * {@link androidx.core.view.ViewParentCompat} compatibility
 * shim static methods. This ensures interoperability with nested scrolling views on Android
 * 5.0 Lollipop and newer.</p>
 */
public class NestedScrollingParentHelper {
    /*
    用户级别的嵌套滑动type
     */
    private int mNestedScrollAxesTouch;
    /*
    非用户级别的嵌套滑动，如fling
     */
    private int mNestedScrollAxesNonTouch;

    /**
     * Construct a new helper for a given ViewGroup
     * 主要是通过以构造方法约束父布局为ViewGroup，子为view
     * 也只有ViewGroup才能嵌套子view
     */
    public NestedScrollingParentHelper(@NonNull ViewGroup viewGroup) {
    }

    /**
     * Called when a nested scrolling operation initiated by a descendant view is accepted
     * by this ViewGroup.
     * <p>
     * 当子（下降）视图发起的嵌套滚动操作被这个父view视图接受时调用
     *
     * <p>This is a delegate method. Call it from your {@link ViewGroup ViewGroup}
     * subclass method/{@link androidx.core.view.NestedScrollingParent} interface method with
     * the same signature to implement the standard policy.</p>
     */
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target,
                                       @ScrollAxis int axes) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    /**
     * Called when a nested scrolling operation initiated by a descendant view is accepted
     * by this ViewGroup.
     *
     * <p>This is a delegate method. Call it from your {@link ViewGroup ViewGroup}
     * subclass method/{@link androidx.core.view.NestedScrollingParent2} interface method with
     * the same signature to implement the standard policy.</p>
     */
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target,
                                       @ScrollAxis int axes, @NestedScrollType int type) {
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            mNestedScrollAxesNonTouch = axes;
        } else {
            mNestedScrollAxesTouch = axes;
        }
    }

    /**
     * Return the current axes of nested scrolling for this ViewGroup.
     * 为这个父view视图返回当前的滚动方向
     *
     * <p>This is a delegate method. Call it from your {@link ViewGroup ViewGroup}
     * subclass method/{@link androidx.core.view.NestedScrollingParent} interface method with
     * the same signature to implement the standard policy.</p>
     */
    @ScrollAxis
    public int getNestedScrollAxes() {
        return mNestedScrollAxesTouch | mNestedScrollAxesNonTouch;
    }

    /**
     * React to a nested scroll operation ending.
     * 相应嵌套盾冬操作结束
     *
     * <p>This is a delegate method. Call it from your {@link ViewGroup ViewGroup}
     * subclass method/{@link androidx.core.view.NestedScrollingParent} interface method with
     * the same signature to implement the standard policy.</p>
     */
    public void onStopNestedScroll(@NonNull View target) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH);
    }

    /**
     * React to a nested scroll operation ending.
     *
     * <p>This is a delegate method. Call it from your {@link ViewGroup ViewGroup}
     * subclass method/{@link androidx.core.view.NestedScrollingParent2} interface method with
     * the same signature to implement the standard policy.</p>
     */
    public void onStopNestedScroll(@NonNull View target, @NestedScrollType int type) {
        if (type == ViewCompat.TYPE_NON_TOUCH) {
            mNestedScrollAxesNonTouch = ViewGroup.SCROLL_AXIS_NONE;
        } else {
            mNestedScrollAxesTouch = ViewGroup.SCROLL_AXIS_NONE;
        }
    }
}
