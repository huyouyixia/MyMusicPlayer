package com.example.mymusicplayer;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wentaodeng on 2017/12/11.
 */

public class Music {
    public List<File> musicList = new ArrayList<>();
    private long musicL = 3 * 1024 * 1024;
    private final static String TAG = "Music";
    private String musicName;

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    /*
    方法一：File类递归
     */
    public List<File> getMusicList() {
        musicList = new ArrayList<>();
        File[] allDirectory = Environment.getExternalStorageDirectory().listFiles();
        Log.d(TAG, "getMusicList: allDirectory=" + allDirectory);
        nextDirectory(allDirectory);
        return musicList;
    }

    private void nextDirectory(File[] allDirectory) {

        for (File f : allDirectory) {
//            Log.d(TAG, "nextDirectory: " + f.getName());
            if (f.getAbsolutePath().endsWith(".mp3") && f.length() > musicL) {
                musicList.add(f);
            } else if (!f.isHidden() && f.isDirectory() && f.getName().length() > 2) {
                File[] nextDirec = f.listFiles();
                nextDirectory(nextDirec);
            }
        }
    }
    public AlertDialog setDialog(Context context, String title, String message
            , boolean cancelable){
        AlertDialog builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancelable)
                .setNegativeButton("取消",(d,i)->{
                }).create();
        return builder;
    }
    /*
    方法二：使用SimpleFileVisitor遍历,Android竟然不支持Files这个接口？？
     */


}
