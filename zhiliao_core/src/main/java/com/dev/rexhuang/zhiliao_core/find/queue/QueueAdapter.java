package com.dev.rexhuang.zhiliao_core.find.queue;

import android.graphics.Color;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.player.PlayManager;
import com.dev.rexhuang.zhiliao_core.player2.playback.QueueManager;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/8/13
 */
public class QueueAdapter extends BaseQuickAdapter<MediaBrowserCompat.MediaItem, BaseViewHolder> {
    public QueueAdapter(int layoutResId, @Nullable List<MediaBrowserCompat.MediaItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MediaBrowserCompat.MediaItem item) {
        helper.setText(R.id.tv_title, item.getDescription().getTitle());
        helper.setText(R.id.tv_artist, item.getDescription().getSubtitle());
        //选中正在播放的歌曲
        if (QueueManager.getInstance().getCurrentItem() == item && QueueManager.getInstance().getCurrentIndex() == helper.getAdapterPosition()) {
            helper.setTextColor(R.id.tv_title, Color.parseColor("#0091EA"));
            helper.setTextColor(R.id.tv_artist, Color.parseColor("#01579B"));
        } else {
//            if (ThemeStore.THEME_MODE == ThemeStore.DAY) {
//                holder.setTextColor(R.id.tv_title, Color.parseColor("#000000"))
//            } else {
            helper.setTextColor(R.id.tv_title, Color.parseColor("#ffffff"));
//            }
            helper.setTextColor(R.id.tv_artist, Color.parseColor("#9e9e9e"));
        }
        helper.addOnClickListener(R.id.iv_more);
    }
}
