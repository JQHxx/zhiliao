package com.dev.rexhuang.zhiliao.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * *  created by RexHuang
 * *  on 2019/9/19
 */
public class RecordSQLiteOpenHelper extends SQLiteOpenHelper {
    private static String name = "record.db";
    private static Integer version = 1;

    public RecordSQLiteOpenHelper(@Nullable Context context) {
        super(context, name, null, version);
    }

    public RecordSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //打开数据库，建立了一个叫records的表，里面只有一列name来存储历史记录：
        db.execSQL("create table records(id integer primary key autoincrement,name varchar(200))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
