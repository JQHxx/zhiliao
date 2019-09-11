package com.dev.rexhuang.zhiliao.find.adapter;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dev.rexhuang.zhiliao.R;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/8/10
 */
public class SearchAdapter extends BaseQuickAdapter<MediaMetadataCompat, BaseViewHolder> {
    public SearchAdapter(int layoutResId, @Nullable List<MediaMetadataCompat> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MediaMetadataCompat item) {
        if (this.mLayoutResId == R.layout.fragment_media_list_item) {
            MediaDescriptionCompat descriptionCompat = item.getDescription();
            helper.setText(R.id.title, descriptionCompat.getTitle());
            helper.setText(R.id.description, descriptionCompat.getSubtitle());
        } else if (this.mLayoutResId == R.layout.item_music){
            MediaDescriptionCompat descriptionCompat = item.getDescription();
            helper.setText(R.id.tv_title,descriptionCompat.getTitle());
            helper.setText(R.id.tv_artist,descriptionCompat.getSubtitle());
//            Glide.with(mContext).load(descriptionCompat.getIconUri())
//                    .into((ImageView) helper.getView(R.id.iv_cover));
        }
//        Glide.with(mContext).load(mContext.getResources().getDrawable(drawables[helper.getLayoutPosition()]))
//                .apply(new RequestOptions().
//                        transform(new CenterCrop(), new RoundedCorners(20)))
//                .into((ImageView) helper.getView(R.id.iv_cover));
    }
}
