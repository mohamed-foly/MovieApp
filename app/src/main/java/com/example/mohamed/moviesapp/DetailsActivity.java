package com.example.mohamed.moviesapp;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.moviesapp.adapter.ReviewsAdapter;
import com.example.mohamed.moviesapp.adapter.VideosAdapter;
import com.example.mohamed.moviesapp.model.Movie;
import com.example.mohamed.moviesapp.model.Review;
import com.example.mohamed.moviesapp.model.Video;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

//  /movie/{id}/videos

    final String API_KEY= "9fbabd5af02f8d12d6a8a625de92559b";

    RecyclerView VideosRecycler;
    RecyclerView ReviewsRecycler;

    DatabaseHelper databaseHelper ;
Movie movie ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        VideosRecycler = findViewById(R.id.VideosRecycler);
        ReviewsRecycler = findViewById(R.id.ReviewsRecycler);

        databaseHelper = new DatabaseHelper(getApplicationContext());

        Bundle bundle = getIntent().getExtras();

        if(bundle !=null) {
            Movie movie = (Movie) bundle.get("movie");
            if (movie != null) {
                this.movie = movie;
                Log.e("Movie Details",movie.getOriginalTitle());
                String imageUrl = "http://image.tmdb.org/t/p/w185/";
                ImageView poster  = findViewById(R.id.movieposter);
                TextView title = findViewById(R.id.movieTitle);
                TextView rate  = findViewById(R.id.voteAverage);
                TextView date = findViewById(R.id.releaseDate);
                TextView overview = findViewById(R.id.overview);
                Picasso.with(this).load(imageUrl +movie.getPosterPath()).fit().into(poster);
                title.setText(movie.getOriginalTitle());
                rate.setText(String.valueOf(movie.getVoteAverage()));
                date.setText(movie.getReleaseDate());
                overview.setText(movie.getOverview());

                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = null;
                if (cm != null) {
                    activeNetwork = cm.getActiveNetworkInfo();
                }
                if (activeNetwork != null && activeNetwork.isConnected()) {
                    new DetailsActivity.LongOperation().execute("http://api.themoviedb.org/3/movie/"+ movie.getId() + "/videos?api_key="+ API_KEY);
                    new DetailsActivity.LoadReviews().execute("http://api.themoviedb.org/3/movie/"+ movie.getId() + "/reviews?api_key="+ API_KEY);

                }


            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);

       if(databaseHelper.isFavorite(movie.getId())){
           menu.findItem(R.id.star_btn).setChecked(true);
           menu.findItem(R.id.star_btn).setIcon(R.drawable.ic_star);


       }else{
           menu.findItem(R.id.star_btn).setChecked(false);
           menu.findItem(R.id.star_btn).setIcon(R.drawable.ic_star_border);

       }


        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.star_btn:
                if (item.isChecked()){
                    item.setChecked(false);
                    item.setIcon(R.drawable.ic_star_border);
                    databaseHelper.deleteById(movie.getId());
                }else{
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_star);
                    if ( databaseHelper.insert(movie.getId(),
                                    movie.getOriginalTitle(),
                                    movie.getPosterPath(),
                                    movie.getOverview(),
                                    movie.getVoteAverage(),
                                    movie.getReleaseDate()
                            ))
                    {
                        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
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
                    ArrayList<Video> videos = new ArrayList<>();
                    JSONArray result = jsonObject.getJSONArray("results");
                    int resultLength = result.length();
                    for (int c = 0; c < resultLength; c++) {
                        JSONObject row = result.getJSONObject(c);
                        Video video = new Video(row.optString("key"),
                                row.optString("name"));
                        videos.add(video);
                    }
                    Log.e("movieList", "Loaded");


                    VideosAdapter.OnVideoClickListener onVideoClickListener = new VideosAdapter.OnVideoClickListener() {
                        @Override
                        public void OnVideoClick(Video item) {
                            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + item.getKey()));
                            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + item.getKey()));
                            try {
                                getApplicationContext().startActivity(appIntent);
                            } catch (ActivityNotFoundException ex) {
                                getApplicationContext().startActivity(webIntent);
                            }
                        }
                    };
                    VideosAdapter videosAdapter = new VideosAdapter(getApplicationContext(),videos , onVideoClickListener);
                    VideosRecycler.setAdapter(videosAdapter);
                    VideosRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class LoadReviews extends AsyncTask<String, Void, JSONObject> {

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
                    ArrayList<Review> reviews = new ArrayList<>();
                    JSONArray result = jsonObject.getJSONArray("results");
                    int resultLength = result.length();
                    for (int c = 0; c < resultLength; c++) {
                        JSONObject row = result.getJSONObject(c);
                        Review review = new Review(row.optString("author"),
                                row.optString("content"),
                                row.optString("id"),
                                row.optString("url")
                        );
                        reviews.add(review);
                    }
                    Log.e("movieList", "Loaded");

                    ReviewsAdapter.OnReviewClickListener onReviewClickListener  = new ReviewsAdapter.OnReviewClickListener() {
                        @Override
                        public void OnReviewClick(Review item) {

                        }
                    };

                    ReviewsAdapter videosAdapter = new ReviewsAdapter(getApplicationContext(),reviews,onReviewClickListener );
                    ReviewsRecycler.setAdapter(videosAdapter);
                    ReviewsRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
