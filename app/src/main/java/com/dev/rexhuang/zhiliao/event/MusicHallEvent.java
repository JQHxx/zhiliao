package com.dev.rexhuang.zhiliao.event;

import com.dev.rexhuang.zhiliao.music_hall.adapter.MultipleItemEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/9/12
 */
public class MusicHallEvent {
    private List<MultipleItemEntity> multipleItemEntities;

    public MusicHallEvent(List<MultipleItemEntity> multipleItemEntities) {
        this.multipleItemEntities = multipleItemEntities;
    }

    public MusicHallEvent setMultipleItemEntities(List<MultipleItemEntity> multipleItemEntities) {
        this.multipleItemEntities = multipleItemEntities;
        return this;
    }

    public List<MultipleItemEntity> getMultipleItemEntities() {
        return multipleItemEntities;
    }
}
