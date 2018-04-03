package com.example.mymusicplayer;

import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by v_wtdeng on 2018/4/2.
 */

public class TestCreateFile {
    private Calendar calendar;
    private String TAG = "TestCreateFile";
    private UiDevice device;
    private FileOutputStream bw;
    public TestCreateFile(){}
    public TestCreateFile(UiDevice device){
        this.device = device;
    }
    public boolean createDirectory(String path){
        File directory = new File(path);
        if(!directory.exists()){
            directory.mkdirs();
        }
        Log.d(TAG, "createDirectory: 是文件夹？" + directory.isDirectory());
        Log.d(TAG, "createDirectory: 是文件？"+ directory.isFile());
        return directory.exists();
    }
    public boolean deleteDirectory(String path){
       File file = new File(path);
       File[] files;
       if(file.isDirectory()){
           files = file.listFiles();
           for(int i=0; i<files.length; i++){
               deleteDirectory(files[i].toString());
           }
       }
       //要先删除文件夹里的文件才能删除文件夹
       file.delete();
       return !file.exists();
    }
    public boolean saveShotScreen(String pathAndName){
        //如果不存在则创建文件夹
        createDirectory(pathAndName.substring(0,pathAndName.lastIndexOf("/")));
        return createJpgFile(pathAndName).exists();
    }
    protected File createJpgFile(String pathAndName){
        if(device==null){
            throw new NullPointerException("device 为空，请检查构造器中是否输入了UiDevice对象");
        }
        File jpg = new File(pathAndName);
        //保存截图
        device.takeScreenshot(jpg);
        return jpg;
    }
    public boolean saveShotScreen(){
        //默认当前时间为文件名
        calendar = Calendar.getInstance(Locale.CHINESE);
        calendar.setTimeInMillis(System.currentTimeMillis());
        String defaultName = calendar.get(Calendar.YEAR)+"-" +
                (calendar.get(Calendar.MONTH)+1)+"-" +
                calendar.get(Calendar.DAY_OF_MONTH)+" "+
                calendar.get(Calendar.HOUR_OF_DAY)+":"+
                calendar.get(Calendar.MINUTE)+":" +
                calendar.get(Calendar.SECOND);
        Log.d(TAG, "saveShotScreen: defaultName="+defaultName);
        //返回jpg文件是否存在
        return createJpgFile("/sdcard/test/shotscreen/"+defaultName+".jpg").exists();
    }
    public void writeResult(String path,ArrayList<String> arrayList){
        path = "sdcard/test/"+path+".txt";
        try {
            if(bw==null)
            bw = new FileOutputStream(path,false);
            for(String text : arrayList){
                Log.d(TAG, "writeResult: text=" + text);
                bw.write(text.getBytes());
                bw.flush();
                bw.write("\n".getBytes());
                bw.flush();
            }
            //因为关闭了没有置为null所以下次调用时出错
            bw.close();
            bw = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public void writeResult(String path ,String text){
//        path = "sdcard/test/"+path+".txt";
//        try {
//            if(bw==null)
//                bw = new FileOutputStream(path,false);
//            bw.write(text.getBytes());
//            bw.flush();
//            bw.write("\n".getBytes());
//            bw.flush();
//            bw.close();
//            bw = null;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
