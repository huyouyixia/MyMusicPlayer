package com.example.mymusicplayer;

import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by wentaodeng on 2017/12/11.
 */

public class MyMedia {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static int playingIndex = 0;
    private static String sourceFile = "";
    private static int musicLength;
    //需要静态的才不会getMusicList出现为空
    private static List<File> musicList = new ArrayList<>();
    public void initPlayer(String sourceF){
        sourceFile = sourceF;
        playingIndex = musicList.indexOf(new File(sourceF));
        Log.d("MyMedia", "设置播放文件initPlayer中的playingIndex=" + playingIndex);
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(sourceF);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(mp->{
                Log.d("MyMedia", "initPlayer: 准备完毕");
                mediaPlayer.start();
                musicLength = mediaPlayer.getDuration();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //要同步，不然会越界出错，现在看来暂时只能找到这个原因
    public void playList(List<File> musicL, int startIndex){
        musicList = musicL;
        //后面好使用
        playingIndex = startIndex;
        Log.d("MyMedia", "没有递归之前playingIndex= "+playingIndex);

        initPlayer(musicL.get(playingIndex).getPath());
        mediaPlayer.setOnCompletionListener(lin->{
            //播放下一首
            if(playingIndex +1== musicL.size()){
                //默认循环播放
                playingIndex = 0;
                playList(musicL,playingIndex);
            }else{
                playList(musicL,playingIndex+1);
                Log.d("MyMedia", "playList中的playingIndex="+playingIndex);
            }
        });
    }
    public int getPlayingIndex(){
//        Log.d("getPlayingIndex", "getPlayingIndex方法中传入的playingIndex= "+playingIndex);
        return playingIndex;
    }
    public List<File> getMusicList(){
        Log.d("getMusicList方法", "getMusicList: musicList=" + musicList.size());
        return musicList;
    }
    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }
    public String getSourceFile(){
        return sourceFile;
    }
    public String getTitle(){
        String titleText = new File(sourceFile).getName().replace(".mp3","");
        return titleText;
    }
    public void paomadeng(TextView textView){
        Log.d(TAG, "paomadeng: 进入跑马灯设置了");
        //跑马灯
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //这条必须设置，不然不会滚动
        textView.setSingleLine(true);
        textView.setSelected(true);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
        textView.setMarqueeRepeatLimit(-1);
    }
}
