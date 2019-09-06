package com.dev.rexhuang.zhiliao_core.net.callback;

import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongSearchEntity;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * *  created by RexHuang
 * *  on 2019/7/30
 */
public interface ISuccess<T> {

    void onSuccess(T response);

//    void onSuccess(String response);
//
//    void onSuccess(SongListEntity songListEntity);
//
//    void onSuccess(SongSearchEntity songSearchEntity);


}
