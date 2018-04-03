package com.example.mymusicplayer;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.EventCondition;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v_wtdeng on 2018/3/30.
 */

public abstract class TestSendInput {
    private final String TAG = "TestSendInput";
    private UiDevice device;
    private UiObject2 object2;
    private UiObject2 send;
    private String result;
    private List<UiObject2> allUiObject;
    private int width ;
    private int height;
    protected TestSendInput(UiDevice device){
        this.device = device;
        width = device.getDisplayWidth();
        height = device.getDisplayHeight();
    }

    /*
    输入框输入
    @param text 测试用例
     */
    protected boolean inputText(String text){
        EventCondition<Boolean> newWindow = Until.newWindow();
        try{
            object2 = device.findObject(By
                    .res("com.tencent.ai.dobby:id/text_input"));
            Log.d(TAG, "inputText: try");
        }catch (NullPointerException not){
            object2 = device.findObject(By
                    .res("com.tencent.ai.dobby:id/input_select_icon"));
            Log.d(TAG, "inputText: cathc捕获，没有找到输入框，语音输入切换到文字输入");
            object2.clickAndWait(newWindow,2000);
            if(object2!=null && object2.isEnabled()){
                Log.d(TAG, "inputText: 进入if");
                //再次调用
                inputText(text);
            }
            return false;

        }
        if(object2!=null && object2.isEnabled()){
            //输入文本
            object2.setText(text);
            send = device.findObject(By.res("com.tencent.ai.dobby:id/text_input_send"));
            //发送
            send.clickAndWait(newWindow,2000);
            //确保结果是最新，需要上滑界面
            device.swipe(width/2,height/2,width/2,200,10);
            device.waitForIdle(1000);
            List<UiObject2> answerList = device.findObjects(By.res("com.tencent.ai.dobby:id/tv_answer"));
            device.waitForIdle(2000);
            Log.d(TAG, "inputText: answerList.size"+answerList.size());
            if(answerList.size()>1){
                object2 = answerList.get(answerList.size()-1);
                //文字返回结果，不包括卡片的文字
                result = object2.getText();
            }else if(answerList.size()==1){
                object2 = answerList.get(0);
                //文字返回结果，不包括卡片的文字
                result = object2.getText();
            }else {
                result = "没有找到叮当回复的控件";
            }

        }
        return true;
    }
    /*
    返回回复结果
     */
    protected String getResult(){
        return result;
    }
    /*
    如果有相同的对象，会被覆盖，这里刚好使用FrameLayout可以覆盖所有回复
    @param uiObject2 某个控件
    @return 该控件所有的子控件的textView
     */
    public List<String> getAllText(UiObject2 uiObject2){
        List<String> allTxt = new ArrayList<>();
        allUiObject = uiObject2.getChildren();
        String text = "";
        UiObject2 tmp ;
        Log.d(TAG, "getAllText: size" + allUiObject.size());
        for(int i=0; i<allUiObject.size(); i++){

           tmp = allUiObject.get(i);
           text = tmp.getText();
//            Log.d(TAG, "getAllText: tmp="+tmp.getClassName());
           if(text!=null &&tmp.getClassName().equals("android.widget.TextView")&&!text.equals("")){
               allTxt.add(text);

           }else{
               if(tmp.getChildren().size()>0){
                   allUiObject.addAll(tmp.getChildren());
               }
           }
        }

        Log.d(TAG, "getAllText: all"+allTxt);
        return allTxt;
    }
    /*
    结果校验抽象方法
     */
    public abstract boolean checkresult(String origin,String result);
}
