package com.dev.rexhuang.zhiliao.music_hall.banner;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dev.rexhuang.zhiliao_core.api.musiclake.MusicLakeApi;
import com.dev.rexhuang.zhiliao_core.entity.BannerEntity;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicDetailEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicUrlEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseResourceType;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.wenjian.loopbanner.LoopAdapter;

/**
 * *  created by RexHuang
 * *  on 2019/7/29
 */
public class BannerAdapter extends LoopAdapter<BannerEntity.BannersEntity> {
    @Override
    protected void onBindView(ViewHolder holder, BannerEntity.BannersEntity data, int position) {
        ImageView imageView = (ImageView) holder.itemView;
        Glide.with(holder.getContext())
                .load(data.getImageUrl())
                .apply(new RequestOptions()
                        .transform(
                                new CenterCrop(), new RoundedCorners(20)
                        )).into(imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(holder.getContext(), "position=$position", Toast.LENGTH_SHORT).show();
                if (Integer.valueOf(data.getTargetType()) == NeteaseResourceType.TYPE_MUSIC){
                    String musicID = data.getTargetId();
                    MusicLakeApi.getMusicDeatail(Integer.valueOf(musicID), null, new ISuccess<NeteaseMusicDetailEntity>() {
                        @Override
                        public void onSuccess(NeteaseMusicDetailEntity response) {
                            if (response != null && response.getCode() == 200) {
                                String picUrl = response.getSongs().get(0).getAl().getPicUrl();
                                String name = response.getSongs().get(0).getName();
                                String singerName = response.getSongs().get(0).getAr().get(0).getName();
                                MusicLakeApi.getMusicUrl(Integer.valueOf(musicID), null, new ISuccess<NeteaseMusicUrlEntity>() {
                                    @Override
                                    public void onSuccess(NeteaseMusicUrlEntity response) {
                                        if (response != null && response.getCode() == 200) {
                                            MusicEntity musicEntity = MusicEntity.create(String.valueOf(musicID),
                                                    name,
                                                    singerName,
                                                    response.getData().get(0).getUrl(),
                                                    picUrl);
//                                        MusicEntity musicEntity = new MusicEntity();
//                                        musicEntity.setId(String.valueOf(musicId));
//                                        musicEntity.setName(mNeteaseSearchAdapter.getItem(position).getName());
//                                        List<SingersEntity> singers = new ArrayList<>();
//                                        SingersEntity singersEntity = new SingersEntity();
//                                        singersEntity.setName(mNeteaseSearchAdapter.getItem(position).getArtists().get(0).getName());
//                                        singers.add(singersEntity);
//                                        musicEntity.setSingers(singers);
//                                        musicEntity.setNormal(response.getData().get(0).getUrl());
//                                        musicEntity.setCover(picUrl);
                                            MusicManager.getInstance().playMusicByEntity(musicEntity);
                                        }
                                    }
                                }, null, null);
                            }
                        }
                    },null,null);
                }
            }
        });
    }
}
