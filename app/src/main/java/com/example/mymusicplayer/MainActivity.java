package com.example.mymusicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG ="MainActivity";
    private static final String bUrl = "https://cn.bing.com";
    //创建数据库
    private MyDatabaseHelper helper;
    private ListView musicLview;
    private List<File> musicList = new ArrayList<>();
    private Music music;
    private Button button;
    private AlertDialog alertDialog;
    private MusicListViewAdapter adapter;
    private SQLiteDatabase db ;
    private ImageView listPic;
    private MyMedia mymedia = new MyMedia();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicLview = (ListView)findViewById(R.id.music_listview);
        button = (Button)findViewById(R.id.saomiao);
        helper = new MyDatabaseHelper(this,"musicTable.db",null,1);
        db = helper.getWritableDatabase();
//        Cursor cursor = db.rawQuery("Select * from Music",null);
//        Log.d(TAG, "onCreate: result="+cursor.getColumnCount());
        //getSelected中读取的是MusicPath
        String result = helper.getSelected(db,"select * from Music");
//        Log.d(TAG, "result===" + result);
        if (musicList.size()==0 && result.length()>1) {
            String[] musicPaths = result.split("\n");
//            Log.d(TAG, "onCreate: musicPaths="+ Arrays.deepToString(musicPaths));
            for(int pathIndex=0; pathIndex<musicPaths.length; pathIndex++){
                musicList.add(new File(musicPaths[pathIndex]));
            }
//            Log.d(TAG, "onCreate: musicList=" + Arrays.deepToString(musicList.toArray()));
        }
        initMain();
        listPic = (ImageView)findViewById(R.id.listpic);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String pic =  preferences.getString("listPic",null);
        if(pic!=null){
            Glide.with(this).load(pic).crossFade(1000)
                    .bitmapTransform(new BlurTransformation(this,17)).into(listPic);
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

        button.setOnClickListener(lin->{
            alertDialog = music.setDialog(this,"查找音乐","搜索中，请稍候……",false);
            alertDialog.show();
            new Thread(()->{
                synchronized (musicList){
                    musicList = music.getMusicList();
                }

                if(musicList.size()==0){
                    runOnUiThread(()->{
                        Toast.makeText(this,"没有找到大于3M的音乐",Toast.LENGTH_SHORT).show();
                    });
                }else if(helper.getMydb()!=null){
                    //添加到数据库中
                    helper.insertMydb(musicList);
                }
                runOnUiThread(()->{
                    alertDialog.dismiss();
                    //这里不能使用synchronized，不然会添加重复歌曲
//                    synchronized (this){
                        myOnItemListener();
//                    }
                });

            }).start();
        });
        myOnItemListener();
    }
    private void myOnItemListener(){
        adapter = new MusicListViewAdapter(MainActivity.this,
                R.layout.music_layout,musicList);
        musicLview.setAdapter(adapter);
        musicLview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String musicPath = musicList.get(i).getPath();
                Log.d(TAG, "onItemClick: musicPath"+musicPath);
                //这个只播放一首
//                    MyMedia.initPlayer(musicPath);
                Intent toDetail = new Intent(MainActivity.this,MusicDetailActivity.class);
                startActivity(toDetail);
                //如果点击的是正在播放的音乐则不重新播放
                if (mymedia.getMediaPlayer().isPlaying() &&
                        mymedia.getSourceFile().equals(musicList.get(i).getPath())){
                    Log.d(TAG, "onItemClick: 点击的是正在播放的音乐");
                }else{
                    Log.d(TAG, "onItemClick: i=" + i);
                    //播放歌曲列表
                    mymedia.playList(musicList,i);
                }

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
        InputStream is = getInputFromHttp(bUrl+"/HPImageArchive.aspx?format=js&idx=0&n=1");
        int ind = 0;
        byte[] bytes = new byte[1024];
        while((ind = is.read(bytes))>-1){
            resultText += new String(bytes,0,ind);
        }
        is.close();
        //获取到的所有字符串
        Log.d(TAG, "getInputFromHttp: 必应网站字符串，每日一图:" + resultText);
        resultText = resultText.substring(resultText.indexOf("url\":\"")+6
                ,resultText.lastIndexOf(".jpg"))+".jpg";
        Log.d(TAG, "initListPic: 获取链接的后半部分=" + resultText);
        //拼接链接
        resultText = bUrl + resultText;
        Log.d(TAG, "initListPic: 拼接后的连接：" + resultText);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this).edit();
        editor.putString("listPic",resultText);
        editor.apply();
        OkHttpClient okClient = new OkHttpClient();
        Request request = new Request.Builder().url(resultText).build();
        final String  re = resultText;
        okClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->{
                    Toast.makeText(MainActivity.this,"获取图片失败",Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                runOnUiThread(()->{
                    Glide.with(MainActivity.this).load(re)
                            .bitmapTransform(new BlurTransformation(MainActivity.this,17)).into(listPic);
                    new ListViewTextColor().setSuccess(true);
                });
            }
        });
    }

    public InputStream getInputFromHttp(String urlPath){
        try {
            URL url = new URL(urlPath);
            Log.d(TAG, "getInputFromHttp: url=" + urlPath);
            HttpURLConnection hConnect = (HttpURLConnection)url.openConnection();
            hConnect.setDoOutput(true);
            hConnect.setDoInput(true);
            InputStream is = hConnect.getInputStream();
            return is;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
