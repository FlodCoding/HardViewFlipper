package com.flod.ui;

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

    private List<Data> mDataList;

    protected  int getItemLayoutId(int position){return 0;}

    protected abstract void onBind(View view, Data data);

    void bind(View view, int position) {
        onBind(view, mDataList.get(position));
    }

    public void setDataList(@NonNull List<Data> list) {
        mDataList = list;
    }

    public int getItemCount() {
        if (mDataList == null) return 0;
        return mDataList.size();
    }


}
