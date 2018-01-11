package com.example.mymusicplayer;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicDetailActivity extends BasicActivity implements View.OnClickListener{
    private static final String TAG="MusicDetailActivity";
    private TextView title;
    private ImageView ablumPic;
    private TextView lyric;
    private Button previosMusic;
    private Button playORpause;
    private Button nextMusic;
    private SeekBar seekBar;
    //必须静态，
    private static MyMedia myMedia = new MyMedia();
    private MediaPlayer mediaPlayer ;
    private int playing =0;
    private ImageView detailBackgroundPic;
    private Button backButon;
    private static List<File> musicList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.play_detail);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String pic =  preferences.getString("listPic",null);

        mediaPlayer = myMedia.getMediaPlayer();
        musicList = myMedia.getMusicList();
        Log.d(TAG, "onCreate: musicList=" + musicList.size());
        initLayout();
        if(pic!=null){
            Glide.with(this).load(pic).crossFade(1000).into(detailBackgroundPic);
            new ListViewTextColor().setSuccess(true);
        }
        //定时器，更新进度条
        handler.postDelayed(t,200);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void initLayout() {
        title = (TextView)findViewById(R.id.detail_title);
        ablumPic = (ImageView)findViewById(R.id.detail_pic);
        lyric = (TextView)findViewById(R.id.detail_lir);
        title.setText(myMedia.getTitle());
        myMedia.paomadeng(title);
        previosMusic = (Button)findViewById(R.id.previous_music);
        nextMusic = (Button)findViewById(R.id.next_music);
        playORpause = (Button)findViewById(R.id.play_or_pause);
        previosMusic.setOnClickListener(this);

        nextMusic.setBackgroundResource(R.drawable.next);
        previosMusic.setBackgroundResource(R.drawable.previous);

        nextMusic.setOnClickListener(this);
        playORpause.setOnClickListener(this);
        seekBar = (SeekBar)findViewById(R.id.seekbar);
        ablumPic = (ImageView)findViewById(R.id.detail_pic);
        detailBackgroundPic = (ImageView)findViewById(R.id.detail_backpic);
        backButon = (Button)findViewById(R.id.back_button);
        backButon.setOnClickListener(this);
        if(new ListViewTextColor().isSuccess()){
            title.setTextColor(Color.WHITE);
        }
        textPauseOrPlay();
        //使用timer会出现随机播放失败MediaPlayer（-38,0）
//        timer.schedule(new TimerTask() {
//            int j;
//            int j2;
//            boolean b = true;
//            int ml ;
//            @Override
//            public void run() {
//                if(b){
//                    ml = getSeek();
//                     j2 = myMedia.getPlayingIndex();
////                    Log.d(TAG, "run: j2=" + j2);
//                    b = false;
//                }
//                j = myMedia.getPlayingIndex();
//                seekBar.setMax(ml);
//                seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                if(j2 != j){
//                    runOnUiThread(()->{
//                        title.setText(myMedia.getTitle());
//
//                    });
//                    b = true;
////                    Log.d(TAG, "run: j2!=j条件中的j2=" + j2);
//                }
////                Log.d(TAG, "run: getCurrentPosition=" + mediaPlayer.getCurrentPosition());
//            }
//        },0,200);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    seekBar.setProgress(i-1);
                    mediaPlayer.seekTo(i-1);
                    Log.d(TAG, "onProgressChanged: 进入了onProgressChanaged");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                Log.d(TAG, "onStartTrackingTouch: 拉动seekBar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        
    }
    //播放、暂停按钮的状态现显示
    private void textPauseOrPlay(){
        if(mediaPlayer.isPlaying()){
//            playORpause.setText("暂停");
            playORpause.setBackgroundResource(R.drawable.pause);
        }else{
//            playORpause.setText("播放");
            playORpause.setBackgroundResource(R.drawable.play);
        }
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.previous_music:
                //获取当前正在播放的index
                initPreviousNext();
                Log.d(TAG, "onClick: 上一首中的playing=" + playing);
                if(playing <= 0){
                    playing = musicList.size()-1;
                    myMedia.playList(musicList,playing);
                }else{
                    //手贱啊，这里是减写成了加。可使用playing -= 1
                    playing -=  1;
                    myMedia.playList(musicList,playing);
                }
                break;
            case R.id.next_music:
                initPreviousNext();
                Log.d(TAG, "onClick: 下一首playing =" +playing);
                if(playing >= musicList.size()-1){
                    playing = 0;
                    myMedia.playList(musicList,playing);
                }else {
                    //不能使用playing++，因为++要在执行后才+1
                    playing = playing + 1;
                    myMedia.playList(musicList,playing);
                }
                break;
            case R.id.play_or_pause:
                Log.d(TAG, "onClick: 暂停中的playing=" + playing);
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }else{
                    mediaPlayer.start();
                }
                break;
            case R.id.back_button:
                finish();
//                Toast.makeText(this,"点击了返回",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
//        textPauseOrPlay();
//        //设置标题
//        initPreviousNext();
//        title.setText(myMedia.getTitle());
    }
    private void initPreviousNext(){
        playing = myMedia.getPlayingIndex();
        Log.d(TAG, "initPreviousNext:获取到的playing=" + playing);
    }
    private Handler handler = new Handler();
    private Runnable t = new Runnable() {
        int j ;
        @Override
        public void run() {
            if(!isFinishing()){
                textPauseOrPlay();

                handler.postDelayed(this,300);
                seekBar.setMax(mediaPlayer.getDuration());
                j = mediaPlayer.getCurrentPosition();
                seekBar.setProgress(j);
                //因为线程有延时，代码运行需要时间，所以到这的时候已经大于300
                if(j<=330){
                    title.setText(myMedia.getTitle());
                    //跑马灯
                    myMedia.paomadeng(title);
                }
//                Log.d(TAG, "run: 进入了run");
            }

        }
    };

}
