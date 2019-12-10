package com.dev.rexhuang.zhiliao.music_hall;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.music_hall.adapter.NeteasePlayListDetailAdapter;
import com.dev.rexhuang.zhiliao.music_hall.adapter.PlayListDetailAdapter;
import com.dev.rexhuang.zhiliao_core.api.musiclake.MusicLakeApi;
import com.dev.rexhuang.zhiliao_core.api.zhiliao.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicSongListDetailEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongListDetailEntity;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dyhdyh.widget.loadingbar2.LoadingBar;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * *  created by RexHuang
 * *  on 2019/12/5
 */
public class PlayListActivity extends ZhiliaoActivity {

    private static final String NETEASEMUSICAPI = "NeteaseMusicApi";
    private static final String ZHILIAOAPI = "ZhiliaoApi";
    @BindView(R.id.playlist_toolbar)
    Toolbar playlist_toolbar;

    @BindView(R.id.rl_loading)
    RelativeLayout rl_loading;

    @BindView(R.id.playlist_rv)
    RecyclerView playlist_rv;

    @BindView(R.id.playlist_cl)
    ConstraintLayout playlist_cl;

    @BindView(R.id.iv_playlist_cover)
    ImageView iv_playlist_cover;

    @BindView(R.id.tv_playlist_name)
    TextView tv_playlist_name;

    @BindView(R.id.tv_title)
    TextView tv_title;

    private View headerView;
    private PlayListDetailAdapter playlistDetailAdapter;
    private NeteasePlayListDetailAdapter neteasePlayListDetailAdapter;

    private IRequest request = new IRequest() {
        @Override
        public void onRequestStart() {
            if (rl_loading != null) {
                rl_loading.setVisibility(View.VISIBLE);
                Log.d("rl_loading","show");
                LoadingBar.view(rl_loading)
                        .setFactoryFromResource(R.layout.loading_view)
                        .show();
            }
        }

        @Override
        public void onRequestEnd() {
            if (rl_loading != null) {
                Log.d("rl_loading","disappear");
                rl_loading.setVisibility(View.GONE);
                LoadingBar.view(rl_loading).cancel();
            }
        }
    };

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

    @Override
    public void loadContainerFragment(ZhiliaoFragment zhiliaoFragment) {
        //nothing, will be removed in future
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        headerView = LayoutInflater.from(this).inflate(R.layout.item_search_header, null);
        ButterKnife.bind(this);
        String from = getIntent().getStringExtra("From");

        String playList_ID = getIntent().getStringExtra("PlayList_ID");
        String playList_NAME = getIntent().getStringExtra("PlayList_NAME");
        int coverPosition = getIntent().getIntExtra("PlayList_COVER", 1);
        Bitmap coverBitmap = BitmapFactory.decodeResource(getResources(), drawables[coverPosition]);
        ImmersionBar.with(this)
                .titleBar(playlist_toolbar)
                .init();
        iv_playlist_cover.setImageBitmap(coverBitmap);
//        Drawable blur = BitmapUtils.createBlurredImageFromBitmap(coverBitmap, 12);
        playlist_cl.setBackground(getDrawable(drawables[coverPosition]));
        tv_title.setText(playList_NAME);
        tv_playlist_name.setText(playList_NAME);
        playlist_rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        if (NETEASEMUSICAPI.equals(from)) {
            neteasePlayListDetailAdapter = new NeteasePlayListDetailAdapter(R.layout.item_music_zhiliao, new ArrayList<>());
            playlist_rv.setAdapter(neteasePlayListDetailAdapter);
            MusicLakeApi.getSongListDetail(playList_ID, request, new ISuccess<NeteaseMusicSongListDetailEntity>() {
                @Override
                public void onSuccess(NeteaseMusicSongListDetailEntity response) {
                    if (response != null && response.getCode() == 200) {
                        List<NeteaseMusicSongListDetailEntity.PlaylistEntity.TracksEntity> trackIdsEntities = response.getPlaylist().getTracks();
                        if (trackIdsEntities != null && trackIdsEntities.size() > 0) {
                            neteasePlayListDetailAdapter.setNewData(trackIdsEntities);
                            neteasePlayListDetailAdapter.addHeaderView(headerView);
                        }
                    }
                }
            }, null, null);

        } else if (ZHILIAOAPI.equals(from)) {
            playlistDetailAdapter = new PlayListDetailAdapter(R.layout.item_music_zhiliao, new ArrayList<>());
            playlist_rv.setAdapter(playlistDetailAdapter);
            try {
                ZhiliaoApi.musicbill(MusicHallFragment.TOKEN, playList_ID, request, new ISuccess<SongListDetailEntity>() {
                    @Override
                    public void onSuccess(SongListDetailEntity response) {
                        if (response != null && response.getCode() == 0) {
                            List<MusicEntity> musicEntities = response.getDataEntity().getMusic_list();
                            if (musicEntities != null && musicEntities.size() > 0) {
                                playlistDetailAdapter.setNewData(response.getDataEntity().getMusic_list());
                                playlistDetailAdapter.addHeaderView(headerView);
                            }
                        }
                    }
                }, null, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
