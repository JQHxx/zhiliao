package com.dev.rexhuang.zhiliao_core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * Created by RexHuang on 2018/12/27.
 */

public class SharedPreferenceHelper {

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor editor;


    public SharedPreferenceHelper(Context context, String name, int mode) {
        this.sharedPreferences = SharedPreferenceHelper.getSharedPreference(context, name, mode);
        this.editor = sharedPreferences.edit();
    }

    public SharedPreferenceHelper(Activity activity, int mode) {
        this.sharedPreferences = SharedPreferenceHelper.getSharedPreference(activity, mode);
        this.editor = sharedPreferences.edit();
    }

    public SharedPreferenceHelper(Context context) {
        this.sharedPreferences = SharedPreferenceHelper.getSharedPreference(context);
        this.editor = sharedPreferences.edit();
    }

    /**
     * Context类的getSharedPreference，存放在/data/data/<package name>/shared_prefs/目录下
     *
     * @param context 上下文
     * @param name    sharedpreference的文件名
     * @param mode    模式，只有MODE_PRIVATE可选，表示只有当前的应用程序才可以对该文件进行读写
     * @return
     */
    private static SharedPreferences getSharedPreference(Context context, String name, int mode) {
        return context.getSharedPreferences(name, mode);
    }

    /**
     * Activity类的getPreferences,存放在/data/data/<package name>/shared_prefs/目录下,文件名为类名
     *
     * @param activity 当前activity
     * @param mode     模式，只有MODE_PRIVATE可选，表示只有当前的应用程序才可以对该文件进行读写
     * @return
     */
    private static SharedPreferences getSharedPreference(Activity activity, int mode) {
        return activity.getPreferences(mode);
    }

    /**
     * PreferenceManager类的getDefaultSharedPreferences,存放在/data/data/<package name>/shared_prefs/目录下,文件名为包名，模式为
     * MODE_PRIVATE
     *
     * @param context 上下文
     * @return
     */
    private static SharedPreferences getSharedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putStringSet(String key, Set<String> values) {
        editor.putStringSet(key, values);
        editor.commit();
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    public void clear(String name) {
        editor.clear();
        editor.commit();
        //todo 删除文件
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public Set<String> getStringSet(String key, Set<String> defValues) {
        return sharedPreferences.getStringSet(key, defValues);
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }


}
