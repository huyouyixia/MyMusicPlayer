package com.example.mymusicplayer;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by wentaodeng on 2017/12/11.
 */

public class MusicListViewAdapter extends ArrayAdapter {
    private int resId;
    private ListViewTextColor textColor;
    private List<File> musicList;
    private static final String TAG="MusicListViewAdapter";
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        File musicFile = musicList.get(position);
        Log.d(TAG, "getView: musicFile="+musicFile.getName());
        View view;
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resId,parent,false);
        } else{
            view = convertView;
        }
        TextView musicText = view.findViewById(R.id.music_name);
        musicText.setText(musicFile.getName());
        textColor = new ListViewTextColor();
        if(textColor.isSuccess()){
            musicText.setTextColor(Color.WHITE);
        }

        return view;
    }

    public MusicListViewAdapter(Context context, int textViewResourceId, List objects) {
        super(context, textViewResourceId, objects);
        resId = textViewResourceId;
        musicList = objects;
    }
}
