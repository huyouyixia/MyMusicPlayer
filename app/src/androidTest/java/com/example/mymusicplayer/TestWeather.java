package com.example.mymusicplayer;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by v_wtdeng on 2018/3/30.
 */

public class TestWeather extends TestSendInput{
    private final String TAG = "TestWeather" ;
    private String[] checkTag = {"空气","气温","°/"};
    private int today;
    private UiDevice device;
    private final int SUPPORT_WEATHER_DAY = 4;
    private HashMap<Integer,String> week = new HashMap<>();
    private HashSet<String> date_weather = new HashSet<>();
    private ArrayList<String> resultList = new ArrayList<>();
    public TestWeather(UiDevice device){
        super(device);
        this.device = device;
        //创建对象时创建对应的日期集合
        week();
        dateWeather();
    }
    /*
    添加日期的别称到set集合,set是不可重复集合
     */
    public void dateWeather(){
        date_weather.add("前天");
        date_weather.add("昨天");
        date_weather.add("今天");
        date_weather.add("明天");
        date_weather.add("后天");
    }
    /*
    添加星期的key-value对
     */
    public void week(){
        week.clear();
        week.put(2,"周一");
        week.put(3,"周二");
        week.put(4,"周三");
        week.put(5,"周四");
        week.put(6,"周五");
        week.put(7,"周六");
        week.put(1,"周日");
        Calendar calendar = Calendar.getInstance(Locale.CHINESE);
        calendar.setTimeInMillis(System.currentTimeMillis());
        today = calendar.get(Calendar.DAY_OF_WEEK);
//        Log.d(TAG, "week: today=" + today);
        for (int i =1; i<week.size()+1; i++) {
            if(i==today){
                Log.d(TAG, "week: 当前星期为：" + week.get(i));
                break;
            }
        }
    }
    /*
    所有天气case放入集合中后迭代每一条
    @param weatherAl 要输入天气case的String集合
     */
    public void allWeatherInput(ArrayList<String> weatherAl){
        weatherAl.forEach(inputText->{
            inputText(inputText);
        });
    }
    /*
    天气回复的结果
     */
    public void weatherStringAnswer(){
        Log.d(TAG, "weatherStringAnswer: 天气文字结果:"+this.getResult());
    }
    /*
    检测今天天气的大图标是否存在
     */
    private boolean checkTodayIcon(){
        UiObject2 icon = device.findObject(By
                .res("com.tencent.ai.dobby:id/weather_id_today_icon"));
        return icon!=null && icon.isEnabled();
    }
    /*
    检查指定别称日期天气，昨天、今天、明天
    @param resultText 天气返回的文字结果
     */
    private boolean checkAssignDay(String resultText){
        boolean dt = false;
        for(String date : date_weather){
            if(resultText.contains(date)){
                dt = true;
                break;
            }
        }
        for(Integer integer : week.keySet()){
            if(resultText.contains(week.get(integer))){
                dt = true;
                break;
            }
        }
        return dt;
    }
    /*
    检查页面返回日期是否正确，用于对照没有指定日期查询的结果,
    当前支持除今天外4天的天气查询
    @param resultText 天气返回的所有文字结果
     */
    private boolean checkWeek(String resultText){
        String dayOfWeek = week.get(today);
        Log.d(TAG, "cheekWeek: 今天是星期：" + dayOfWeek);
        //分割星期，判断是否除了今天其他日期都存在
        Pattern pattern ;
        Matcher matcher;
        String tmp ;
        int count = 0;
        for(Integer entry: week.keySet()){
            tmp = week.get(entry);
            if(!dayOfWeek.equals(tmp)){
                pattern = Pattern.compile(tmp);
                matcher = pattern.matcher(resultText);
                if(!matcher.find()){
                    Log.d(TAG, "cheekWeek: 除了今天之外没有发现: " + tmp);
                }else{
                    Log.d(TAG, "cheekWeek: 发现星期符合条件的日期key为：" +entry+"-" + tmp);
                    count++;
                }
            }

        }
        return count==SUPPORT_WEATHER_DAY;
    }

    /*
    检查温度
    @param resultText 天气返回结果
     */
    private boolean checkTemperature(String resultText){
        boolean w = true;
        for(int i=0; i<checkTag.length; i++){
            w &= resultText.contains(checkTag[i]);
        }
        return w;
    }

    /*
    检查城市
    @param resultText 天气返回结果
     */
    private boolean checkCity(String resultText){
        //如果同一个城市出现2次则返回true
        /*
        略
         */

        return false;
    }
    private String table ="输入\t" +"大天气图标\t " +"星期\t" +"温度\t"+"日期\t"+"原始结果\t\n";
    private boolean isFirstWrite = true;
    @Override
    public boolean checkresult(String origin,String s) {
        TestCreateFile createDirectory = new TestCreateFile(device);
        boolean iconB = false;
        boolean weekB = false;
        boolean temperB = false;
        boolean dateB = false;
        String allTag;

        iconB = checkTodayIcon();
        weekB = checkWeek(s);
        temperB = checkTemperature(s);
        dateB = checkAssignDay(s);
        //检查天气图标
        if(!iconB){
            //带参数的构造器
            Log.d(TAG, "checkresult: 日期为今天的大图标控件没有找到，保存截图");
            createDirectory.saveShotScreen();
        }
        Log.d(TAG, "checkresult: 大图标check:" + iconB);
        //检查星期
        Log.d(TAG, "checkresult: 星期check:"+weekB);
        //检查温度
        Log.d(TAG, "checkresult: 温度check:"+temperB);
        //检查日期
        Log.d(TAG, "checkresult: 日期check:"+dateB);
        allTag = origin+"\t"+iconB +"\t" + weekB+"\t" + temperB + "\t" + dateB + "\t"+s.toString()+"\n";
        if(isFirstWrite){
            allTag = table + allTag;
        }
        isFirstWrite = false;
        resultList.add(allTag);
        Log.d(TAG, "checkresult: origin=" + origin);
        createDirectory.writeResult(TestWeather.class.getSimpleName(),resultList);
        Log.d(TAG, "checkresult: text=" + allTag);

        return iconB && weekB && temperB && dateB;
    }
}
