package com.dev.rexhuang.zhiliao_core.net;

import com.dev.rexhuang.zhiliao_core.entity.LyricEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongSearchEntity;
import com.dev.rexhuang.zhiliao_core.entity.User;
import com.google.gson.Gson;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * *  created by RexHuang
 * *  on 2019/7/30
 */
public interface RestService {

    @GET
    Call<String> get(@Url String url, @QueryMap Map<String, Object> params, @HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @POST
    Call<String> post(@Url String url, @FieldMap Map<String, Object> params);

//    @Multipart
//    @PUT
//    Call<String> put(@Url String url, @HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params);

    @PUT
    Call<String> put(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody body);

    @DELETE
    Call<String> delete(@Url String url, @HeaderMap Map<String, String> headers, @QueryMap Map<String, Object> params);

    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url, @QueryMap Map<String, Object> params);

    @Multipart
    @POST
    Call<String> upload(@Url String url, @Part MultipartBody.Part file);

//    @GET
//    Call<SongSearchEntity> getSongSearchList(@Url String url, @QueryMap Map<String, Object> params, @HeaderMap Map<String, String> headers);

    @POST
    Call<User> signIn(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody body);

    @GET
    Call<User> profile(@Url String url, @HeaderMap Map<String, String> headers);

//    @Multipart
//    @PUT
//    Call<String> user(@Url String url, @HeaderMap Map<String, String> headers, @Part("id") RequestBody id, @Part("key") RequestBody key, @Part("value") RequestBody value);
//
//    @PUT
//    Call<String> user(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody body);

    @Multipart
    @PUT
    Call<String> user(@Url String url, @HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params);

    @GET
    Call<SongListEntity> musicbillList(@Url String url, @HeaderMap Map<String, String> headers);

    @GET
    Call<String> musicbill(@Url String url, @HeaderMap Map<String, String> headers);

    @POST
    Call<String> createMusicbill(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody body);

    @DELETE
    Call<String> deleteMusicbill(@Url String url, @HeaderMap Map<String, String> headers);

    @POST
    Call<String> addMusicbillMusic(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody body);

    @Multipart
    @PUT
    Call<String> updateMusicbillMusic(@Url String url, @HeaderMap Map<String, String> headers, @PartMap Map<String, RequestBody> params);

    @DELETE
    Call<String> deleteMusicbillMusic(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody body);

    @GET
    Call<SongSearchEntity> getMusic(@Url String url, @HeaderMap Map<String, String> headers);

    @GET
    Call<LyricEntity> getLyric(@Url String url, @HeaderMap Map<String, String> headers);

    @GET
    Call<String> getSinger(@Url String url, @HeaderMap Map<String, String> headers);

    @GET
    Call<String> getVerify(@Url String url, @HeaderMap Map<String, String> headers);
}
