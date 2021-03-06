package com.flod.ui.flipper;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2018-11-20
 * UseDes:
 */
public abstract class HardViewFlipperAdapter<Data> {
    public HardViewFlipperAdapter() {
    }

    public HardViewFlipperAdapter(List<Data> dataList) {
        mDataList = dataList;
    }

    private List<Data> mDataList;

    protected int getItemLayoutId(int position) {
        return 0;
    }

    protected abstract void onBind(View view, int position, Data data);

    void bind(View view, int position) {
        onBind(view, position, mDataList.get(position));
    }

    public void setDataList(@NonNull List<Data> list) {
        mDataList = list;
    }

    public int getItemCount() {
        if (mDataList == null) return 0;
        return mDataList.size();
    }


}
