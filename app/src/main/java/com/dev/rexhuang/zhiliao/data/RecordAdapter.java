package com.dev.rexhuang.zhiliao.data;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dev.rexhuang.zhiliao.R;

/**
 * *  created by RexHuang
 * *  on 2019/9/19
 */
public class RecordAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public RecordAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_record,item);
    }


}
