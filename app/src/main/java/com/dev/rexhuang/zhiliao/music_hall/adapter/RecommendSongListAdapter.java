package com.dev.rexhuang.zhiliao.music_hall.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao_core.entity.RecommendSongListEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;
import com.google.android.exoplayer2.C;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/7/30
 */
public class RecommendSongListAdapter extends BaseQuickAdapter<RecommendSongListEntity.ResultEntity, BaseViewHolder> {


    public RecommendSongListAdapter(int layoutResId, @Nullable List<RecommendSongListEntity.ResultEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, RecommendSongListEntity.ResultEntity item) {
        helper.setText(R.id.tv_name, item.getName());
        String cover = item.getPicUrl();
        String playCount;
        int count = item.getPlayCount();
        if (count / 100000000 > 0) {
            StringBuilder sb = new StringBuilder(String.valueOf(count / 100000000));
            playCount = sb.append("亿").toString();
        } else if (count / 10000 > 0) {
            StringBuilder sb = new StringBuilder(String.valueOf(count / 10000));
            playCount = sb.append("万").toString();
        } else {
            playCount = String.valueOf(count);
        }
        helper.setText(R.id.tv_count, playCount);
        Glide.with(mContext).load(cover)
                .apply(new RequestOptions().
                        transform(new CenterCrop(), new RoundedCorners(20)))
                .into((ImageView) helper.getView(R.id.iv_cover));
    }
}
