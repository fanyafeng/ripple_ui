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
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingChild2;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewCompat.NestedScrollType;
import androidx.core.view.ViewCompat.ScrollAxis;
import androidx.core.view.ViewParentCompat;

import static androidx.core.view.ViewCompat.TYPE_NON_TOUCH;
import static androidx.core.view.ViewCompat.TYPE_TOUCH;

/**
 * Helper class for implementing nested scrolling child views compatible with Android platform
 * versions earlier than Android 5.0 Lollipop (API 21).
 *
 * <p>{@link View View} subclasses should instantiate a final instance of this
 * class as a field at construction. For each <code>View</code> method that has a matching
 * method signature in this class, delegate the operation to the helper instance in an overridden
 * method implementation. This implements the standard framework policy for nested scrolling.</p>
 *
 * <p>Views invoking nested scrolling functionality should always do so from the relevant
 * {@link ViewCompat}, {@link androidx.core.view.ViewGroupCompat} or
 * {@link ViewParentCompat} compatibility
 * shim static methods. This ensures interoperability with nested scrolling views on Android
 * 5.0 Lollipop and newer.</p>
 */
public class NestedScrollingChildHelper {
    /*
    嵌套滚动父视图touch类型

     */
    private ViewParent mNestedScrollingParentTouch;
    /*
    嵌套滚动父视图非touch类型
     */
    private ViewParent mNestedScrollingParentNonTouch;
    /*
    当前view
     */
    private final View mView;
    /*
    是否支持嵌套滚动
     */
    private boolean mIsNestedScrollingEnabled;
    /*
    临时存放嵌套滚动消耗数组
     */
    private int[] mTempNestedScrollConsumed;

    /**
     * Construct a new helper for a given view.
     * 构造方法，通过指定的view创建一个新的helper
     */
    public NestedScrollingChildHelper(@NonNull View view) {
        mView = view;
    }

    /**
     * Enable nested scrolling.
     * 启用嵌套滚动
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @param enabled true to enable nested scrolling dispatch from this view, false otherwise
     */
    public void setNestedScrollingEnabled(boolean enabled) {
        /*
        默认为false，第一次设置肯定不会走到
        如果为true，则是先停止当前view的的滚动否则的话还是不会走到
        在此设置会重新走嵌套滚动一系列操作，重新分配手势事件
         */
        if (mIsNestedScrollingEnabled) {
            ViewCompat.stopNestedScroll(mView);
        }
        mIsNestedScrollingEnabled = enabled;
    }

    /**
     * Check if nested scrolling is enabled for this view.
     * 判断这个view是否启用了嵌套滚动
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     * <p>
     * 这是个代理方法，在view的子类中调用NestedScrollingChild的接口方法，实现这个接口的标准规范
     * <p>
     * <p>
     * 如果返回为true，那么此view支持嵌套滑动
     *
     * @return true if nested scrolling is enabled for this view
     */
    public boolean isNestedScrollingEnabled() {
        return mIsNestedScrollingEnabled;
    }

    /**
     * Check if this view has a nested scrolling parent view currently receiving events for
     * a nested scroll in progress with the type of touch.
     * <p>
     * 检测这个view是否有嵌套滚动的父view（当前接收触摸类型的嵌套滚动事件）
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     * <p>
     * 这是个代理方法，在view的子类中调用NestedScrollingChild的接口方法，实现这个接口的标准规范
     * <p>
     * 如果是true的话那么这个view则有和它配套的嵌套滚动的父view，否则的话就没有
     *
     * @return true if this view has a nested scrolling parent, false otherwise
     */
    public boolean hasNestedScrollingParent() {
        /*
        5.0之前只考虑了用户操作层级的，为考虑到fling，所以执行此方法都是用户的操作，未分type
         */
        return hasNestedScrollingParent(TYPE_TOUCH);
    }

    /**
     * 比上方方法多一个type值
     * 主要是为了解决fling传递问题
     * <p>
     * {@link ViewCompat#TYPE_NON_TOUCH}
     * 非用户触摸产生的手势
     * <p>
     * {@link ViewCompat#TYPE_TOUCH}
     * 用户触摸产生的手势
     * <p>
     * Check if this view has a nested scrolling parent view currently receiving events for
     * a nested scroll in progress with the given type.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return true if this view has a nested scrolling parent, false otherwise
     */
    public boolean hasNestedScrollingParent(@NestedScrollType int type) {
        /*
        只要入参非TYPE_NON_TOUCH，TYPE_TOUCH则都不是嵌套滚动的父view
        这样的话就是没有和其搭配的父view视图
         */
        return getNestedScrollingParentForType(type) != null;
    }

    /**
     * Start a new nested scroll for this view.
     * 为当前的view视图开始一个新的嵌套滑动
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @param axes Supported nested scroll axes.
     *             See {@link androidx.core.view.NestedScrollingChild#startNestedScroll(int)}.
     *             <p>
     *             <p>
     *             如果返回为true则是找到了和其合作的嵌套滚动的父视图，并且开始进行嵌套滚动
     * @return true if a cooperating parent view was found and nested scrolling started successfully
     */
    public boolean startNestedScroll(@ScrollAxis int axes) {
        return startNestedScroll(axes, TYPE_TOUCH);
    }

    /**
     * Start a new nested scroll for this view.
     * <p>
     * 多一个type值，同上方
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild2} interface method with the same
     * signature to implement the standard policy.</p>
     * <p>
     * 滚动方向
     *
     * @param axes Supported nested scroll axes.
     *             See {@link NestedScrollingChild2#startNestedScroll(int,
     *             int)}.
     * @return true if a cooperating parent view was found and nested scrolling started successfully
     */
    public boolean startNestedScroll(@ScrollAxis int axes, @NestedScrollType int type) {
        /*
        判断是否有嵌套滑动的父view视图，如果有的话直接返回为true
         */
        if (hasNestedScrollingParent(type)) {
            /*
            Already in progress
            已经在进行中，直接返回为true
             */
            return true;
        }
        /*
        判断当前view是否支持嵌套滚动，如果不支持则直接返回fasle
         */
        if (isNestedScrollingEnabled()) {
            /*
            获取当前view的父视图
             */
            ViewParent p = mView.getParent();
            /*
            当前视图，命名为child是因为当前是NestedScrollingChild的辅助类
             */
            View child = mView;
            /*
            冒泡查找父view视图
             */
            while (p != null) {
                /*
                调用父view视图的onStartNestedScroll，判断其是否要配合嵌套滑动
                如果为true则父视图是符合嵌套滑动view标准的视图
                同时并且传入相应变量
                 */
                if (ViewParentCompat.onStartNestedScroll(p, child, mView, axes, type)) {
                    /*
                    设置滚动类型，兼容5.0之前的版本
                     */
                    setNestedScrollingParentForType(type, p);
                    /*
                    让父view视图在嵌套滚动时做一些前期工作
                    回调给父view视图
                     */
                    ViewParentCompat.onNestedScrollAccepted(p, child, mView, axes, type);
                    return true;
                }
                if (p instanceof View) {
                    child = (View) p;
                }
                p = p.getParent();
            }
        }
        return false;
    }

    /**
     * Stop a nested scroll in progress.
     * 停止嵌套滚动在滚动过程中
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     */
    public void stopNestedScroll() {
        stopNestedScroll(TYPE_TOUCH);
    }

    /**
     * Stop a nested scroll in progress.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild2} interface method with the same
     * signature to implement the standard policy.</p>
     */
    public void stopNestedScroll(@NestedScrollType int type) {
        ViewParent parent = getNestedScrollingParentForType(type);
        /*
        判断是否有可嵌套滑动的父view视图
         */
        if (parent != null) {
            /*
            如果有的话继续在当前视图中回调，通知父视图
             */
            ViewParentCompat.onStopNestedScroll(parent, mView, type);
            setNestedScrollingParentForType(type, null);
        }
    }

    /**
     * Dispatch one step of a nested scrolling operation to the current nested scrolling parent.
     * <p>
     * 将嵌套滚动操作的一步分配到当前嵌套滑动view的父view视图
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     * <p>
     * 如果父view视图消耗了任何距离则返回为true，否则返回fasle
     *
     * @return <code>true</code> if the parent consumed any of the nested scroll distance
     */
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
        return dispatchNestedScrollInternal(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, TYPE_TOUCH, null);
    }

    /**
     * Dispatch one step of a nested scrolling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link NestedScrollingChild2} interface
     * method with the same signature to implement the standard policy.
     *
     * @return <code>true</code> if the parent consumed any of the nested scroll distance
     */
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, @Nullable int[] offsetInWindow, @NestedScrollType int type) {
        return dispatchNestedScrollInternal(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type, null);
    }

    /**
     * Dispatch one step of a nested scrolling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link NestedScrollingChild3} interface
     * method with the same signature to implement the standard policy.
     */
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                     int dyUnconsumed, @Nullable int[] offsetInWindow, @NestedScrollType int type,
                                     @Nullable int[] consumed) {
        dispatchNestedScrollInternal(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                offsetInWindow, type, consumed);
    }

    private boolean dispatchNestedScrollInternal(int dxConsumed, int dyConsumed,
                                                 int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow,
                                                 @NestedScrollType int type, @Nullable int[] consumed) {
        /*
        判断当前view视图是否支持嵌套滚动
         */
        if (isNestedScrollingEnabled()) {
            /*
            判断父视图是否为可嵌套滚动父视图view
             */
            final ViewParent parent = getNestedScrollingParentForType(type);
            if (parent == null) {
                return false;
            }

            /*
            判断x，y轴只要有距离剩余则返回true
             */
            if (dxConsumed != 0 || dyConsumed != 0 || dxUnconsumed != 0 || dyUnconsumed != 0) {
                int startX = 0;
                int startY = 0;
                /*
                如果屏幕偏移量数组不为空
                 */
                if (offsetInWindow != null) {
                    /*
                    获取当前view所在屏幕位置
                     */
                    mView.getLocationInWindow(offsetInWindow);
                    /*
                    将当前view所在屏幕的x，y坐标存储
                     */
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }

                /*
                容错，如果位置消耗为空
                 */
                if (consumed == null) {
                    /*
                    赋值，获取临时的嵌套滚动消耗距离数组（new）
                     */
                    consumed = getTempNestedScrollConsumed();
                    consumed[0] = 0;
                    consumed[1] = 0;
                }

                /*
                回调父view onNestedScroll方法，通知父view视图相应的动作
                此时父view将会对位置，偏移量消耗进行重新操作
                 */
                ViewParentCompat.onNestedScroll(parent, mView,
                        dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);

                /*
                如果屏幕偏移量不为空
                 */
                if (offsetInWindow != null) {
                    /*
                    操作后进行重新的位置获取
                     */
                    mView.getLocationInWindow(offsetInWindow);
                    /*
                    获取完成后，则减去之前的x，y则是操作后的偏移量
                     */
                    offsetInWindow[0] -= startX;
                    offsetInWindow[1] -= startY;
                }
                return true;
            } else if (offsetInWindow != null) {
                /*
                No motion, no dispatch. Keep offsetInWindow up to date.
                无动作，无调度，更新当前视图的位置
                 */
                offsetInWindow[0] = 0;
                offsetInWindow[1] = 0;
            }
        }
        return false;
    }

    /**
     * Dispatch one step of a nested pre-scrolling operation to the current nested scrolling parent.
     * <p>
     * 将嵌套预滚动操作的一个步骤分派到当前嵌套滚动的父view视图
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     * <p>
     * 如果父view消耗使用了嵌套的任何滚动操作则返回为true
     *
     * @return true if the parent consumed any of the nested scroll
     */
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
                                           @Nullable int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, TYPE_TOUCH);
    }

    /**
     * 同dispatchNestedScroll
     * <p>
     * Dispatch one step of a nested pre-scrolling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link NestedScrollingChild2} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return true if the parent consumed any of the nested scroll
     */
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
                                           @Nullable int[] offsetInWindow, @NestedScrollType int type) {
        /*
        如果支持嵌套滑动
         */
        if (isNestedScrollingEnabled()) {
            final ViewParent parent = getNestedScrollingParentForType(type);
            if (parent == null) {
                return false;
            }

            if (dx != 0 || dy != 0) {
                int startX = 0;
                int startY = 0;
                if (offsetInWindow != null) {
                    mView.getLocationInWindow(offsetInWindow);
                    startX = offsetInWindow[0];
                    startY = offsetInWindow[1];
                }

                if (consumed == null) {
                    consumed = getTempNestedScrollConsumed();
                }
                consumed[0] = 0;
                consumed[1] = 0;
                ViewParentCompat.onNestedPreScroll(parent, mView, dx, dy, consumed, type);

                if (offsetInWindow != null) {
                    mView.getLocationInWindow(offsetInWindow);
                    offsetInWindow[0] -= startX;
                    offsetInWindow[1] -= startY;
                }
                return consumed[0] != 0 || consumed[1] != 0;
            } else if (offsetInWindow != null) {
                offsetInWindow[0] = 0;
                offsetInWindow[1] = 0;
            }
        }
        return false;
    }

    /**
     * Dispatch a nested fling operation to the current nested scrolling parent.
     * 将嵌套的fling操作分派到当前的嵌套滚动父view视图
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     * <p>
     * 如果父view视图消耗掉嵌套fling则返回true
     *
     * @return true if the parent consumed the nested fling
     */
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        if (isNestedScrollingEnabled()) {
            ViewParent parent = getNestedScrollingParentForType(TYPE_TOUCH);
            if (parent != null) {
                return ViewParentCompat.onNestedFling(parent, mView, velocityX,
                        velocityY, consumed);
            }
        }
        return false;
    }

    /**
     * Dispatch a nested pre-fling operation to the current nested scrolling parent.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @return true if the parent consumed the nested fling
     */
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        if (isNestedScrollingEnabled()) {
            ViewParent parent = getNestedScrollingParentForType(TYPE_TOUCH);
            if (parent != null) {
                return ViewParentCompat.onNestedPreFling(parent, mView, velocityX,
                        velocityY);
            }
        }
        return false;
    }

    /**
     * View subclasses should always call this method on their
     * <code>NestedScrollingChildHelper</code> when detached from a window.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     * <p>
     * 停止嵌套滚动，自身调用
     */
    public void onDetachedFromWindow() {
        ViewCompat.stopNestedScroll(mView);
    }

    /**
     * Called when a nested scrolling child stops its current nested scroll operation.
     *
     * <p>This is a delegate method. Call it from your {@link View View} subclass
     * method/{@link androidx.core.view.NestedScrollingChild} interface method with the same
     * signature to implement the standard policy.</p>
     *
     * @param child Child view stopping its nested scroll. This may not be a direct child view.
     */
    public void onStopNestedScroll(@NonNull View child) {
        ViewCompat.stopNestedScroll(mView);
    }

    private ViewParent getNestedScrollingParentForType(@NestedScrollType int type) {
        switch (type) {
            case TYPE_TOUCH:
                return mNestedScrollingParentTouch;
            case TYPE_NON_TOUCH:
                return mNestedScrollingParentNonTouch;
        }
        return null;
    }

    private void setNestedScrollingParentForType(@NestedScrollType int type, ViewParent p) {
        switch (type) {
            case TYPE_TOUCH:
                mNestedScrollingParentTouch = p;
                break;
            case TYPE_NON_TOUCH:
                mNestedScrollingParentNonTouch = p;
                break;
        }
    }

    private int[] getTempNestedScrollConsumed() {
        if (mTempNestedScrollConsumed == null) {
            mTempNestedScrollConsumed = new int[2];
        }
        return mTempNestedScrollConsumed;
    }
}
