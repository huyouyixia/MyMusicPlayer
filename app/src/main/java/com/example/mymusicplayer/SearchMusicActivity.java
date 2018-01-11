package com.example.mymusicplayer;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchMusicActivity extends BasicActivity
        implements View.OnClickListener, TextWatcher{
    private static final String TAG = "SearchMusicActivity";
    private EditText user_custom;
    private CheckBox folderCheck;
    private RadioButton size_kb;
    private RadioButton custom_size_kb;
    private Button searchAll;
    private Button searchFolder;
    private Music music;
    private List<File> musicList = new ArrayList<>();
    private android.support.v7.app.AlertDialog searchMusicDialog;
    private MyDatabaseHelper helper;
    private SQLiteDatabase db ;
    private RadioGroup customRadio;
    private RadioGroup defaultRadio;
    private long musicL;
    private Button folderBack;
    private int customL =0;
    private LinearLayout searchLocation;
    private MyMedia myMedia = new MyMedia();
    private List<File> beforeInsertList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.search_music_location);
        user_custom = findViewById(R.id.user_custom);
//        user_custom.setSingleLine(true);
        size_kb = findViewById(R.id.default_size_500kb);
        custom_size_kb = findViewById(R.id.custom_kb);
        searchAll = findViewById(R.id.search_all);
        searchFolder = findViewById(R.id.search_folder);
        searchFolder.setOnClickListener(this);
        searchAll.setOnClickListener(this);
        user_custom.setOnClickListener(this);
        user_custom.addTextChangedListener(this);
        folderBack = findViewById(R.id.select_folder_back);
        folderBack.setOnClickListener(this);
        music = new Music();
        helper = new MyDatabaseHelper(this,"musicTable.db",null,1);
        db = helper.getWritableDatabase();
        customRadio = findViewById(R.id.radio_custom);
        user_custom.setCursorVisible(false);
        defaultRadio = findViewById(R.id.radio_default);
        searchLocation = findViewById(R.id.search_location);
        searchLocation.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_custom:
                customRadio.setVisibility(View.VISIBLE);
                user_custom.setCursorVisible(true);
                defaultRadio.setVisibility(View.GONE);
                customRadio.setFocusable(true);
                break;
            case R.id.search_all:
                Toast.makeText(this,"全盘扫描",Toast.LENGTH_SHORT).show();
                editText = "";
                searchMusic();
                break;
            case R.id.search_folder:
                editText = "";
                Intent searchFolderIntent = new Intent(this,SearchFolderActivity.class);
                searchFolderIntent.putExtra("isFirstCreate",true);
                startActivity(searchFolderIntent);
                break;
            case R.id.select_folder_back:
                finish();
                break;
            case R.id.search_location:
                //收起键盘
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                user_custom.setCursorVisible(false);
                break;
            default:
                break;
        }
    }
    private void searchMusic(){
        String state = getSelectedRadioCheck();
        Log.d(TAG, "searchMusic: state=" + state);

        switch (state){
            case "custom_kb":
                musicL = customL * 1024;
                break;
            case "custom_m":
                musicL = customL * 1024 * 1024;
                Log.d(TAG, "searchMusic: custom_m=" +customL);
                break;
            case "default_500kb":
                musicL = 500 *1024;
                break;
            case "default_1m":
                musicL = 1024*1024;
                break;
            default:
                break;
        }

        Toast.makeText(this,"customL=" + customL+"musicL=" +musicL,Toast.LENGTH_SHORT).show();
        //扫描全部
        searchMusic(Environment.getExternalStorageDirectory(),musicL);
    }
    @NonNull
    private String getSelectedRadioCheck(){
        if(customRadio.isFocusable()){
            Log.d(TAG, "getSelectedRadioCheck: isEnabled=true");
            if(custom_size_kb.isChecked()){
                return "custom_kb";
            }else{
                return "custom_m";
            }

        }else{
            if(size_kb.isChecked()){
                return "default_500kb";
            }else {
                return "default_1m";
            }

        }
    }
    /*
    folder为文件夹
    musicL为文件夹中的文件大小
     */
    private void searchMusic(File folder,long musicL){
        searchMusicDialog = music.setDialog(this,"扫描音乐","搜索中，请稍候……",false);
        searchMusicDialog.show();
        new Thread(()->{
            synchronized (musicList){
                musicList = music.getMusicList(folder,musicL);
                Log.d(TAG, "searchMusic: musicList="+ Arrays.deepToString(musicList.toArray()));
                Log.d(TAG, "searchMusic: =null" + (db==null));
            }

            if(musicList.size()==0){
                runOnUiThread(()->{
                    Toast.makeText(this,"没有找到大于"+musicL+"的音乐",Toast.LENGTH_SHORT).show();
                });
            }else if(musicList.size()>0 && db!=null){
                String result = helper.getSelected(db,"select * from Music");
                Log.d(TAG, "search_result===" + result);
                String[] paths = result.split("\n");
                for(int i=0; i<paths.length && !paths[i].equals(""); i++){
                    beforeInsertList.add(new File(paths[i]));
                }
                Log.d(TAG, "searchMusic: beforeInsertList=" + beforeInsertList.size());
//                测试用的
//                int[] test = {1,3,5};
//                int[] test2 = {1,3,5};
//                Set<Object> testSet = new LinkedHashSet<>();
//                Set<Object> testSet2 = new LinkedHashSet<>();
//                for(int j=0; j<test.length; j++){
//                    testSet.add(test[j]);
//                }
//                for(int j2=0; j2<test2.length; j2++){
//                    testSet2.add(test2[j2]);
//                }
//                music.deleteSameFile(testSet,testSet2);
                List<File> tmpList = new ArrayList<>();
                tmpList.addAll(music.different(beforeInsertList,musicList));
                Log.d(TAG, "searchMusic: tmpList=" + tmpList.size());
                musicList.removeAll(musicList);
                musicList.addAll(tmpList);
                Log.d(TAG, "searchMusic: musicList的长度=" + musicList.size());
                beforeInsertList.removeAll(beforeInsertList);
                if(musicList.size()>0){

                    //添加到数据库中
                    helper.insertMydb(musicList);
                    Log.d(TAG, "searchMusic: 执行过insertMydb");
                    result = helper.getSelected(db,"select * from Music");
                    Log.d(TAG, "插入后数据库查询结果" + result);

                    musicList.removeAll(musicList);
                    Log.d(TAG, "searchMusic: 删除后的长度=" + beforeInsertList.size());
                }

            }

            runOnUiThread(()->{
                searchMusicDialog.dismiss();
                //这里不能使用synchronized，不然会添加重复歌曲
            });

        }).start();
    }

    Pattern p = Pattern.compile("\\d+");
    Matcher m;
    String editText ="";
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if(s!=null){
            Log.d(TAG, "beforeTextChanged: s="+s);
        }

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s!=null)
        Log.d(TAG, "onTextChanged: s="+s);

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s!=null)
        Log.d(TAG, "afterTextChanged: s="+s);
        editText = new String(String.valueOf(s));
        Log.d(TAG, "afterTextChanged: editText=" +editText);
        m = p.matcher(editText.toString());
        if(m.matches()){
            customL = Integer.parseInt(editText);
        }else if(user_custom.getText().toString().equals("")){
            customL = 0;
        }else if(editText.contains("\n")){
            //收起键盘
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(user_custom.getWindowToken(), 0);
            Log.d(TAG, "afterTextChanged: s.contains=" + editText + "哦");
            user_custom.setText(editText.replace("\n",""));
            Log.d(TAG, "afterTextChanged: s.delete=" + s.toString());
            user_custom.setCursorVisible(false);
        } else{
            user_custom.setText("");
            Toast.makeText(this,"只能输入数字",Toast.LENGTH_SHORT).show();
            user_custom.setCursorVisible(true);
            customRadio.setEnabled(true);
        }

    }

}
