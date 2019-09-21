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
        if (checkEditorNotNull(editor)) {
            editor.putString(key, value);
        }
    }

    public void putStringSet(String key, Set<String> values) {
        if (checkEditorNotNull(editor)) {
            editor.putStringSet(key, values);
        }
    }

    public void putInt(String key, int value) {
        if (checkEditorNotNull(editor)) {
            editor.putInt(key, value);
        }
    }

    public void putLong(String key, long value) {
        if (checkEditorNotNull(editor)) {
            editor.putLong(key, value);
        }
    }

    public void putFloat(String key, float value) {
        if (checkEditorNotNull(editor)) {
            editor.putFloat(key, value);
        }
    }

    public void putBoolean(String key, boolean value) {
        if (checkEditorNotNull(editor)) {
            editor.putBoolean(key, value);
        }
    }

    public void remove(String key) {
        if (checkEditorNotNull(editor)) {
            editor.remove(key);
        }
    }

    public void clear() {
        if (checkEditorNotNull(editor)) {
            editor.clear();
        }
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

    public void commit() {
        if (checkEditorNotNull(editor)) {
            editor.commit();
        }
    }

    public void apply() {
        if (checkEditorNotNull(editor)) {
            editor.apply();
        }
    }

    private boolean checkEditorNotNull(SharedPreferences.Editor editor) {
        if (editor == null) {
            return false;
        }
        return true;
    }
}
