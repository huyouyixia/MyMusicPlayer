package com.example.mymusicplayer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by wentaodeng on 2017/12/13.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    //需要静态，不然容易出现没有播放退出之后再次进入不搜索数据库
    private SQLiteDatabase db;
    private final static String TAG = "MyDatabaseHelper";
    private static final String CREATE_MUSIC = "create table Music("+
            "id integer primary key autoincrement,"
            +" musicName text,"
            +" musicPath text,musicLength text);";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name
            , SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MUSIC);
        Toast.makeText(mContext,"创建数据库成功",Toast.LENGTH_SHORT).show();
        db = sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Music");
        onCreate(sqLiteDatabase);
    }
    public String getSelected(SQLiteDatabase db ,String sql){
        Cursor cursor =db.rawQuery(sql,null);
        String result ="";
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            result += cursor.getString(cursor.getColumnIndex("musicPath")) + "\n";
            Log.d(TAG , "addMusicTableP: 查询数据库musicPath=" + result);
        }
        cursor.close();
        return result;
    }
    public void insertMydb(List<File> musicList){
        //数据库
        db = this.getWritableDatabase();
        //添加数据到数据库
        for(File f: musicList){
            db.execSQL("insert into Music(musicName,musicPath,musicLength) values(?,?,?) "
                    ,new String[]{f.getName().toString(),f.getPath().toString(), f.length()+""});
            Log.d(TAG, "insertMydb: " + f.getName());
        }
    }
}
