package com.example.mohamed.moviesapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LongOperation.OnLoadingFinish {
    ArrayList<Movie> moviesList;
    GridView gridview;
    final String API_KEY= BuildConfig.THE_MOVIE_DB_API_TOKEN;
    final String STATE_STARS_KEY= "stars";
    final String STATE_LIST_POS= "list_position";
    boolean isFavoriteMode = false;
    FavoritesProvider favoritesProvider ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //databaseHelper = new DatabaseHelper(getApplicationContext());
        favoritesProvider = new FavoritesProvider(getApplicationContext());
        moviesList = new ArrayList<>();

        gridview = findViewById(R.id.gridview);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                i.putExtra("movie",  moviesList.get(position));
                startActivity(i);
            }
        });
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "https://www.themoviedb.org/movie/"+ moviesList.get(position).getId());
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false ;
            }
        });



        if (savedInstanceState != null) {
            // Restore value of members from saved state
            isFavoriteMode = savedInstanceState.getBoolean(STATE_STARS_KEY);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFavoriteMode){
            GetLocalData();
        }else {
            GetApiData();
        }


    }

    private void GetApiData(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if (activeNetwork != null && activeNetwork.isConnected()) {
            if (new SharedPref(this).getAsc()) {
                new LongOperation(this).execute("http://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY);
            } else {
                new LongOperation(this).execute("http://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY);
            }
        }else{
            ClearGrid();
            //GetLocalData();
        }
    }

    private void GetLocalData(){
        if (moviesList != null){
            moviesList.clear();
        }
        //Cursor cursor = databaseHelper.getData();
        //Cursor cursor = getContentResolver().query(FavoritesProvider.CONTENT_URI,null,"*",null,null);
        Cursor cursor = favoritesProvider.query(FavoritesProvider.CONTENT_URI,null,"*",null,null);
        if (cursor != null) {
            while (cursor.moveToNext()){
                Movie movie = new Movie(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getString(5)
                );

                moviesList.add(movie);
            }
            cursor.close();
        }

        gridview.setAdapter(new ImageAdapter(getApplicationContext(), moviesList));
    }

    private void ClearGrid(){
        moviesList.clear();
        gridview.setAdapter(new ImageAdapter(getApplicationContext(), moviesList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items, menu);

        if (isFavoriteMode){
            menu.findItem(R.id.stars_btn).setIcon(R.drawable.ic_stars);

        }else{
            menu.findItem(R.id.stars_btn).setIcon(R.drawable.ic_star);
        }


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
            case R.id.stars_btn:
                if(isFavoriteMode){
                    item.setIcon(R.drawable.ic_star);
                    isFavoriteMode = false;
                    GetApiData(); //clear offline Data and load From API
                }else{
                    item.setIcon(R.drawable.ic_stars);
                    isFavoriteMode = true;
                    GetLocalData(); //Refresh Screen with favorites
                }
                break;
        }
        return true;
    }




    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_STARS_KEY, isFavoriteMode);
        savedInstanceState.putInt(STATE_LIST_POS,gridview.getFirstVisiblePosition());

        super.onSaveInstanceState(savedInstanceState);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null)
            gridview.smoothScrollToPosition(state.getInt(STATE_LIST_POS));
    }





    @Override
    public void LoadingFinished(JSONObject jsonObject) {
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
            }else {
                ClearGrid();
            }
        } catch (JSONException e) {
            ClearGrid();
            e.printStackTrace();
        }
    }
}