package com.dev.rexhuang.zhiliao.music_hall.adapter;

import com.dev.rexhuang.zhiliao_core.entity.RecommendSongListEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/7/31
 */
public class MultipleItemsCreator {

    private static final String RECOMMEND = "推荐歌单";
    private static final String YOURS = "你的歌单";
    private List<MultipleItemEntity> multipleItemEntities = new ArrayList<>();
    private List<SongListEntity.DataEntity> dataEntity;
    private List<RecommendSongListEntity.ResultEntity> resultEntities;
    private List<String> imagesArray;

    public MultipleItemsCreator(List<SongListEntity.DataEntity> dataEntity, List<String> imagesArray) {
        this.dataEntity = dataEntity;
        this.imagesArray = imagesArray;
    }

    public void setDataEntity(List<SongListEntity.DataEntity> dataEntity) {
        this.dataEntity = dataEntity;
    }

    public void setRecommendListEntity(List<RecommendSongListEntity.ResultEntity> resultEntities) {
        this.resultEntities = resultEntities;
    }

    public void setImagesArray(List<String> imagesArray) {
        this.imagesArray = imagesArray;
    }

    public List<MultipleItemEntity> create() {
        if (imagesArray == null) {
            throw new RuntimeException("imagesArray is not ready,call setImagesArray");
        }
        if (dataEntity == null) {
            throw new RuntimeException("SongListEntity.DataEntity dataEntity is not ready,call setDataEntity");
        }
        multipleItemEntities.clear();
        MultipleItemEntity bannerItem = new MultipleItemEntity();
        bannerItem.setField(MultipleItemType.ITEM_TYPE, MultipleItemType.BANNER.ordinal());
        bannerItem.setField(MultipleItemType.BANNER, imagesArray);

        MultipleItemEntity choiceItem = new MultipleItemEntity();
        choiceItem.setField(MultipleItemType.ITEM_TYPE, MultipleItemType.CHOICE.ordinal());
        choiceItem.setField(MultipleItemType.CHOICE, "");

        MultipleItemEntity ageItem = new MultipleItemEntity();
        ageItem.setField(MultipleItemType.ITEM_TYPE, MultipleItemType.AGE.ordinal());
        ageItem.setField(MultipleItemType.AGE, "");

        MultipleItemEntity recommendSongListTextItem = new MultipleItemEntity();
        recommendSongListTextItem.setField(MultipleItemType.ITEM_TYPE, MultipleItemType.TEXT.ordinal());
        recommendSongListTextItem.setField(MultipleItemType.TEXT, RECOMMEND);

        MultipleItemEntity recommendSongListItem = new MultipleItemEntity();
        recommendSongListItem.setField(MultipleItemType.ITEM_TYPE, MultipleItemType.RECOMMONSONGLIST.ordinal());
        recommendSongListItem.setField(MultipleItemType.RECOMMONSONGLIST, resultEntities);

        MultipleItemEntity songListTextItem = new MultipleItemEntity();
        songListTextItem.setField(MultipleItemType.ITEM_TYPE, MultipleItemType.TEXT.ordinal());
        songListTextItem.setField(MultipleItemType.TEXT, YOURS);

        MultipleItemEntity songListItem = new MultipleItemEntity();
        songListItem.setField(MultipleItemType.ITEM_TYPE, MultipleItemType.SONGLIST.ordinal());
        songListItem.setField(MultipleItemType.SONGLIST, dataEntity);

        multipleItemEntities.add(bannerItem);
        multipleItemEntities.add(choiceItem);
        multipleItemEntities.add(ageItem);
        if (resultEntities != null) {
            multipleItemEntities.add(recommendSongListTextItem);
            multipleItemEntities.add(recommendSongListItem);
        }
        multipleItemEntities.add(songListTextItem);
        multipleItemEntities.add(songListItem);
        return this.multipleItemEntities;
    }


}
