package com.dev.rexhuang.zhiliao.music_hall;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.music_hall.adapter.ArtistCateAdapter;
import com.dev.rexhuang.zhiliao.music_hall.adapter.ArtistListAdapter;
import com.dev.rexhuang.zhiliao_core.api.qq.QQMusicApi;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.entity.Artist;
import com.dev.rexhuang.zhiliao_core.entity.ArtistsDataInfo;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public class ArtistListFragment extends ZhiliaoFragment {

    private ArtistsDataInfo.SingerListEntity.DataEntity.TagsEntity singerTag;
    private StringBuilder filterTips = new StringBuilder();
    private ArtistCateAdapter areaCateAdapter;
    private ArtistCateAdapter sexCateAdapter;
    private ArtistCateAdapter genreCateAdapter;
    private ArtistCateAdapter indexCateAdapter;
    private ArtistListAdapter artistListAdapter;
    @BindView(R.id.rcy_area)
    RecyclerView rcy_area;
    @BindView(R.id.rcy_genre)
    RecyclerView rcy_genre;
    @BindView(R.id.rcy_index)
    RecyclerView rcy_index;
    @BindView(R.id.rcy_sex)
    RecyclerView rcy_sex;
    @BindView(R.id.rcy_result)
    RecyclerView rcy_result;
    @BindView(R.id.titleTv)
    TextView titleTv;
    @BindView(R.id.toolbar)
    public Toolbar mToolbar;

    @Override
    public Object setLayout() {
        return R.layout.fragment_art;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        updateArtistList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initToolBar();
    }

    private void initToolBar() {
        if (getActivity() != null && mToolbar != null) {
            mToolbar.setTitle("歌手列表");
//            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
//            appCompatActivity.setSupportActionBar(mToolbar);
//            appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showArtistList(List<Artist> artists) {
        if (artists != null && artists.size() > 0) {
            if (artistListAdapter == null) {
                rcy_result.setLayoutManager(new LinearLayoutManager(getActivity()));
                artistListAdapter = new ArtistListAdapter(R.layout.item_artist_list, artists);
                artistListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                    }
                });
                rcy_result.setAdapter(artistListAdapter);
            } else {
                artistListAdapter.setNewData(artists);
            }
        }
    }

    private void showArtistTags(ArtistsDataInfo.SingerListEntity.DataEntity.TagsEntity tags) {
        titleTv.setText(filterTips.toString());
        if (areaCateAdapter == null) {
            singerTag = tags;
            areaCateAdapter = new ArtistCateAdapter(R.layout.item_artist_cate, singerTag.getArea());

            areaCateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    areaCateAdapter.position = position;
                    areaCateAdapter.flagId = singerTag.getArea().get(position) != null ? singerTag.getArea().get(position).getId() : -100;
                    areaCateAdapter.notifyDataSetChanged();
                    updateArtistList();
                }
            });
            rcy_area.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rcy_area.setAdapter(areaCateAdapter);

            sexCateAdapter = new ArtistCateAdapter(R.layout.item_artist_cate, singerTag.getSex());
            sexCateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    sexCateAdapter.position = position;
                    sexCateAdapter.flagId = singerTag.getSex().get(position) != null ? singerTag.getSex().get(position).getId() : -100;
                    sexCateAdapter.notifyDataSetChanged();
                    updateArtistList();
                }
            });
            rcy_sex.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rcy_sex.setAdapter(sexCateAdapter);

            genreCateAdapter = new ArtistCateAdapter(R.layout.item_artist_cate, singerTag.getGenre());
            genreCateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    genreCateAdapter.position = position;
                    genreCateAdapter.flagId = singerTag.getGenre().get(position) != null ? singerTag.getGenre().get(position).getId() : -100;
                    genreCateAdapter.notifyDataSetChanged();
                    updateArtistList();
                }
            });
            rcy_genre.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rcy_genre.setAdapter(genreCateAdapter);

            indexCateAdapter = new ArtistCateAdapter(R.layout.item_artist_cate, singerTag.getIndex());
            indexCateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    indexCateAdapter.position = position;
                    indexCateAdapter.flagId = singerTag.getIndex().get(position) != null ? singerTag.getIndex().get(position).getId() : -100;
                    indexCateAdapter.notifyDataSetChanged();
                    updateArtistList();
                }
            });
            rcy_index.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            rcy_index.setAdapter(indexCateAdapter);
        }
    }

    /**
     * 更新歌手分类
     */
    private void updateArtistList() {
        int area = areaCateAdapter != null ? areaCateAdapter.flagId : -100;
        int sex = sexCateAdapter != null ? sexCateAdapter.flagId : -100;
        int genre = genreCateAdapter != null ? genreCateAdapter.flagId : -100;
        int index = indexCateAdapter != null ? indexCateAdapter.flagId : -100;
        filterTips.setLength(0);
        if (singerTag != null && indexCateAdapter != null && singerTag.getIndex().get(indexCateAdapter.position).getName() != null) {
            filterTips.append(singerTag.getIndex().get(indexCateAdapter.position).getName());
        } else {
            filterTips.append("热门");
        }
        filterTips.append(" ");
        if (areaCateAdapter != null && areaCateAdapter.position != 0) {
            if (singerTag != null && singerTag.getArea().get(areaCateAdapter.position).getName() != null) {
                filterTips.append(singerTag.getArea().get(areaCateAdapter.position).getName());
                filterTips.append("-");
            }
        }
        if (sexCateAdapter != null && sexCateAdapter.position != 0) {
            if (singerTag != null && singerTag.getSex().get(sexCateAdapter.position).getName() != null) {
                filterTips.append(singerTag.getSex().get(sexCateAdapter.position).getName());
                filterTips.append("-");
            }
        }
        if (genreCateAdapter != null && genreCateAdapter.position != 0) {
            if (singerTag != null && singerTag.getGenre().get(genreCateAdapter.position).getName() != null) {
                filterTips.append(singerTag.getGenre().get(genreCateAdapter.position).getName());
            }
        }
//        val params = mapOf("area"to area, "sex"to sex, "genre"to genre, "index"to index)
        HashMap<String, Integer> params = new HashMap<>();
        params.put("area", area);
        params.put("sex", sex);
        params.put("genre", genre);
        params.put("index", index);
        Logger.d("artistList", params.toString());
        QQMusicApi.getArtistList(0, params, null, new ISuccess<ArtistsDataInfo>() {
            @Override
            public void onSuccess(ArtistsDataInfo response) {
                List<ArtistsDataInfo.SingerListEntity.DataEntity.SingerlistEntity> singerlistEntities =
                        response.getSingerList().getData().getSingerlist();
                if (singerlistEntities != null && singerlistEntities.size() >= 0) {
                    List<Artist> artists = new ArrayList<>();
                    for (ArtistsDataInfo.SingerListEntity.DataEntity.SingerlistEntity singerlistEntity : singerlistEntities) {
                        Artist artist = new Artist();
                        artist.setName(singerlistEntity.getSinger_name());
                        artist.setArtistId(singerlistEntity.getSinger_id());
                        artist.setPicUrl(singerlistEntity.getSinger_pic());
                        artist.setType("qq");
                        artists.add(artist);
                    }
                    Logger.d(response.getSingerList().getData().getSingerlist());
                    showArtistList(artists);
                    showArtistTags(response.getSingerList().getData().getTags());
                }
            }
        }, null, null);
    }
}
