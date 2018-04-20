package com.example.mohamed.moviesapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mohamed.moviesapp.adapter.ReviewsAdapter;
import com.example.mohamed.moviesapp.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@SuppressLint("StaticFieldLeak")
public class LoadReviews extends AsyncTask<String, Void, JSONObject> {

    OnLoadReviews onLoadReviews;

    public LoadReviews (OnLoadReviews onLoadReviews){
        this.onLoadReviews = onLoadReviews;
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

        onLoadReviews.OnLoadReviewsFinished(jsonObject);
    }

    public interface OnLoadReviews{
        void OnLoadReviewsFinished(JSONObject jsonObject);
    }
}