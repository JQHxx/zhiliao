package com.dev.rexhuang.zhiliao.find.queue;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/8/13
 */
public class QueueAdapter extends BaseQuickAdapter<MusicEntity, BaseViewHolder> {

    private int index;

    public QueueAdapter(int layoutResId, @Nullable List<MusicEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MusicEntity item) {
        helper.setText(R.id.tv_title, item.getName());
        helper.setText(R.id.tv_artist, item.getSingers().get(0).getName());
        //选中正在播放的歌曲
        if (MusicManager.getInstance().getNowPlayingSongInfo().getId() == item.getId() && MusicManager.getInstance().getNowPlayingIndex() == helper.getAdapterPosition()) {
            index = helper.getAdapterPosition();
            helper.setTextColor(R.id.tv_title, Color.parseColor("#0091EA"));
            helper.setTextColor(R.id.tv_artist, Color.parseColor("#01579B"));
        } else {
            helper.setTextColor(R.id.tv_title, Color.parseColor("#ffffff"));
            helper.setTextColor(R.id.tv_artist, Color.parseColor("#9e9e9e"));
        }
        helper.addOnClickListener(R.id.iv_more);
    }

    public int getIndex() {
        return index;
    }
}
