package com.dev.rexhuang.zhiliao_core.api;

import android.widget.Toast;

import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.net.callback.IError;
import com.dev.rexhuang.zhiliao_core.net.callback.IFailure;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public abstract class BaseApi {
    protected static IError iError = new IError() {
        @Override
        public void onError(int code, String message) {
            Toast.makeText(Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name()),
                    "网络请求错误: 错误码 : " + code + " 错误信息 : " + message, Toast.LENGTH_LONG).show();
        }
    };

    protected static IRequest iRequest = new IRequest() {
        @Override
        public void onRequestStart() {

        }

        @Override
        public void onRequestEnd() {

        }
    };

    protected static ISuccess<String> iSuccess = new ISuccess<String>() {
        @Override
        public void onSuccess(String response) {
            Toast.makeText(Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name()), response, Toast.LENGTH_SHORT).show();
        }
    };

    protected static IFailure iFailure = new IFailure() {
        @Override
        public void onFailure() {
            Toast.makeText(Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name()),
                    "网络请求失败,请检查网络是否可用!", Toast.LENGTH_LONG).show();
        }
    };
}
