package com.example.mymusicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlarmPlayActivity extends BasicActivity implements View.OnClickListener,TextWatcher{
    private static final String TAG = "AlarmPlayActivity";
    private Button userOk;
    private EditText startTimeUser;
    private EditText endTimeUser;
    private String input = "";
    private Pattern p = Pattern.compile("\\d+");;
    private Matcher m;
    private long startTime =0;
    private long endTime =0;
    private boolean selectStart = false;
    private boolean selectEnd = false;
    private boolean isDelete = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_play);
        userOk = findViewById(R.id.user_ok);
        startTimeUser = findViewById(R.id.start_time_edit);
        endTimeUser = findViewById(R.id.end_time_edit);
        startTimeUser.addTextChangedListener(this);
        endTimeUser.addTextChangedListener(this);
        startTimeUser.setOnClickListener(this);
        endTimeUser.setOnClickListener(this);
        userOk.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_ok:
//                Toast.makeText(this,"startTime=" + startTime,Toast.LENGTH_SHORT).show();

                if(!startTimeUser.getText().toString().equals("")){
                    startTime = Long.parseLong(startTimeUser.getText().toString());
                    Toast.makeText(this,"开始时间：" +startTime,Toast.LENGTH_SHORT).show();
                }
                if(!endTimeUser.getText().toString().equals("")){
                    endTime = Long.parseLong(endTimeUser.getText().toString());
                    Toast.makeText(this,"结束时间：" +endTime,Toast.LENGTH_SHORT).show();
                }
                if(startTimeUser.getText().toString().equals("")&&
                        endTimeUser.getText().toString().equals("")){
                    Toast.makeText(this,"输入为空",Toast.LENGTH_SHORT).show();
                }else {
                    endTimeUser.setEnabled(false);
                    startTimeUser.setEnabled(false);
                    userOk.setEnabled(false);
                    Intent alarmService = new Intent(this,AlarmPlayService.class);
                    alarmService.putExtra("startTime",startTime);
                    alarmService.putExtra("endTime",endTime);
                    startService(alarmService);
                }
                break;
            case R.id.start_time_edit:
                selectStart = true;
                Log.d(TAG, "onClick: selectStart=" + selectStart);
                break;
            case R.id.end_time_edit:
                selectEnd = true;
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endTimeUser.setEnabled(true);
        startTimeUser.setEnabled(true);
        userOk.setEnabled(true);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(count==0){
            Toast.makeText(this,"删除了一位数",Toast.LENGTH_SHORT).show();
            isDelete = true;
        }else{
            isDelete = false;
        }
        Log.d(TAG, "onTextChanged: count=" +count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s!=null){
            input = s.toString();
            Log.d(TAG, "afterTextChanged: input=" + input);
            m = p.matcher(input);
            if(!m.matches()&&(isDelete == false)){
                s.clear();
                Toast.makeText(this,"只能输入数字",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "afterTextChanged: else中");

            }
        }
    }
}
