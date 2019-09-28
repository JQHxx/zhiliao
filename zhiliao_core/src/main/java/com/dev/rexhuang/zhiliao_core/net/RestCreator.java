package com.dev.rexhuang.zhiliao_core.net;

import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;

import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * *  created by RexHuang
 * *  on 2019/7/30
 */
public class RestCreator {

    public static WeakHashMap<String, Object> getParams() {
        return ParamsHolder.PARAMS;
    }

    private static final class ParamsHolder {
        public static final WeakHashMap<String, Object> PARAMS = new WeakHashMap<>();
    }

    private static final class RetrofitHolder {
        private static final String BASE_URL = Zhiliao.getConfig(ConfigKeys.API_HOST.name());
        private static final Retrofit RETROFIT_CLIENT = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OkHttpHolder.OK_HTTP_CLIENT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }


    private static final class OkHttpHolder {
        private static final int TIME_OUT = 60;
        private static final LoggingInterceptor interceptor = new LoggingInterceptor();
        private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)  //设置打印拦截日志
                .build();
    }

    private static final class RestServiceHolder {
        private static final RestService REST_SERVICE = RetrofitHolder.RETROFIT_CLIENT.create(RestService.class);
        private static final NeteaseRestService NETEASE_SERVICE = RetrofitHolder.RETROFIT_CLIENT.create(NeteaseRestService.class);
        private static final QQRestService QQ_SERVICE = RetrofitHolder.RETROFIT_CLIENT.create(QQRestService.class);
    }

    public static RestService getRestService() {
        return RestServiceHolder.REST_SERVICE;
    }

    public static NeteaseRestService getNeteaseRestService() {
        return RestServiceHolder.NETEASE_SERVICE;
    }

    public static QQRestService getQQRestService() {
        return RestServiceHolder.QQ_SERVICE;
    }

    public static final OkHttpClient getOkHttpClient() {
        return OkHttpHolder.OK_HTTP_CLIENT;
    }

}
