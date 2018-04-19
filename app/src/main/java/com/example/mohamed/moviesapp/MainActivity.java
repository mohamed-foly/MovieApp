package com.example.mohamed.moviesapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.mohamed.moviesapp.adapter.ImageAdapter;
import com.example.mohamed.moviesapp.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ArrayList<Movie> moviesList;
    GridView gridview;
    final String API_KEY= "9fbabd5af02f8d12d6a8a625de92559b";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (new SharedPref(this).getAsc()) {
                new LongOperation().execute("http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY);
            } else {
                new LongOperation().execute("http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY);
            }
        }


        gridview = findViewById(R.id.gridview);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                i.putExtra("movie",  moviesList.get(position));
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case R.id.settings:
                Intent i  = new Intent(this,SettingsActivity.class);
                startActivity(i);
                break;
        }
        return true;
    }


    @SuppressLint("StaticFieldLeak")
    private class LongOperation extends AsyncTask<String, Void, JSONObject> {

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
            try {
                if (jsonObject != null) {
                    ArrayList<Movie> movies = new ArrayList<>();
                    JSONArray result = jsonObject.getJSONArray("results");
                    int resultLength = result.length();
                    for (int c = 0; c < resultLength; c++) {
                        JSONObject row = result.getJSONObject(c);
                        Movie movie = new Movie(row.optInt("id")
                                , row.optString("original_title")
                                , row.optString("poster_path")
                                , row.optString("overview")
                                , row.optDouble("vote_average")
                                , row.optString("release_date")
                        );
                        movies.add(movie);
                    }
                    Log.e("movieList", "Loaded");
                    moviesList = movies;
                    gridview.setAdapter(new ImageAdapter(getApplicationContext(), moviesList));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}



