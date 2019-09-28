package com.dev.rexhuang.zhiliao_core.api.musiclake;

import android.widget.Toast;

import com.dev.rexhuang.zhiliao_core.api.BaseApi;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.BannerEntity;
import com.dev.rexhuang.zhiliao_core.net.NeteaseRestService;
import com.dev.rexhuang.zhiliao_core.net.RestCreator;
import com.dev.rexhuang.zhiliao_core.net.callback.IError;
import com.dev.rexhuang.zhiliao_core.net.callback.IFailure;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.orhanobut.logger.Logger;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public class MusicLakeApi extends BaseApi {
    private static final NeteaseRestService neteaseRestService = RestCreator.getNeteaseRestService();

    public static void getBanner(IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "http://musiclake.leanapp.cn/banner";
        Observable observable = neteaseRestService.getBanner(url);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BannerEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BannerEntity bannerEntity) {
                        if (success != null) {
                            success.onSuccess(bannerEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("onError");
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("onComplete");
                    }
                });
    }
}
