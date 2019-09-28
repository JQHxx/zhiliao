package com.dev.rexhuang.zhiliao.music_hall.adapter;

import android.graphics.Color;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao_core.entity.ArtistsDataInfo;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public class ArtistCateAdapter extends BaseQuickAdapter<ArtistsDataInfo.SingerListEntity.DataEntity.TagsEntity.CategoryEntity, BaseViewHolder> {

    public int flagId = -100;
    public int position = 0;

    private List<ArtistsDataInfo.SingerListEntity.DataEntity.TagsEntity.CategoryEntity> categoryEntities;

    public ArtistCateAdapter(int layoutResId, List<ArtistsDataInfo.SingerListEntity.DataEntity.TagsEntity.CategoryEntity> categoryEntities) {
        super(layoutResId, categoryEntities);
        this.categoryEntities = categoryEntities;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ArtistsDataInfo.SingerListEntity.DataEntity.TagsEntity.CategoryEntity item) {
        helper.setText(R.id.titleTv, item.getName());
        boolean isChecked = position == helper.getAdapterPosition();
        ((CheckedTextView) helper.getView(R.id.titleTv)).setChecked(isChecked);
        helper.setTextColor(R.id.titleTv, isChecked ? Color.BLACK : Color.WHITE);
    }

}
