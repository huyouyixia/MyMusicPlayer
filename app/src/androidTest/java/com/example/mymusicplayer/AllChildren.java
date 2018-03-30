package com.example.mymusicplayer;

import android.os.SystemClock;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by v_wtdeng on 2018/3/7.
 */

public class AllChildren{
    private final String TAG = "AllChildren";
    private List<UiObject2> childrenList = new ArrayList<>();
    private String text ="";
    private String time;
    private String analysisXmlText;
    private ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    private ByteArrayInputStream byteIn;

    public String getAnalysisXmlText() {
        return analysisXmlText;
    }

    public void setAnalysisXmlText(String analysisXmlText) {
        this.analysisXmlText = analysisXmlText;
    }

    public void dumpWindow(UiDevice device){
        device.waitForWindowUpdate("com.tencent.ai.dobby",1000);
        Calendar calendar = Calendar.getInstance(Locale.CHINESE);
        time = calendar.get(Calendar.YEAR)+"-" +
                (calendar.get(Calendar.MONTH)+1)+"-" +
                (calendar.get(Calendar.DAY_OF_MONTH))
                +" " +calendar.get(Calendar.HOUR)+":" +
                calendar.get(Calendar.MINUTE) +":" +
                calendar.get(Calendar.SECOND);
        try {
            device.dumpWindowHierarchy(byteOut);
            byte[] bytes = byteOut.toByteArray();
            byteIn = new ByteArrayInputStream(bytes);
            text = new String(bytes);
            analysisXml(text,"android.widget.TextView");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //重置，不然会把上一次的连上
        byteOut.reset();
    }
    public UiObject2 getUiObject(String text,String className,String resourceName){
        if(((text==null)&&(className==null)&&(resourceName==null))
                ||((text.equals(""))&&(className.equals(""))&&(resourceName.equals("")))){
            throw new IllegalArgumentException("参数不能全部为空");
        }
        return null;
    }
    public void analysisXml(String xmlString,String nodeTagName){
        try {
            String[] xmlsz = xmlString.split("\n");
            if(xmlsz.length==0){
                throw new IOException("文件没有找到\\n");
            }
            String[] splitXmlsz;
            String resultTmp = "";
            String tmp2 = "";
            Log.d(TAG, "analysisXml: 分割xml后的数组长度=" + xmlsz.length);
            for(int i=0; i<xmlsz.length; i++){
                Log.d(TAG, "analysisXml: xmlsz[i]=" + xmlsz[i]);
                if(xmlsz[i].contains(nodeTagName)){
                    splitXmlsz = xmlsz[i].split("\\s");
                    for(int j=0; j<splitXmlsz.length; j++){
                        if(splitXmlsz[j].contains("text=")&&!splitXmlsz[j].contains("发送")){
                            Log.d(TAG, "analysisXml: "+splitXmlsz[j]);
                            tmp2 = splitXmlsz[j].substring(splitXmlsz[j].indexOf("=\"")+1).trim();
                            if(tmp2.length()>1){
                                resultTmp += tmp2;
                            }

                        }
                    }

                }
            }
            setAnalysisXmlText(resultTmp);
            Log.d(TAG, "analysisXml: resultTmp = "+resultTmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<UiObject2> iteratorUiObject(List<UiObject2> list){
//        childrenList.addAll(list);
        if(childrenList.size()>0)
        childrenList.removeAll(childrenList);
        childrenList.addAll(list);
        Iterator<UiObject2> iterator = childrenList.iterator();
        while (iterator.hasNext()){
            iteratorUiObject(iterator.next());
        }
        return childrenList;
    }
    public List<UiObject2> iteratorUiObject(UiObject2 object2){
        int childrenCount = object2.getChildCount();
        UiObject2 tmp ;
        Log.d(TAG, "iteratorUiObject: childrenList.size()"+childrenList.size());
        childrenList = object2.getChildren();
        if(childrenList.size()>0){
            nextUiObject(childrenList);
        }

        return  childrenList;
    }
    private void nextUiObject(List<UiObject2> list){
        for(int i=0; i<list.size(); i++){
            text = list.get(i).getText();
            if(text==null||text.equals("")){
                Log.d(TAG, "nextUiObject: text为空");
            }else{
                Log.d(TAG, "nextUiObject: text=" + text);
            }
        }
    }

}
