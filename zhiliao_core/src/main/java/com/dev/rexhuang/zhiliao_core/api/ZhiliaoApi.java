package com.dev.rexhuang.zhiliao_core.api;

import android.widget.Toast;

import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.net.RestClient;
import com.dev.rexhuang.zhiliao_core.net.callback.IError;
import com.dev.rexhuang.zhiliao_core.net.callback.IFailure;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * *  created by RexHuang
 * *  on 2019/8/30
 */
public class ZhiliaoApi {
    //field
    private static IError iError = new IError() {
        @Override
        public void onError(int code, String message) {
            Toast.makeText(Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name()),
                    "网络请求错误: 错误码 : " + code + " 错误信息 : " + message, Toast.LENGTH_LONG).show();
        }
    };

    private static IRequest iRequest = new IRequest() {
        @Override
        public void onRequestStart() {

        }

        @Override
        public void onRequestEnd() {

        }
    };

    private static ISuccess<String> iSuccess = new ISuccess<String>() {
        @Override
        public void onSuccess(String response) {
            Toast.makeText(Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name()), response, Toast.LENGTH_SHORT).show();
        }
    };

    private static IFailure iFailure = new IFailure() {
        @Override
        public void onFailure() {
            Toast.makeText(Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name()),
                    "网络请求失败,请检查网络是否可用!", Toast.LENGTH_LONG).show();
        }
    };

    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENTTYPE = "Content-Type";
    private static final String MEDIAJSON = "application/json";
    private static final String MEDIATEXT = "text/plain";


    /**
     * 登录
     *
     * @param contact     用户邮箱
     * @param verify_code 用户验证码
     * @throws JSONException JSONException
     */
    public static void signIn(String contact, String verify_code, IRequest request, ISuccess success, IFailure failure, IError error) throws JSONException {
        String url = "https://engine.mebtte.com/1/user/signin";
        HashMap<String, String> headers = new HashMap<>();
        headers.put(CONTENTTYPE, MEDIAJSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("contact", contact);
        jsonObject.put("verify_code", verify_code);
        Logger.t("ZhiliaoApi").d(jsonObject.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIAJSON), jsonObject.toString());
        getRestClient(url, headers, requestBody, null, request, success, failure, error).signIn();
    }

    /**
     * 获取用户资料
     *
     * @param token token
     */
    public static void profile(String token, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "https://engine.mebtte.com/1/user/profile";
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        getRestClient(url, headers, null, null, request, success, failure, error).profile();
    }

    /**
     * 更新用户资料
     *
     * @param token token
     * @param id    用户id
     * @param key   用户字段
     * @param value 用户字段值
     * @throws JSONException JSONException
     */
    public static void user(String token, String id, String key, String value, IRequest request, ISuccess success, IFailure failure, IError error) throws JSONException {
        String url = "https://engine.mebtte.com/1/user";
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        HashMap<String, RequestBody> requestBodyHashMap = new HashMap<>();
        requestBodyHashMap.put("id", RequestBody.create(MediaType.parse(MEDIATEXT), id));
        requestBodyHashMap.put("key", RequestBody.create(MediaType.parse(MEDIATEXT), key));
        requestBodyHashMap.put("value", RequestBody.create(MediaType.parse(MEDIATEXT), value));
        getRestClient(url, headers, null, requestBodyHashMap, request, success, failure, error).user();
    }

    //musicbill

    /**
     * 获取用户歌单列表
     *
     * @param token token
     */
    public static void musicbillList(String token, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "https://engine.mebtte.com/1/musicbill/list";
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        getRestClient(url, headers, null, null, request, success, failure, error).musicbillList();
    }

    /**
     * 获取歌单详情
     *
     * @param token        token
     * @param musicbill_id 歌单id
     * @throws JSONException JSONException
     */
    public static void musicbill(String token, String musicbill_id, IRequest request, ISuccess success, IFailure failure, IError error) throws JSONException {
        String url = "https://engine.mebtte.com/1/musicbill?id=" + musicbill_id;
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        getRestClient(url, headers, null, null, request, success, failure, error).musicbill();
    }

    /**
     * 创建歌单
     *
     * @param token token
     * @param name  歌单名
     * @throws JSONException JSONException
     */
    public static void createMusicbill(String token, String name, IRequest request, ISuccess success, IFailure failure, IError error) throws JSONException {
        String url = "https://engine.mebtte.com/1/musicbill";
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        headers.put(CONTENTTYPE, MEDIAJSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIAJSON), jsonObject.toString());
        getRestClient(url, headers, requestBody, null, request, success, failure, error).createMusicbill();
    }

    /**
     * 删除歌单
     *
     * @param token        token
     * @param musicbill_id 歌单id
     * @throws JSONException JSONException
     */
    public static void delMusicbill(String token, String musicbill_id, IRequest request, ISuccess success, IFailure failure, IError error) throws JSONException {
        String url = "https://engine.mebtte.com/1/musicbill?id=" + musicbill_id;
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        getRestClient(url, headers, null, null, request, success, failure, error).delMusicbill();
    }

    /**
     * 添加音乐到歌单
     *
     * @param token        token
     * @param musicbill_id 歌单id
     * @param music_id     歌曲id
     * @throws JSONException JSONException
     */
    public static void addMusicbillMusic(String token, String musicbill_id, String music_id, IRequest request, ISuccess success, IFailure failure, IError error) throws JSONException {
        String url = "https://engine.mebtte.com/1/musicbill/music";
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        headers.put(CONTENTTYPE, MEDIAJSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("musicbill_id", musicbill_id);
        jsonObject.put("music_id", music_id);
        Logger.t("ZhiliaoApi").d(jsonObject.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIAJSON), jsonObject.toString());
        getRestClient(url, headers, requestBody, null, request, success, failure, error).addMusicbillMusic();
    }

    /**
     * 更新歌单
     *
     * @param token token
     * @param id    歌单id
     * @param key   歌单字段
     * @param value 歌单字段值
     */
    public static void updateMusicbill(String token, String id, String key, String value, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "https://engine.mebtte.com/1/musicbill";
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        HashMap<String, RequestBody> requestBodyMap = new HashMap<>();
        requestBodyMap.put("id", RequestBody.create(MediaType.parse(MEDIATEXT), id));
        requestBodyMap.put("key", RequestBody.create(MediaType.parse(MEDIATEXT), key));
        requestBodyMap.put("value", RequestBody.create(MediaType.parse(MEDIATEXT), value));
        getRestClient(url, headers, null, requestBodyMap, request, success, failure, error).updateMusicbill();
    }

    /**
     * 从歌单删除音乐
     *
     * @param token        token
     * @param musicbill_id 歌单id
     * @param music_id     歌曲id
     * @throws JSONException JSONException
     */
    public static void delMusicbillMusic(String token, String musicbill_id, String music_id, IRequest request, ISuccess success, IFailure failure, IError error) throws JSONException {
        String url = "https://engine.mebtte.com/1/musicbill/music";
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        headers.put(CONTENTTYPE, MEDIAJSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("musicbill_id", musicbill_id);
        jsonObject.put("music_id", music_id);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        getRestClient(url, headers, requestBody, null, request, success, failure, error).delMusicbillMusic();
    }

    /**
     * 上传用户听歌记录
     *
     * @param token    token
     * @param music_id 歌曲id
     * @param percent  记录
     * @throws JSONException JSONException
     */
    public static void uploadPlayLog(String token, String music_id, String percent, IRequest request, ISuccess success, IFailure failure, IError error) throws JSONException {
        String url = "https://engine.mebtte.com/1/music/play_log";
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        headers.put(CONTENTTYPE, MEDIAJSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("music_id", music_id);
        jsonObject.put("percent", percent);
        RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIAJSON), jsonObject.toString());
        getRestClient(url, headers, requestBody, null, request, success, failure, error).signIn();
    }

    /**
     * 上传用户下载记录
     *
     * @param token    token
     * @param music_id 歌曲id
     * @param type     normal-普通音质 accompany-伴奏 hq-超高音质
     * @throws JSONException JSONException
     */
    public static void uploadDownloadLog(String token, String music_id, String type, IRequest request, ISuccess success, IFailure failure, IError error) throws JSONException {
        String url = "https://engine.mebtte.com/1/music/download_log";
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        headers.put(CONTENTTYPE, MEDIAJSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", music_id);
        jsonObject.put("type", type);
        RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIAJSON), jsonObject.toString());
        getRestClient(url, headers, requestBody, null, request, success, failure, error).signIn();
    }

    /**
     * 获取音乐
     *
     * @param token token
     * @param key   key
     * @param value value
     */
    public static void getMusic(String token, String key, String value, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "https://engine.mebtte.com/1/music?key=" + key + "&" + "value=" + value;
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        getRestClient(url, headers, null, null, request, success, failure, error).getMusic();
    }

    /**
     * 获取歌词
     *
     * @param token token
     * @param id    歌曲id
     */
    public static void getlyric(String token, String id, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "https://engine.mebtte.com/1/music/lyric?id=" + id;
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        getRestClient(url, headers, null, null, request, success, failure, error).getLyric();

    }

    /**
     * 获取歌手信息
     *
     * @param token token
     * @param id    歌手id
     */
    public static void getSinger(String token, String id, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "https://engine.mebtte.com/1/figure?id=" + id;
        HashMap<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION, token);
        getRestClient(url, headers, null, null, request, success, failure, error).getSinger();
    }

    /**
     * 获取验证码
     *
     * @param contact 验证邮箱
     * @param signin  验证码类型 signin
     */
    public static void getVerify(String contact, String signin, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "https://engine.mebtte.com/1/verify_code?contact=" + contact + "&" + "type=" + signin;
        getRestClient(url, null, null, null, request, success, failure, error).getVerify();
    }

    /**
     * 返回构建好的RestClient实例
     *
     * @param url            请求url
     * @param headers        请求首部
     * @param requestBody    请求主体
     * @param requestBodyMap 请求主体的Map形式
     * @param request        请求中回调
     * @param success        请求成功回调
     * @param failure        请求失败回调
     * @param error          请求错误回调
     * @return RestClient实例
     */
    @SuppressWarnings("SameParameterValue")
    private static RestClient getRestClient(String url, Map<String, String> headers, RequestBody requestBody,
                                            Map<String, RequestBody> requestBodyMap,
                                            IRequest request, ISuccess success, IFailure failure, IError error) {
        if (url == null || url.length() <= 0) {
            throw new RuntimeException("网络请求的url不能为空");
        }
        return RestClient.builder()
                .url(url)
                .headers(headers)
                .body(requestBody)
                .requestBodyMap(requestBodyMap)
                .request(request != null ? request : iRequest)
                .success(success != null ? success : iSuccess)
                .error(error != null ? error : iError)
                .failure(failure != null ? failure : iFailure)
                .build();
    }
}
