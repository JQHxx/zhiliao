package com.dev.rexhuang.zhiliao_core.api.qq;

import com.dev.rexhuang.zhiliao_core.api.BaseApi;
import com.dev.rexhuang.zhiliao_core.entity.ArtistsDataInfo;
import com.dev.rexhuang.zhiliao_core.entity.ArtistsParams;
import com.dev.rexhuang.zhiliao_core.net.QQRestService;
import com.dev.rexhuang.zhiliao_core.net.RestCreator;
import com.dev.rexhuang.zhiliao_core.net.callback.IError;
import com.dev.rexhuang.zhiliao_core.net.callback.IFailure;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.google.gson.Gson;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public class QQMusicApi extends BaseApi {

    private static final QQRestService QQ_SERVICE = RestCreator.getQQRestService();

    @SuppressWarnings("ConstantConditions")
    public static void getArtistList(int offset, HashMap<String, Integer> map, IRequest request, ISuccess success, IError error, IFailure failure) {
        ArtistsParams artistsParams = new ArtistsParams(new ArtistsParams.CommEntity(24, 0)
                , new ArtistsParams.SingerListEntity("get_singer_list", "Music.SingerListServer",
                new ArtistsParams.SingerListEntity.ParamEntity(map.get("area") != null ? map.get("area") : -100,
                        map.get("sex") != null ? map.get("sex") : -100,
                        map.get("genre") != null ? map.get("genre") : -100,
                        map.get("index") != null ? map.get("index") : -100, offset * 80, offset + 1)));
        String url = "https://u.y.qq.com/cgi-bin/musicu.fcg?";
        Observable observable = QQ_SERVICE.getQQArtists(url, new Gson().toJson(artistsParams));
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArtistsDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArtistsDataInfo artistsDataInfo) {
                        if (success != null) {
                            success.onSuccess(artistsDataInfo);
                        }
                    }


                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
