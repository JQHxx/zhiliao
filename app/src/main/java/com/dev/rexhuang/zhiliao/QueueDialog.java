package com.dev.rexhuang.zhiliao;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao.find.queue.QueueAdapter;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.model.MusicProvider;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/9/19
 */
public class QueueDialog extends BottomSheetDialog {
    private RecyclerView mRecyclerView;
    private QueueAdapter mQueueAdapter;
    private LinearLayout ll_mode;
    private TextView tv_play_mode;
    private ImageView iv_play_mode;
    private String[] play_mode_text;
    private TextView tv_close;
    private Drawable[] play_mode_drawable;
    private ImageView iv_clear_all;

    public QueueDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public QueueDialog(@NonNull Context context, int theme) {
        super(context, theme);
        init(context);
    }

    protected QueueDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        play_mode_text = context.getResources().getStringArray(R.array.play_mode_text);
        play_mode_drawable = new Drawable[]{context.getDrawable(R.drawable.ic_order),
                context.getDrawable(R.drawable.ic_repeat),
                context.getDrawable(R.drawable.ic_loop)};
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.6);
        View view = View.inflate(context, R.layout.dialog_playqueue, null); //设置Dialog的位置和宽高
        setContentView(view);
        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
        Window window = getWindow();
        WindowManager.LayoutParams lp = null;
        if (window != null) {
            lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = height;
            window.setAttributes(lp);
            window.findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
            mBehavior.setPeekHeight(lp.height);
        }
        View bottomSheet = findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.getLayoutParams().height = height;
        }

        //设置Dialog的操作逻辑
        setCanceledOnTouchOutside(true);
        initView(context);
    }

    private void initView(Context context) {
        tv_close = findViewById(R.id.tv_close);
        ll_mode = findViewById(R.id.ll_mode);
        tv_play_mode = findViewById(R.id.tv_play_mode);
        iv_play_mode = findViewById(R.id.iv_play_mode);
        iv_clear_all = findViewById(R.id.iv_clear_all);
        mRecyclerView = findViewById(R.id.rcv_songs);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mQueueAdapter = new QueueAdapter(R.layout.item_queue, MusicManager.getInstance().getPlayList());
        mRecyclerView.setAdapter(mQueueAdapter);
        mQueueAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String musicId = mQueueAdapter.getItem(position).getId();
                String musicName = mQueueAdapter.getItem(position).getName();
                if (!TextUtils.isEmpty(musicId) &&
                        !musicId.equals(MusicManager.getInstance().getNowPlayingSongId())) {
                    MusicManager.getInstance().playMusicByIndex(position);
                } else if (!TextUtils.isEmpty(musicName)) {
                    Logger.d(musicName + "is already playing");
                }
            }
        });
        mQueueAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_delete) {
                    if (MusicManager.getInstance().getNowPlayingIndex() == position) {
                        MusicEntity musicEntity = MusicManager.getInstance().getNowPlayingSongInfo();
                        WarningDialog warningDialog = new WarningDialog(context);
                        warningDialog.setOnFirmListener(new WarningDialog.OnFirmListener() {
                            @Override
                            public void onFirm() {
                                MusicManager.getInstance().deleteFromMusicQueue(musicEntity, true);
                            }
                        });
                        warningDialog.setMessage(musicEntity.getName() + "正在播放中,确定要从播放列表中删除吗?");
                        warningDialog.show();
                    } else {
                        MusicManager.getInstance().deleteFromMusicQueue(mQueueAdapter.getItem(position), false);
                    }

                }
            }
        });
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        onRepeatModeChanged(MusicManager.getInstance().getRepeatMode());
        ll_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMode();
            }
        });
        iv_clear_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WarningDialog warningDialog = new WarningDialog(context);
                warningDialog.setMessage("是否要清空播放列表");
                warningDialog.setOnFirmListener(new WarningDialog.OnFirmListener() {
                    @Override
                    public void onFirm() {
                        MusicManager.getInstance().clearPlayList();
                    }
                });
                warningDialog.show();
            }
        });
    }

    public void nextMode() {
        switch (MusicManager.getInstance().getRepeatMode()) {
            case PlaybackStateCompat.REPEAT_MODE_NONE:
                MusicManager.getInstance().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                tv_play_mode.setText(play_mode_text[1]);
                iv_play_mode.setImageDrawable(play_mode_drawable[1]);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ONE:
                MusicManager.getInstance().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                tv_play_mode.setText(play_mode_text[2]);
                iv_play_mode.setImageDrawable(play_mode_drawable[2]);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                MusicManager.getInstance().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                tv_play_mode.setText(play_mode_text[0]);
                iv_play_mode.setImageDrawable(play_mode_drawable[0]);
                break;
        }
    }

    public void setAdapter(QueueAdapter queueAdapter) {
        mQueueAdapter = queueAdapter;
        mRecyclerView.setAdapter(queueAdapter);
    }

    public void notifyDataSetChanged() {
        mQueueAdapter.notifyDataSetChanged();
    }

    public void setNewData(List<MusicEntity> musicEntities) {
        mQueueAdapter.setNewData(musicEntities);
    }

    @Override
    public void show() {
        if (MusicManager.getInstance().getNowPlayingIndex() >= 0) {
            mRecyclerView.scrollToPosition(MusicManager.getInstance().getNowPlayingIndex());
        }
        super.show();
    }

    public void onRepeatModeChanged(int repeatMode) {
        switch (repeatMode) {
            case PlaybackStateCompat.REPEAT_MODE_NONE:
                tv_play_mode.setText(play_mode_text[0]);
                iv_play_mode.setImageDrawable(play_mode_drawable[0]);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ONE:
                tv_play_mode.setText(play_mode_text[1]);
                iv_play_mode.setImageDrawable(play_mode_drawable[1]);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                tv_play_mode.setText(play_mode_text[2]);
                iv_play_mode.setImageDrawable(play_mode_drawable[2]);
                break;
        }
    }

    public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
        setNewData(MusicManager.getInstance().getPlayList());
        if (MusicManager.getInstance().getPlayList().size() <= 0) {
            hide();
        }
    }

}
