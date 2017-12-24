package com.example.mymusicplayer;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by wentaodeng on 2017/12/20.
 */

public class URLConnectionUtil {

    public static void sendOkHttp(String urlPath, Callback callback){
        Log.d("URLConnectionUtil", "sendOkHttp: urlPath="+urlPath);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(urlPath).build();
        client.newCall(request).enqueue(callback);
    }
    public static String  getInputFromHttp(String urlPath,boolean useHttp) throws IOException {
        URL url = new URL(urlPath);
        Log.d(TAG, "getInputFromHttp: url=" + urlPath);
        InputStream is = null;
        String resultText = "" ;
        switch (useHttp + ""){
            case true+"":
                HttpURLConnection hConnect = (HttpURLConnection) url.openConnection();
                hConnect.setDoOutput(true);
                hConnect.setDoInput(true);
                hConnect.connect();
                is = hConnect.getInputStream();
                break;
            case false+"":
                is = url.openStream();
                break;
        }
        int ind ;
        byte[] bytes = new byte[1024];
        while((ind = is.read(bytes))>-1){
            resultText += new String(bytes,0,ind);
        }
        is.close();
        Log.d(TAG, "getInputFromHttp: resultText=" + resultText);
        return resultText;
    }
}
