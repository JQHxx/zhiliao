package com.dev.rexhuang.zhiliao.music_hall.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.music_hall.MusicHallFragment;
import com.dev.rexhuang.zhiliao_core.api.zhiliao.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;
import com.dev.rexhuang.zhiliao.music_hall.banner.BannerAdapter;
import com.wenjian.loopbanner.LoopBanner;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;


/**
 * *  created by RexHuang
 * *  on 2019/7/31
 */
public class MultipleRecyclerAdapter extends BaseMultiItemQuickAdapter<MultipleItemEntity, BaseViewHolder> {

    private Context mContext;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MultipleRecyclerAdapter(List<MultipleItemEntity> data) {
        super(data);
//        addItemType();
    }

    public MultipleRecyclerAdapter(Context context, List<MultipleItemEntity> data) {
        super(data);
        addItemType(TYPE_BANNER, R.layout.item_banner);
        addItemType(TYPE_CHOICE, R.layout.item_choice);
        addItemType(TYPE_AGE, R.layout.item_age);
        addItemType(TYPE_TEXT, R.layout.item_text);
        addItemType(TYPE_SONGLIST, R.layout.item_songlist);
        mContext = context;
    }

    private static final int TYPE_BANNER = 0; //MultipleItemType.BANNER.ordinal();
    private static final int TYPE_CHOICE = 1; //MultipleItemType.CHOICE.ordinal();
    private static final int TYPE_AGE = 2;//MultipleItemType.AGE.ordinal();
    private static final int TYPE_TEXT = 3;//MultipleItemType.TEXT.ordinal();
    private static final int TYPE_SONGLIST = 4;//MultipleItemType.SONGLIST.ordinal();


    @Override
    protected void convert(@NonNull BaseViewHolder helper, MultipleItemEntity item) {
        switch (helper.getItemViewType()) {
            case TYPE_BANNER:
                List<String> images = item.getField(MultipleItemType.BANNER);
                LoopBanner loopBanner = helper.getView(R.id.banner);
                BannerAdapter bannerAdapter = new BannerAdapter();
                bannerAdapter.setNewData(images);
                loopBanner.setAdapter(bannerAdapter);
                break;
            case TYPE_CHOICE:
                break;
            case TYPE_AGE:
                break;
            case TYPE_TEXT:
                break;
            case TYPE_SONGLIST:
                RecyclerView songList = helper.getView(R.id.rv_song_list);
                if (songList.getLayoutManager() == null) {
                    LinearLayoutManager ll = new LinearLayoutManager(mContext, LinearLayout.HORIZONTAL, false);
                    songList.setLayoutManager(ll);
                }

                if (songList.getAdapter() == null) {
                    List<SongListEntity.DataEntity> dataEntities = item.getField(MultipleItemType.SONGLIST);
                    SongListAdapter songListAdapter = new SongListAdapter(R.layout.item_rv_songlist, dataEntities);
                    songListAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                            try {
                                ZhiliaoApi.musicbill(MusicHallFragment.TOKEN, dataEntities.get(position).getId(), null, null, null, null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    songList.setAdapter(songListAdapter);
                    songListAdapter.bindToRecyclerView(songList);
                } else {
                    ((SongListAdapter) songList.getAdapter()).setNewData(item.getField(MultipleItemType.SONGLIST));
                }
                break;
            default:
                break;
        }
        helper.addOnClickListener(R.id.layout_singer);
    }
}
