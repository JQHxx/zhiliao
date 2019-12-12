package com.dev.rexhuang.zhiliao.login;

import android.content.Context;
import android.text.TextUtils;

import com.dev.rexhuang.zhiliao_core.entity.User;
import com.dev.rexhuang.zhiliao_core.utils.SharedPreferenceHelper;

/**
 * *  created by RexHuang
 * *  on 2019/9/20
 */
public class UserManager {
    private SharedPreferenceHelper mSharedPreferenceHelper;
    private static volatile UserManager sInstance;
    private static Context sContext;
    private static final String ID = "id";
    private static final String EMAIL = "email";
    private static final String PHONENUMBER = "phoneNumber";
    private static final String AVATAR = "avatar";
    private static final String NICKNAME = "nickname";
    private static final String STATUS = "status";
    private static final String JOIN_TIME = "join_time";
    private static final String FACTORY = "factory";
    private static final String CICADA = "cicada";
    private static final String TOKEN = "token";
    private static final String TOKEN_EXPIRE = "token_expired_at";

    public static void init(Context context) {
        sContext = context;
    }

    public static UserManager getInstance() {
        if (sInstance == null) {
            synchronized (UserManager.class) {
                if (sInstance == null) {
                    sInstance = new UserManager(sContext);
                }
            }
        }
        return sInstance;
    }

    private UserManager(Context context) {
        mSharedPreferenceHelper = new SharedPreferenceHelper(context);
    }

    public String getToken() {
        if (mSharedPreferenceHelper == null) {
            return null;
        }
        return getSharePreferences().getString("token", null);
    }

    public User getUser() {
        User user = new User();
        User.DataEntity dataEntity = user.getData();
        user.setCode(0);
        dataEntity.setId(getSharePreferences().getString(ID, null));
        dataEntity.setEmail(getSharePreferences().getString(EMAIL, null));
        dataEntity.setPhonenumber(getSharePreferences().getString(PHONENUMBER, null));
        dataEntity.setAvatar(getSharePreferences().getString(AVATAR, null));
        dataEntity.setNickname(getSharePreferences().getString(NICKNAME, null));
        dataEntity.setStatus(getSharePreferences().getString(STATUS, null));
        dataEntity.setJoin_time(getSharePreferences().getString(JOIN_TIME, null));
        dataEntity.setFactory(getSharePreferences().getString(FACTORY, null));
        dataEntity.setCicada(getSharePreferences().getString(CICADA, null));
        dataEntity.setToken(getSharePreferences().getString(TOKEN, null));
        dataEntity.setToken_expired_at(getSharePreferences().getString(TOKEN_EXPIRE, null));
        return user;
    }

    public boolean putUser(User user) {
        if (user == null || user.getData() == null) {
            return false;
        }
        User.DataEntity userData = user.getData();
        getSharePreferences().putString(ID, userData.getId());
        getSharePreferences().putString(EMAIL, userData.getEmail());
        getSharePreferences().putString(PHONENUMBER, userData.getPhonenumber() != null ? userData.getPhonenumber().toString() : null);
        getSharePreferences().putString(AVATAR, userData.getAvatar());
        getSharePreferences().putString(NICKNAME, userData.getNickname());
        getSharePreferences().putString(STATUS, userData.getStatus());
        getSharePreferences().putString(JOIN_TIME, userData.getJoin_time());
        getSharePreferences().putString(FACTORY, userData.getFactory());
        getSharePreferences().putString(CICADA, userData.getCicada());
        getSharePreferences().putString(TOKEN, userData.getToken());
        getSharePreferences().putString(TOKEN_EXPIRE, userData.getToken_expired_at());
        getSharePreferences().apply();
        return true;
    }

    public boolean putToken(String token, String token_expired_at) {
        if (TextUtils.isEmpty(token)) {
            return false;
        }
        getSharePreferences().putString(TOKEN, token);
        getSharePreferences().putString(TOKEN_EXPIRE, token_expired_at);
        getSharePreferences().apply();
        return true;
    }

    private SharedPreferenceHelper getSharePreferences() {
        if (mSharedPreferenceHelper == null) {
            mSharedPreferenceHelper = new SharedPreferenceHelper(sContext);
        }
        return mSharedPreferenceHelper;
    }

    public boolean isLogined() {
        if (!TextUtils.isEmpty(getSharePreferences().getString(ID, null))) {
            return true;
        }
        return false;
    }
}
