package com.example.mymusicplayer;

import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.EventCondition;
import android.support.test.uiautomator.SearchCondition;
import android.support.test.uiautomator.StaleObjectException;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by v_wtdeng on 2018/2/7.
 */

public class DingDangTest implements FileFactory{
    private String testDirectroy = "sdcard/test/";
    private UiDevice device;
    private UiObject2 object2 = null;
    private FileWriter writer;
    private String packageName = "com.tencent.ai.dobby";
    private final String TAG = "DingDangTest";
    private AllChildren children = new AllChildren();
    private Calendar calendar = Calendar.getInstance(Locale.CHINESE);;
    private String time;
    private List<UiObject2> chatContent;
    private String answer ="";
    @Before
    public void before() throws IOException {
        compareReadAndWrite(testDirectroy+"result.txt");
        System.exit(0);
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        device.executeShellCommand("monkey -p "+packageName+" -v 1");
        device.setCompressedLayoutHeirarchy(true);
        //删除测试文件夹下的jpg文件
        File jpgFile = new File(testDirectroy);
        for(File jpg : jpgFile.listFiles()){
            if(jpg.getName().endsWith(".jpg") || jpg.getName().endsWith(".xml")){
                Log.d(TAG, "before: 删除测试文件夹下的jpg文件"+jpg.getName());
                Log.d(TAG, "before: "+jpg.delete());
            }
        }
    }
    @Test
    public void onlineCheck() throws IOException, InterruptedException {
        BySelector selector = By.pkg(packageName);

        while(object2==null){
            object2 = device.findObject(selector);
            Thread.sleep(2000);
            Log.d(packageName, "onlineCheck: 等待……");
        }

        //依赖于新窗口
        EventCondition<Boolean> newWindow = Until.newWindow();
        device.findObject(By.res("com.tencent.ai.dobby:id/speaker_icon"))
                .clickAndWait(newWindow,2000);
        myWait(600);
        Log.d(packageName, "onlineCheck: object2=null"+(object2==null));
        Log.d(packageName, "onlineCheck: displaywidth=" + device.getDisplayWidth());
//        SearchCondition helpText = Until.hasObject(By.res("com.tencent.ai.dobby:id/record_text"));
//        device.wait(helpText,2000);
        device.findObject(By.res("com.tencent.ai.dobby:id/record_text")).clickAndWait(newWindow,2000);
        device.waitForWindowUpdate(packageName,600);
        device.findObject(By.res("com.tencent.ai.dobby:id/input_select_icon")).clickAndWait(newWindow,2000);
        myWait(600);

        File testFile = Environment.getExternalStoragePublicDirectory("test" );
        writer = FileFactory.getFileWriteIntance(testFile.getPath()+"/result.txt",false);
        String[] cases = FileFactory.readFile(testFile.getPath()+"/test.txt", Charset.forName("GB2312")).split("\r\n");
        Log.d(packageName, "onlineCheck: case=" +cases);
        for(String s : cases){
            startInputText(s);
        }
        writer.close();
    }
    @After
    public void end(){
        compareReadAndWrite(testDirectroy+"result.txt");
    }
    public void startInputText(String text) throws IOException {
        object2 = device.findObject(By.res("com.tencent.ai.dobby:id/text_input"));
        object2.setText(text);
        myWait(600);
        device.findObject(By.res("com.tencent.ai.dobby:id/text_input_send")).click();
        myWait(600);

//        BySelector chatlistSelector = By.clazz("android.widget.TextView");
//        children.dumpWindow(device);
        if(chatContent!=null){
            chatContent.clear();
        }

        BySelector chatlistSelector = By.pkg(packageName);
        chatContent = device.findObjects(chatlistSelector);
        Iterator<UiObject2> iterator = chatContent.iterator();
        Log.d(packageName, "onlineCheck: 测试问："+text);
        writer.write("测试输入：" + text+"\n");
//        children.iteratorUiObject(chatContent);
        Log.d(TAG, "startInputText: "+chatContent.size());

        try {
            children.dumpWindow(device);
            answer = children.getAnalysisXmlText();
            //每次清空
            children.setAnalysisXmlText("");
        }catch (StaleObjectException se){
            Log.d(TAG, "startInputText: catch"+"文本获取失败");
            myTakeShotScreen();
//                object2 = iterator.next();
        }
        Log.d(TAG, "startInputText: object2.getText()=" + answer);
        writer.write("回复："+answer+"\n\n");
        writer.flush();
    }
    public void myTakeShotScreen(){
        time = calendar.get(Calendar.YEAR)+""+(calendar.get(Calendar.MONTH)+1)+""+
                calendar.get(Calendar.DAY_OF_MONTH)+calendar.get(Calendar.HOUR_OF_DAY)+""+
                calendar.get(Calendar.MINUTE)+""+calendar.get(Calendar.SECOND)+"";
        device.takeScreenshot(new File(testDirectroy + time+".jpg"));
        myWait(1000);
    }
    public void myWait(long time){
        device.waitForWindowUpdate(packageName,time);
    }

    @Override
    public void compareReadAndWrite(String resultTxt) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(resultTxt));
            BufferedWriter bw = new BufferedWriter(new FileWriter(testDirectroy+"lastResult.txt"));
            String line ;
            String tm2 = "";
            String[] results;
            ArrayList<String> tem = new ArrayList<>();
            while ((line = bufferedReader.readLine())!=null){
              tem.addAll( Arrays.asList(line.split("\\p{Punct}")));
                Log.d(TAG, "compareReadAndWrite: line = " + line);
                Log.d(TAG, "compareReadAndWrite: "+line.split("\\p{Punct}").length);
              if(tem.size()>2){
                  for(int i=0; i<tem.size(); i++){
                      Log.d(TAG, "compareReadAndWrite: s=" + tem.get(i));
                     if(tem.get(0).equals(tem.get(1))){

                         tem.remove(0);
                         tem.remove(1);
                         i = 0;
                     }
                     results = tem.get(i).split("\\s");
                     for(int j=0; j<results.length; j++){
                         if(!results[j].contains("回复")&&!results[j].trim().equals("")){
                             tm2 = results[j];
                             Log.d(TAG, "compareReadAndWrite: tm2=" + tm2);
                             bw.write(tm2+"\t");
                             bw.flush();

                         }
                     }

                     bw.newLine();
                  }
              }else {
                  Log.d(TAG, "compareReadAndWrite: 分割长度小于等于2=="+line);
              }

              tem.clear();
            }
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
