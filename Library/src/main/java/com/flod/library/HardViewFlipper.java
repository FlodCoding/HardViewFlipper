package com.flod.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewFlipper;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2018-11-20
 * UseDes:
 */
public class HardViewFlipper extends ViewFlipper {
    private HardViewFlipperAdapter mAdapter;
    private int itemLayoutId;

    public HardViewFlipper(Context context) {
        super(context);

    }

    public HardViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HardViewFlipper);
        if (typedArray != null) {
            itemLayoutId = typedArray.getResourceId(R.styleable.HardViewFlipper_itemLayout, 0);
            typedArray.recycle();
        }
    }

    /**
     * 设置一个适配器
     * @param adapter HardViewFlipperAdapter
     * @return HardViewFlipper
     */
    public HardViewFlipper setAdapter(HardViewFlipperAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("adapter is null !!");
        }
        mAdapter = adapter;
        addView();
        return this;
    }


    /**
     * 添加视图到轮播中
     * itemLayoutId 有两种设置方式：
     * 1、{@link HardViewFlipperAdapter#getItemLayoutId(int)}，如果轮播中有不同的布局需求，可以重写这个方法
     * 2、xml中使用 {app:itemLayout} 或者 {@link #setItemLayoutId(int)} 注：所有的布局都会使用同一个布局
     * 注：如果同时使用1的优先级大于2
     */
    private void addView() {
        removeAllViews();
        int itemCount = mAdapter.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            int formAdapterItemLayout = mAdapter.getItemLayoutId(i);
            if (formAdapterItemLayout != 0) itemLayoutId = formAdapterItemLayout;
            if (itemLayoutId != 0) {
                View view = LayoutInflater.from(getContext()).inflate(itemLayoutId, this, false);
                mAdapter.bind(view, i);
                addView(view);
            }
        }
    }

    /**
     * 设置轮播视图的布局Id
     *
     * @param layoutId 布局Id
     */
    public void setItemLayoutId(@LayoutRes int layoutId) {
        itemLayoutId = layoutId;
    }

}
