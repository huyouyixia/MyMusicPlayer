package com.example.mymusicplayer;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by v_wtdeng on 2017/12/28.
 */

public class MyUiTest {
    private UiDevice mDevices;
    private boolean isOpen = false;
    @Before
    public void before() throws InterruptedException {
        mDevices = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevices.pressHome();
        openApp("com.tencent.ai.dobby.sugyvoice");
    }
    @Test
    public void test() throws InterruptedException {
        UiObject2 sugyvoice = null;
        while(sugyvoice==null){
            Thread.sleep(1000);
            sugyvoice = mDevices.findObject(By.pkg("com.tencent.ai.dobby.sugyvoice"));
            Log.d("MyUiTest", "test: while中" + sugyvoice);
        }
        Log.d("MyUiTest", "test: exsits=" + sugyvoice.isChecked());
        mDevices.swipe(500,600,100,600,20);
        mfindObject(By.res("com.tencent.ai.dobby.sugyvoice:id/btn_feedback")).click();
        Music music = new Music();
        List<File> musicList = music.getMusicList(Environment.getExternalStorageDirectory(),0);
        MyMedia myMedia = new MyMedia();
        myMedia.playList(musicList,0);

        while (myMedia.getMediaPlayer()!=null){
            Thread.sleep(1000);
            if(!myMedia.getMediaPlayer().isPlaying()){
                Log.d("MyUiTest", "test: 没有在播放");
                break;
            }
        }
        Log.d("MyUiTest", "test: musicList=" + musicList.size());
    }
    public void openApp(String packageName) throws InterruptedException {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public UiObject2 mfindObject(BySelector selector)throws InterruptedException{
        UiObject2 object2 = null;
        int timeout = 5000;
        int delay = 1000;
        long time = System.currentTimeMillis();
        while (object2 == null){
            object2 = mDevices.findObject(selector);
            Thread.sleep(delay);
            if(System.currentTimeMillis() - timeout > time){

                break;
            }
        }
        return object2;
    }
}
