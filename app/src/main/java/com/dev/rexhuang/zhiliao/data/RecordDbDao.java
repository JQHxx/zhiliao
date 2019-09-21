package com.dev.rexhuang.zhiliao.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/9/19
 */
public class RecordDbDao {
    private Context context;
    private RecordSQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private final int DEFAULT_SIZE = 10;

    public RecordDbDao(Context context) {
        this.context = context;
        init();
    }

    private void init() {

        //实例化数据库SQLiteOpenHelper子类对象
        helper = new RecordSQLiteOpenHelper(context);
        // 第一次进入时查询所有的历史记录
        queryData("");
    }

    public List<String> queryData(String tempName) {
        List<String> data = new ArrayList<>();
        //模糊搜索
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc ", null);

        while (cursor.moveToNext()) {
            //注意这里的name跟建表的name统一
            String name = cursor.getString(cursor.getColumnIndex("name"));
            data.add(name);
        }
        cursor.close();
        return data;

    }

    /**
     * 检查数据库中是否已经有该条记录
     *
     * @param tempName
     * @return
     */
    private boolean hasData(String tempName) {
        //从Record这个表里找到name=tempName的id
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 插入数据
     *
     * @param tempName
     */
    public void insertData(String tempName) {
        if (!checkSize()) {
            deleteFirst();
        }
        if (hasData(tempName)){
            return;
        }
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 删除数据
     *
     * @param name
     * @return
     */
    public int delete(String name) {
        //获取数据
        SQLiteDatabase db = helper.getWritableDatabase();
        //执行SQL
        int delete = db.delete("records", "name=?", new String[]{name});
        db.close();
        return delete;
    }

    /**
     * 清空数据
     */
    public void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }


    private boolean checkSize() {
        List<String> tempRecord = queryData("");
        if (tempRecord.size() >= 15) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 删除第一条数据
     *
     * @return
     */
    private void deleteFirst() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records where id in (select id from records order by id limit 1)");
        db.close();
    }
}
