package com.dev.rexhuang.zhiliao_core.net;

import com.dev.rexhuang.zhiliao_core.net.callback.IError;
import com.dev.rexhuang.zhiliao_core.net.callback.IFailure;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * *  created by RexHuang
 * *  on 2019/7/30
 */
public class RestClientBuilder {
    private String mUrl;

    private Map<String, Object> mParams;

    private Map<String, String> mHeaders;

    private IRequest mIRequest;

    private ISuccess mISuccess;

    private IFailure mIFailure;

    private IError mIError;

    private RequestBody mRequestBody;

    private Map<String, RequestBody> mRequestBodyMap;


    public final RestClientBuilder url(String url) {
        this.mUrl = url;
        return this;
    }

    public final RestClientBuilder params(String key, Object value) {
        if (mParams == null) {
            mParams = new HashMap<>();
        }
        mParams.put(key, value);
        return this;
    }

    public final RestClientBuilder params(Map<String, Object> params) {
        if (mParams == null) {
            mParams = new HashMap<>();
        }
        mParams.putAll(params);
        return this;
    }

    public final RestClientBuilder headers(String key, String value) {
        if (mHeaders == null) {
            mHeaders = new HashMap<>();
        }
        mHeaders.put(key, value);
        return this;
    }

    public final RestClientBuilder headers(Map<String, String> headers) {
        if (mHeaders == null) {
            mHeaders = new HashMap<>();
        }
        mHeaders.putAll(headers);
        return this;
    }

    public final RestClientBuilder raw(String json) {
        this.mRequestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json);
        return this;
    }

    public final RestClientBuilder request(IRequest iRequest) {
        this.mIRequest = iRequest;
        return this;
    }

    public final RestClientBuilder success(ISuccess iSuccess) {
        this.mISuccess = iSuccess;
        return this;
    }

    public final RestClientBuilder failure(IFailure iFailure) {
        this.mIFailure = iFailure;
        return this;
    }

    public final RestClientBuilder error(IError iError) {
        this.mIError = iError;
        return this;
    }

    public final RestClientBuilder body(RequestBody requestBody) {
        this.mRequestBody = requestBody;
        return this;
    }

    public final RestClientBuilder requestBodyMap(Map<String, RequestBody> requestBodyMap) {
        this.mRequestBodyMap = requestBodyMap;
        return this;
    }

    public RestClient build() {
        return new RestClient(mUrl, mParams, mHeaders, mIRequest, mISuccess, mIFailure, mIError, mRequestBody, mRequestBodyMap);
    }
}
