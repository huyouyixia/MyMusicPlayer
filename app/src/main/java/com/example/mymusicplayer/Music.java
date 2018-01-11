package com.example.mymusicplayer;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wentaodeng on 2017/12/11.
 */

public class Music {
    public List<File> musicList = new ArrayList<>();
    private static long musicL;
    private final static String TAG = "Music";
    private String musicName;
    private String[] musicFormet = {".PCM",".AAC",".AAC+",".eAAC+",".MP3"
            ,".AMR",".FLAC",".APE",".DSD",".WAV"};
    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
    public long getMusicL(){
        return musicL;
    }

    /*
    方法一：File类递归
     */
    public List<File> getMusicList(File folder,long musicL) {
        this.musicL = musicL;
        musicList = new ArrayList<>();
//        File[] allDirectory = Environment.getExternalStorageDirectory().listFiles();
        File[] allDirectory = folder.listFiles();
//        Log.d(TAG, "getMusicList: allDirectory=" + allDirectory);
        nextDirectory(allDirectory);
        return musicList;
    }

    private void nextDirectory(File[] allDirectory) {

        for (File f : allDirectory) {
//            Log.d(TAG, "nextDirectory: " + f.getName());
            for(int i=0; i<musicFormet.length; i++){
                if ((f.getAbsolutePath().endsWith(musicFormet[i].toLowerCase())) && f.length() > musicL) {
                    musicList.add(f);
                    Log.d(TAG, "nextDirectory: 路径：" +f.getAbsolutePath());
                    Log.d(TAG, "nextDirectory: f.length()=" + f.length()+"musicL=" + musicL);
                    Log.d(TAG, "nextDirectory: musicList.add()=" + f.getName());
                }
            }
            if (!f.isHidden() && f.isDirectory()) {
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


    public List<File> deleteSameFile(List<File> musicList){
        Log.d(TAG, "一个参数deleteSameFile: 集合长度="+musicList.size());
        String name = "用来删除相同文件的变量初始化";
        for(int i=0; i<musicList.size(); i++){
            name = musicList.get(i).getAbsolutePath();
            for(int j=i+1; j<musicList.size(); j++){
                if(name.equals(musicList.get(j).getAbsolutePath())){
                    musicList.remove(j);
                    j--;
                    Log.d(TAG, "deleteSameFile: name=" +name);
                }
            }

        }
        Log.d(TAG, "一个参数deleteSameFile: musicList=" + Arrays.deepToString(musicList.toArray()));
        return musicList;
    }
    /*
    @param list1:需要去重的集合
    @param tagList:用来对比的集合
    @return 去除两个集合合并后的重复项
     */
    public List<File> merge(List<File> list1,List<File> list2){
        //两个集合先自己去重，再两个集合一起去重
        List<File> tmp1 = new ArrayList<>();
        //需要添加进去，不能只是引用
        tmp1.addAll(deleteSameFile(list1));
        List<File> tmp2 = new ArrayList<>();
        tmp2.addAll(deleteSameFile(list2));
        Log.d(TAG, "deleteSameFile: tmpD长度=" + tmp1.size());
        Log.d(TAG, "deleteSameFile: tmpT长度=" + tmp2.size());
        if(tmp1.size()==0){
            return tmp2;
        }

        Iterator<File> iterator = tmp1.iterator();
        Iterator<File> it = tmp2.iterator();
        while (it.hasNext()){
            String name  = it.next().getAbsolutePath();
            Log.d(TAG, "deleteSameFile: name="+name);
            if (iterator.hasNext()){
                String name2 = iterator.next().getAbsolutePath();
                Log.d(TAG, "deleteSameFile: name2=" + name2);
                if(name.equals(name2)){
                    iterator.remove();
                }
            }
        }
        //用for循环执行之后得到的集合长度为0，remove(j)实际只执行了12次
//        for(int i=0; i<tmpT.size(); i++){
//            for(int j=0; j<tmpD.size(); j++){
//                if(tmpT.get(i).getAbsolutePath().equals(tmpD.get(j).getAbsolutePath())){
//                    Log.d(TAG, "两个参数deleteSameFile: tmpD.get(j)=" +tmpD.get(j));
//                    tmpD.remove(j);
//                   j--;
//                }
//            }
//
//        }
        Log.d(TAG, "两个参数deleteSameFile: tmpD=" + Arrays.toString(tmp1.toArray()));
        Log.d(TAG, "两个参数deleteSameFile: tmpD的集合长度=" + tmp1.size());
        List<File> resultList = new ArrayList<>();
        if(tmp1.size()>0)
        resultList.addAll(tmp1);
        if(tmp2.size()>0)
        resultList.addAll(tmp2);
        Log.d(TAG, "deleteSameFile: reusltList="+Arrays.toString(resultList.toArray()));
        return resultList;

    }
    /*
   使用set方式合并两个集合
   */
    public Set<Object> merge(Set<Object> set,Set<Object> set2){
        Log.d(TAG, "deleteSameFile: set=" + set.size());
        Log.d(TAG, "deleteSameFile: set2=" + set2.size());
        Set<Object> resutlSet = new LinkedHashSet<>();
        resutlSet.addAll(set);
        resutlSet.addAll(set2);
        Log.d(TAG, "deleteSameFile: resultSet=" + resutlSet.size());
        Iterator<Object> iterator = resutlSet.iterator();
        while(iterator.hasNext()){
            Log.d(TAG, "deleteSameFile: 迭代=" + iterator.next().toString());
        }
        Log.d(TAG, "deleteSameFile: resultSet=" + (Arrays.toString(resutlSet.toArray())));
        return resutlSet;
    }
    /*
    对比2个集合，selectList作为参考集合,如果searchList中存在一样的元素，则删除searchList中的元素
    返回最后的searchList集合
     */
    public List<File> different(List<File> selectFromDBList,List<File> localList){
        if(selectFromDBList.size()==0){
            return localList;
        }
        if(localList.size()==0){
            return localList;
        }

        Iterator<File> iteratorSearch = localList.listIterator();
        while (iteratorSearch.hasNext()){
            File name = iteratorSearch.next();
            //这一行必须放在这里
            Iterator<File> iteratorSelect = selectFromDBList.listIterator();
            for(int i=0; i<selectFromDBList.size(); i++){
                if(iteratorSelect.hasNext() && iteratorSelect.next().getAbsolutePath()
                        .equals(name.getAbsolutePath())){
                    Log.d(TAG, "different: 即将删除：" + name.getAbsolutePath());
                    iteratorSearch.remove();
                }else{
                    Log.d(TAG, "different: zzz" + name.getName());
                }
            }

        }
        Log.d(TAG, "different: localList="+localList.size());
        List<File> resultList = new ArrayList<>();
        resultList.addAll(localList);
        return resultList;
    }
}
