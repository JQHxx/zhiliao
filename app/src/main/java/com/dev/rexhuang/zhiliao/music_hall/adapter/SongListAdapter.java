package com.dev.rexhuang.zhiliao.music_hall.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * *  created by RexHuang
 * *  on 2019/7/30
 */
public class SongListAdapter extends BaseQuickAdapter<SongListEntity.DataEntity, SongListViewHolder> {

    private int[] drawables = new int[]{R.drawable.cover_00001, R.drawable.cover_00002, R.drawable.cover_00003,
            R.drawable.cover_00004, R.drawable.cover_00005, R.drawable.cover_00006,
            R.drawable.cover_00009, R.drawable.cover_00008, R.drawable.cover_00007,
            R.drawable.cover_00010, R.drawable.cover_00011, R.drawable.cover_00012,
            R.drawable.cover_00015, R.drawable.cover_00014, R.drawable.cover_00013,
            R.drawable.cover_00016, R.drawable.cover_00017, R.drawable.cover_00018,
            R.drawable.cover_00021, R.drawable.cover_00020, R.drawable.cover_00019,
            R.drawable.cover_00022, R.drawable.cover_00023, R.drawable.cover_00024,
            R.drawable.cover_00029, R.drawable.cover_00028, R.drawable.cover_00025,
            R.drawable.cover_00030, R.drawable.cover_00027, R.drawable.cover_00026,
    };

    public SongListAdapter(int layoutResId, @Nullable List<SongListEntity.DataEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull SongListViewHolder helper, SongListEntity.DataEntity item) {
        helper.setText(R.id.tv_name, item.getName());
        Glide.with(mContext).load(mContext.getResources().getDrawable(drawables[helper.getLayoutPosition()]))
                .apply(new RequestOptions().
                        transform(new CenterCrop(), new RoundedCorners(20)))
                .into((ImageView) helper.getView(R.id.iv_cover));
    }
}
