package com.dev.rexhuang.zhiliao.music_hall.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/8/10
 */
public class PlayListDetailAdapter extends BaseQuickAdapter<MusicEntity, BaseViewHolder> {
    public PlayListDetailAdapter(int layoutResId, @Nullable List<MusicEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MusicEntity item) {
        if (this.mLayoutResId == R.layout.fragment_media_list_item) {
            helper.setText(R.id.title, item.getName());
            helper.setText(R.id.description, item.getSingers().get(0).getName());
        } else if (this.mLayoutResId == R.layout.item_music){
            helper.setText(R.id.tv_title,item.getName());
            helper.setText(R.id.tv_artist,item.getSingers().get(0).getName());
            helper.addOnClickListener(R.id.iv_more);
//            Glide.with(mContext).load(descriptionCompat.getIconUri())
//                    .into((ImageView) helper.getView(R.id.iv_cover));
        } else if (this.mLayoutResId == R.layout.item_music_zhiliao){
            int position = helper.getAdapterPosition();
            helper.setText(R.id.tv_count, String.valueOf(position));
            helper.setText(R.id.tv_title,item.getName());
            helper.setText(R.id.tv_artist,item.getSingers().get(0).getName());
            helper.addOnClickListener(R.id.iv_more);
        }
//        Glide.with(mContext).load(mContext.getResources().getDrawable(drawables[helper.getLayoutPosition()]))
//                .apply(new RequestOptions().
//                        transform(new CenterCrop(), new RoundedCorners(20)))
//                .into((ImageView) helper.getView(R.id.iv_cover));
    }
}
