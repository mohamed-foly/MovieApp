package com.example.mohamed.moviesapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("StaticFieldLeak")
public class LongOperation extends AsyncTask<String, Void, JSONObject> {

    private OnLoadingFinish onLoadingFinish;

    LongOperation(OnLoadingFinish onLoadingFinish) {
        this.onLoadingFinish = onLoadingFinish;
    }
    @Override
    protected JSONObject doInBackground(String... params) {
        InputStream inputStream;
        String result;
        JSONObject jsonObject;
        // HTTP
        try {
            URL mURL = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET");

            inputStream = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            inputStream.close();
            result = sb.toString();

            jsonObject = new JSONObject(result);

        } catch(Exception e) {
            return null;
        }

        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        onLoadingFinish.LoadingFinished(jsonObject);
    }


    public interface OnLoadingFinish {
        void LoadingFinished(JSONObject jsonObject);

    }
}