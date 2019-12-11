package com.dev.rexhuang.zhiliao.search.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicEntity;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/8/10
 */
public class NeteaseSearchAdapter extends BaseQuickAdapter<NeteaseMusicEntity.ResultEntity.SongsEntity, BaseViewHolder> {
    public NeteaseSearchAdapter(int layoutResId, @Nullable List<NeteaseMusicEntity.ResultEntity.SongsEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NeteaseMusicEntity.ResultEntity.SongsEntity item) {
        if (this.mLayoutResId == R.layout.item_music){
            helper.setText(R.id.tv_title,item.getName());
            helper.setText(R.id.tv_artist,item.getArtists().get(0).getName());
            helper.addOnClickListener(R.id.iv_more);
//            Glide.with(mContext).load(descriptionCompat.getIconUri())
//                    .into((ImageView) helper.getView(R.id.iv_cover));
        }
//        Glide.with(mContext).load(mContext.getResources().getDrawable(drawables[helper.getLayoutPosition()]))
//                .apply(new RequestOptions().
//                        transform(new CenterCrop(), new RoundedCorners(20)))
//                .into((ImageView) helper.getView(R.id.iv_cover));
    }
}
