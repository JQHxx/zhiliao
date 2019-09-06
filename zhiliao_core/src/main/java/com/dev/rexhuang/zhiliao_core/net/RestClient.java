package com.dev.rexhuang.zhiliao_core.net;

import com.dev.rexhuang.zhiliao_core.net.callback.IError;
import com.dev.rexhuang.zhiliao_core.net.callback.IFailure;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.net.callback.RequestCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * *  created by RexHuang
 * *  on 2019/7/30
 */
public class RestClient {

    private final String URL;

    private final Map<String, Object> PARAMS = new HashMap<>();

    private final Map<String, String> HEADERS = new HashMap<>();

    private final IRequest IREQUEST;

    private final ISuccess ISUCCESS;

    private final IFailure IFAILURE;

    private final IError IERROR;

    private final RequestBody REQUESTBODY;

    private final Map<String, RequestBody> REQUESTBODYMAP;

    public RestClient(String url, Map<String, Object> params, Map<String, String> headers,
                      IRequest iRequest, ISuccess iSuccess, IFailure iFailure,
                      IError iError,
                      RequestBody requestBody, Map<String, RequestBody> requestBodyMap) {
        URL = url;
        if (params != null && params.size() > 0) {
            PARAMS.putAll(params);
        }
        if (headers != null && headers.size() > 0) {
            HEADERS.putAll(headers);
        }
        IREQUEST = iRequest;
        ISUCCESS = iSuccess;
        IFAILURE = iFailure;
        IERROR = iError;
        REQUESTBODY = requestBody;
        REQUESTBODYMAP = requestBodyMap;
    }

    public static RestClientBuilder builder() {
        return new RestClientBuilder();
    }

    @SuppressWarnings("unchecked")
    private void request(HttpMethod method) {
        final RestService service = RestCreator.getRestService();
        Call call = null;
        if (IREQUEST != null) {
            IREQUEST.onRequestStart();
        }

        switch (method) {
            case GET:
                call = service.get(URL, PARAMS, HEADERS);
                break;
            case POST:
                call = service.post(URL, PARAMS);
                break;
            case POST_RAW:
                break;
            case PUT:
                call = service.put(URL, HEADERS, REQUESTBODY);
                break;
            case PUT_RAW:
                break;
            case DELETE:
                call = service.delete(URL, HEADERS, PARAMS);
                break;
            case UPLOAD:
                break;
            case DOWNLOAD:
                break;
            case SIGNIN:
                call = service.signIn(URL, HEADERS, REQUESTBODY);
                break;
            case PROFILE:
                call = service.profile(URL, HEADERS);
                break;
            case USER:
                call = service.user(URL, HEADERS, REQUESTBODYMAP);
                break;
            case MUSICBILLLIST:
                call = service.musicbillList(URL, HEADERS);
                break;
            case MUSICBILL:
                call = service.musicbill(URL, HEADERS);
                break;
            case CREATEMUSICBILL:
                call = service.createMusicbill(URL, HEADERS, REQUESTBODY);
                break;
            case DELMUSICBILL:
                call = service.deleteMusicbill(URL, HEADERS);
                break;
            case ADDMUSICBILLMUSIC:
                call = service.addMusicbillMusic(URL, HEADERS, REQUESTBODY);
                break;
            case UPDATEMUSICBILL:
                call = service.updateMusicbillMusic(URL, HEADERS, REQUESTBODYMAP);
                break;
            case DELETEMUSICBILLMUSIC:
                call = service.deleteMusicbillMusic(URL, HEADERS, REQUESTBODY);
                break;
            case GETMUSIC:
                call = service.getMusic(URL, HEADERS);
                break;
            case GETLYRIC:
                call = service.getLyric(URL, HEADERS);
                break;
            case GETSINGER:
                call = service.getSinger(URL, HEADERS);
                break;
            case VERIFY:
                call = service.getVerify(URL, HEADERS);
                break;
            default:
                break;
        }

        if (call != null) {
            call.enqueue(getRequestCallback());
        }
    }

    public void get() {
        request(HttpMethod.GET);
    }

    public void getSearchList() {
        request(HttpMethod.SONGSEARCH);
    }

    public void post() {
        request(HttpMethod.POST);
    }

    public void put() {
        request(HttpMethod.PUT);
    }

    public void delete() {
        request(HttpMethod.DELETE);
    }

    public void signIn() {
        request(HttpMethod.SIGNIN);
    }

    public void profile() {
        request(HttpMethod.PROFILE);
    }

    public void user() {
        request(HttpMethod.USER);
    }

    public void musicbillList() {
        request(HttpMethod.MUSICBILLLIST);
    }

    public void musicbill() {
        request(HttpMethod.MUSICBILL);
    }

    public void createMusicbill() {
        request(HttpMethod.CREATEMUSICBILL);
    }

    public void delMusicbill() {
        request(HttpMethod.DELMUSICBILL);
    }

    public void addMusicbillMusic() {
        request(HttpMethod.ADDMUSICBILLMUSIC);
    }

    public void updateMusicbill() {
        request(HttpMethod.UPDATEMUSICBILL);
    }

    public void delMusicbillMusic() {
        request(HttpMethod.DELETEMUSICBILLMUSIC);
    }

    public void getMusic() {
        request(HttpMethod.GETMUSIC);
    }

    public void getLyric() {
        request(HttpMethod.GETLYRIC);
    }

    public void getSinger() {
        request(HttpMethod.GETSINGER);
    }

    public void getVerify() {
        request(HttpMethod.VERIFY);
    }

    private Callback getRequestCallback() {
        return new RequestCallback(IREQUEST, ISUCCESS, IFAILURE, IERROR);
    }


}
