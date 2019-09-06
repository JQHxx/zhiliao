package com.dev.rexhuang.zhiliao_core.net.callback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * *  created by RexHuang
 * *  on 2019/7/30
 */
@SuppressWarnings({"NullableProblems","unchecked"})
public class RequestCallback<T> implements Callback<T> {

    private IRequest mIRequest;

    private ISuccess mISuccess;

    private IFailure mIFailure;

    private IError mIError;

    public RequestCallback(IRequest request, ISuccess success, IFailure failure, IError error) {
        this.mIRequest = request;
        this.mISuccess = success;
        this.mIFailure = failure;
        this.mIError = error;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
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
    public void onFailure(Call<T> call, Throwable t) {
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
