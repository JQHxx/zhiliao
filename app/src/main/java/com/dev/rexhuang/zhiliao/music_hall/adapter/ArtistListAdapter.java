package com.dev.rexhuang.zhiliao.music_hall.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao_core.entity.Artist;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/9/26
 */
public class ArtistListAdapter extends BaseQuickAdapter<Artist, BaseViewHolder> {

    public ArtistListAdapter(int layoutResId, @Nullable List<Artist> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Artist item) {
        helper.setText(R.id.tv_name, item.getName());
        Glide.with((ImageView) helper.getView(R.id.iv_cover)).load(item.getPicUrl()).into((ImageView) helper.getView(R.id.iv_cover));
    }
}
