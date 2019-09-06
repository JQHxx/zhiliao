package com.dev.rexhuang.zhiliao_core.net.callback;

import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongSearchEntity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * *  created by RexHuang
 * *  on 2019/7/30
 */
public class RequestSongSearchListCallback implements Callback<SongSearchEntity> {

    private IRequest mIRequest;

    private ISuccess mISuccess;

    private IFailure mIFailure;

    private IError mIError;

    public RequestSongSearchListCallback(IRequest request, ISuccess success, IFailure failure, IError error) {
        this.mIRequest = request;
        this.mISuccess = success;
        this.mIFailure = failure;
        this.mIError = error;
    }

    @Override
    public void onResponse(Call<SongSearchEntity> call, Response<SongSearchEntity> response) {
        if (response.isSuccessful()) {
            if (call.isExecuted()) {
                if (mISuccess != null) {
                    mISuccess.onSuccess(response.body());
                }
            }
        } else {
            if (mIError != null) {
                mIError.onError(response.code(), response.message());
            }
        }
        requestEnd();
    }

    @Override
    public void onFailure(Call<SongSearchEntity> call, Throwable t) {
        if (mIFailure != null) {
            mIFailure.onFailure();
        }
        requestEnd();
    }


    public void requestEnd() {
        if (mIRequest != null) {
            mIRequest.onRequestEnd();
        }
    }
}
