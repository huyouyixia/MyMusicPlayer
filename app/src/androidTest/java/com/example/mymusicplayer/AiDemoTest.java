package com.example.mymusicplayer;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;
import android.widget.Toast;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by v_wtdeng on 2018/1/15.
 */

public class AiDemoTest {
    public UiDevice uiDevice;
    private boolean isRun = true;
    private FileOutputStream fos;
    @Before
    public void before() throws InterruptedException {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }
    @Test
    public void clickStart() throws InterruptedException, IOException {
        UiObject2 startButton =null;
        int k = 0;
        Log.d("AiDemo", "clickStart: uiDevice=null" + (uiDevice==null));
        while(startButton ==null){
            startButton = uiDevice.findObject(
                    By.res("com.tencent.ai.demo:id/start").pkg("com.tencent.ai.demo"));

            Thread.sleep(1000);
            k++;
            if(k==60){
                throw new RuntimeException("1分钟内没有找到控件");
            }
        }
        fos = new FileOutputStream("sdcard/demo_original.txt",true);
        new Thread(()->{

            try {

                while (isRun){
                    byte[] b = uiDevice.executeShellCommand("dumpsys meminfo com.tencent.ai.demo -d").getBytes();
                    String s = new String(b,0,b.length);
                    Log.d("AiDemo", "clickStart: s=" + s);
                    fos.write(b,0,b.length);
                    Thread.sleep(1000);
                }
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        for(int i=0; i<5000; i++){
            startButton.click();
            Log.d("AiDemo", "clickStart: 进来了");
            if(i==5000){
                isRun = false;
            }
            Thread.sleep(20000);
        }
        fos.close();

    }
}
