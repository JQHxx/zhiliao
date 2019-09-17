package com.dev.rexhuang.zhiliao.event;

import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/9/12
 */
public class MusicHallEvent {
    public final List<SongListEntity.DataEntity> dataEntities;

    public MusicHallEvent(List<SongListEntity.DataEntity> dataEntities) {
        this.dataEntities = dataEntities;
    }

}
