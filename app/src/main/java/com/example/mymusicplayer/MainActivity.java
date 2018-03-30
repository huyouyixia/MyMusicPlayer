package com.example.mymusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.mymusicplayer.URLConnectionUtil.getInputFromHttp;

public class MainActivity extends BasicActivity implements View.OnClickListener{

    private static final String TAG ="MainActivity";
    private static final String bUrl = "https://cn.bing.com";
    private Button menuButton;
    //创建数据库
    private static MyDatabaseHelper helper;
    private ListView musicLview;
    private List<File> musicList = new ArrayList<>();
    private Music music;
    private AlertDialog alertDialog;
    private MusicListViewAdapter adapter;
    private SQLiteDatabase db ;
    private ImageView listPic;
    //最好是静态，不然容易抱错，-38
    private static MyMedia mymedia = new MyMedia();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView menuExit;
    private MediaPlayer mediaPlayer;
    private TextView mainTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        musicLview = (ListView)findViewById(R.id.music_listview);
        menuButton = findViewById(R.id.menu_button);
        helper = new MyDatabaseHelper(this,"musicTable.db",null,1);
        initMain();
        displayMusicFromDB();
        listPic = (ImageView)findViewById(R.id.listpic);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String pic =  preferences.getString("listPic",null);
        if(pic!=null){
            Glide.with(this).load(pic)
                    .bitmapTransform(new BlurTransformation(this,13)).into(listPic);
            new ListViewTextColor().setSuccess(true);
        }else {

            new Thread(()->{
                try {
                    initListPic();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,"允许读写权限",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"拒绝权限将不能使用",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }
    /*
    扫描音乐
     */
    private void initMain(){
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            },1);}
        music = new Music();
        menuButton.setOnClickListener(this);
        menuExit = findViewById(R.id.menu_exit);
        menuExit.setOnClickListener(this);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        mymedia.setMediaPlayer(new MediaPlayer());
        mediaPlayer = mymedia.getMediaPlayer();
        mainTitle = findViewById(R.id.mainTitle);
//        mainTitle.setTextColor(Color.WHITE);
        navListener(navigationView);
        myOnItemListener();
    }
    private void navListener(NavigationView nav){
        nav.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menu_alarm:
                    Toast.makeText(MainActivity.this,item.getTitle().toString(),Toast.LENGTH_SHORT).show();
                    Intent alrmIntent = new Intent(this,AlarmPlayActivity.class);
                    startActivity(alrmIntent);
                    break;
                case R.id.menu_search_all:
//                    Toast.makeText(MainActivity.this,item.getTitle(),Toast.LENGTH_SHORT).show();
//                    alertDialog = music.setDialog(MainActivity.this,"扫描音乐","搜索中，请稍候……",false);
//                    alertDialog.show();
//                    new Thread(()->{
//                        synchronized (musicList){
//                            musicList = music.getMusicList();
//                        }
//
//                        if(musicList.size()==0){
//                            runOnUiThread(()->{
//                                Toast.makeText(MainActivity.this,"没有找到大于3M的音乐",Toast.LENGTH_SHORT).show();
//                            });
//                        }else if(musicList.size()>0 && helper.getMydb()!=null){
//                            //去重
//                            String mn = "";
//                            for(int i=0; i<musicList.size(); i++){
//                                if(mn.equals(musicList.get(i).toString())){
//                                    musicList.remove(i);
//                                    i--;
//                                }
//                            }
//                            //添加到数据库中
//                            helper.insertMydb(musicList);
//                        }
//                        runOnUiThread(()->{
//                            alertDialog.dismiss();
//                            //这里不能使用synchronized，不然会添加重复歌曲
////                    synchronized (this){
//                            myOnItemListener();
////                    }
//                        });
//
//                    }).start();
                    Intent intent = new Intent(MainActivity.this,SearchMusicActivity.class);
                    startActivity(intent);
                    break;
                default:
//                        Log.d(TAG, "onNavigationItemSelected: ItemId=" + item.getItemId()+"\n"+item.getGroupId());
                    break;
            }
            Log.d(TAG, "onNavigationItemSelected: close之前" + item.getTitle());
            drawerLayout.closeDrawers();
            return false;
        });


    }
    private void myOnItemListener(){
        adapter = new MusicListViewAdapter(MainActivity.this,
                R.layout.music_layout,musicList);
        adapter.setTextId(R.id.music_name);
        musicLview.setAdapter(adapter);
        musicLview.setOnItemClickListener((adapterView, view, i, l) -> {
            String musicPath = musicList.get(i).getPath();
            Log.d(TAG, "onItemClick: musicPath"+musicPath);
            //这个只播放一首
//                    MyMedia.initPlayer(musicPath);
            Intent toDetail = new Intent(MainActivity.this,MusicDetailActivity.class);
            startActivity(toDetail);
            Log.d(TAG, "myOnItemListener: isNull="+mediaPlayer);
            //如果点击的是正在播放的音乐则不重新播放
            if (mediaPlayer.isPlaying() &&
                    mymedia.getSourceFile().equals(musicList.get(i).getPath())) {
                Log.d(TAG, "onItemClick: 点击的是正在播放的音乐");
            } else {
                Log.d(TAG, "onItemClick: i=" + i);
                //播放歌曲列表
                mymedia.playList(musicList, i);
            }

        });
    }
    /*
    每日一图，作为列表背景
    //必应
    "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1"
     */
    private void initListPic() throws IOException {
       String resultText = "";
        resultText = getInputFromHttp(bUrl+"/HPImageArchive.aspx?format=js&idx=0&n=1",true);
        //获取到的所有字符串
        Log.d(TAG, "getInputFromHttp: 必应网站字符串，每日一图:" + resultText);
        JSONObject jO;
        JSONArray ja ;
        JSONObject jOb;
        try {
            int ind ;
            jO = new JSONObject(resultText);
            ja = jO.getJSONArray("images");
            for(ind =0; ind<ja.length(); ind++){
                jOb = ja.getJSONObject(ind);
                resultText = String.valueOf(jOb.get("url"));
                Log.d(TAG, "initListPic: stmp=" + resultText);
            }
            resultText = bUrl+resultText;
            Log.d(TAG, "initListPic: resultText=" + resultText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String sr = resultText;
        Log.d(TAG, "initListPic: sr=" + sr);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putString("listPic",sr);
        editor.apply();

        URLConnectionUtil.sendOkHttp(sr,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->{
                    Toast.makeText(MainActivity.this,"获取图片失败",Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                runOnUiThread(()->{
                    Glide.with(MainActivity.this).load(sr)
                            .bitmapTransform(new BlurTransformation(MainActivity.this,13)).into(listPic);
                    new ListViewTextColor().setSuccess(true);
                });
            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_button:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_exit:
                mediaPlayer.stop();
                //释放资源
                mediaPlayer.release();
                //需要将MyMedia中的media释放才不会崩溃，为此用了一个set方法，onCreated时创建mediaplayer
                mymedia.setMediaPlayer(null);
                ActivityCollector.finishAll();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        displayMusicFromDB();
        super.onRestart();
    }
    private void displayMusicFromDB(){
        db = helper.getWritableDatabase();
        String result ="";
        Log.d(TAG, "onRestart: db==null" +( db==null) + " helper=null" + (helper==null));
        Cursor cursor = db.rawQuery("select musicLength from Music",null);
        long length ;
        long musicL = music.getMusicL();
        musicList.removeAll(musicList);
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            result = cursor.getString(cursor.getColumnIndex("musicLength"));
            length = Long.parseLong(result);
            Log.d(TAG, "displayMusicList: length=" + length);
            Log.d(TAG, "displayMusicFromDB: musicL=" + musicL);
            if (length>musicL) {
                Cursor cursor1 = db.rawQuery("select * from Music " +
                        "where musicLength="+length+"",null);
                //移到开头，不然报越界错误
                cursor1.moveToFirst();
                String name = cursor1.getString(cursor1.getColumnIndex("musicPath"));
                Log.d(TAG, "displayMusicList: name="+name);
                musicList.add(new File(name));
                }
                Log.d(TAG, "onRestart: musicList=" + Arrays.deepToString(musicList.toArray()));
        }
        cursor.close();
        Log.d(TAG, "onRestart_result===" + result);

        myOnItemListener();
    }
}

