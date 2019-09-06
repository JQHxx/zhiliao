package com.dev.rexhuang.zhiliao_core.music_hall.adapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * *  created by RexHuang
 * *  on 2019/7/31
 */
public class MultipleItemEntity implements MultiItemEntity {

    //    private MultipleItemType type;
    private LinkedHashMap<Object, Object> datas;

    public MultipleItemEntity() {
        datas = new LinkedHashMap<>();
    }

    public void setField(Object key, Object value) {
        datas.put(key, value);
    }

    @Override
    public int getItemType() {
        return (int) datas.get(MultipleItemType.ITEM_TYPE);
    }

    public <T> T getField(MultipleItemType key) {
        return (T) datas.get(key);
    }

//    public void setType(MultipleItemType type) {
//        this.type = type;
//    }
}
