package com.example.mymusicplayer;

import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by v_wtdeng on 2018/2/11.
 * 我来试试工厂模式:把功能解耦，当需要改变时只需改变该接口的实现类
 * 金立F103只能读取GB2312格式，虽然输出的格式显示是UTF-8,实际上那是sd支持的格式
 */

public interface FileFactory {
    static String TAG = "FileFactory";
    static String readFile(String path,Charset charset) throws IOException {
        File file = new File(path);
        if(!file.exists() || file.isFile()){
            file.mkdir();
        }
        Log.d("FileFactory", "readFile: path=" + path);
        FileReader fileReader = new FileReader(file);
        Log.d("FileFactory", "readFile: 编码格式为："+fileReader.getEncoding());
        int read ;
        String line = "" ;
        String resultT = "";
        char[] chars = new char[1024];
        InputStreamReader fis = new InputStreamReader(new FileInputStream(file),charset);
        while ((read = fis.read(chars))>-1){
            line = new String(chars);
            resultT += line.trim();
            Log.d(TAG, "readFile: "+resultT);
        }

//        BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//        while ((line=bufferedReader.readLine())!=null){
//            Log.d(TAG, "readFile读取文件GB2312格式: "+new String(line.getBytes(),"GB2312"));
//            resultT += new String(line.getBytes(),"GB2312");
//        }
//        bufferedReader.close();
        return resultT;
    }
    static FileWriter getFileWriteIntance(String path, boolean append) throws IOException {
        FileWriter fileWriter = new FileWriter(path,append);

        return fileWriter;
    }
    void compareReadAndWrite(String readPath);

}
