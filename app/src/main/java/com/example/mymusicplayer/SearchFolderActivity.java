package com.example.mymusicplayer;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFolderActivity extends BasicActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{
    private List<File> musicFolder = new ArrayList<>();
    private MusicListViewAdapter adapter;
    private ListView folderListView;
    private Button folderOk;
    private Button folderBack;
    private static String folderName;
    private TextView folderTextView;
    private CheckBox folderCheck;
    private static List<File> copyFolderName = new ArrayList<>() ;
    private static boolean isFirstCreate = true;
    private static final String TAG = "SearchFolderActivity";
    private boolean intentB ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_folder);
        intentB = getIntent().getBooleanExtra("isFirstCreate",false);
        if(intentB){
            Log.d(TAG, "onCreate: isFirstCreate=" + isFirstCreate);
            selectedFolder(Environment.getExternalStorageDirectory());
            isFirstCreate=false;
        }else {
            selectedFolder(new File(getFolderName()));
            Log.d(TAG, "onCreate: getFolderName()" + getFolderName());
        }

        folderTextView = findViewById(R.id.folder_name);
        adapter = new MusicListViewAdapter(this,R.layout.folder_layout,musicFolder);

        Log.d(TAG, "onCreate: musicFolder=" + Arrays.toString(musicFolder.toArray()));

        folderListView = findViewById(R.id.folder_list);
        onItemListener(folderListView);
        adapter.setTextId(R.id.folder_name);
        folderListView.setAdapter(adapter);
        folderOk = findViewById(R.id.folder_ok);
        folderOk.setOnClickListener(this);
        folderBack = findViewById(R.id.select_folder_back);
        folderBack.setOnClickListener(this);
        folderCheck = findViewById(R.id.folder_check);
    }
    private void onItemListener(ListView listView){
        Log.d(TAG, "onItemListener: 监听器之前");
        listView.setOnItemClickListener((parent, view, position, id) -> {
//            isFirstCreate = false;
            setFolderName(musicFolder.get(position).getAbsolutePath());

            Intent intent = new Intent(this,SearchFolderActivity.class);
            startActivity(intent);
            folderCheck.setOnCheckedChangeListener(this);
//            copyFolderName.clear();
//            musicFolder = selectedFolder(musicFolder.get(position));
//            adapter = new MusicListViewAdapter(this,R.layout.activity_search_folder,musicFolder);

            Log.d(TAG, "onItemListener: get(position)" +musicFolder.get(position));
        });
    }
    public List<File> selectedFolder(File folder){
        File[] sdcard = folder.listFiles();
        for(File f : sdcard){
            if(f.isDirectory()){
                musicFolder.add(f);
            }
        }
        return musicFolder;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.folder_ok:
                Toast.makeText(this,"文件夹为" + getFolderName() ,Toast.LENGTH_SHORT).show();
                break;
            case R.id.select_folder_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
//        isFirstCreate = false;
        if(musicFolder.size()>0){
            setFolderName(musicFolder.get(0).getParentFile().getParentFile().getAbsolutePath());
            Log.d(TAG, "onDestroy: " + musicFolder.get(0).getParentFile()
                    .getParentFile().getAbsolutePath());
        }
        super.onDestroy();

    }
    public String getFolderName() {
        Log.d(TAG, "getFolderName: folderName=" + folderName);
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this,"nihao",Toast.LENGTH_SHORT).show();
    }
}
