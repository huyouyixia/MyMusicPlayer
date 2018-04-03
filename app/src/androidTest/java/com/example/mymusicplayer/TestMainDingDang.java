package com.example.mymusicplayer;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;
import android.util.TimeUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by v_wtdeng on 2018/3/30.
 */

public class TestMainDingDang{
    private UiDevice device;
    private final String TAG = "TestMainDingDang" ;
    @Before
    public void before(){
        int timeout = 0;
        while(device==null){
            device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            if(timeout>=40){
                throw new NullPointerException("40秒内没有生成device实例");
            }
            timeout++;
        }
        //删除文件夹中的文件

    }
    @Test
    public void startWeather(){
        TestWeather testWeather = new TestWeather(device);
        String[] wea = {"北京天气","深圳天气"};
        List<String> answerTxt;
        for(String s : wea){
            testWeather.inputText(s);

            //日志输出文字回复，不包含卡片
            testWeather.weatherStringAnswer();
            device.waitForIdle(2000);
            //获取该控件的所有子控件的textView
           answerTxt = testWeather.getAllText(device.findObject
                    (By.clazz("android.widget.FrameLayout")));
        Log.d(TAG, "startWeather: answerTxt=" + answerTxt.toString());
            Log.d(TAG, "startWeather: 天气check="+testWeather.checkresult(s,answerTxt.toString()));
        }

    }
    @After
    public void end(){
        Log.d(TAG, "end: 结束");

    }

}
