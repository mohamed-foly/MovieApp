package com.example.mohamed.moviesapp;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mohamed.moviesapp.adapter.ReviewsAdapter;
import com.example.mohamed.moviesapp.adapter.VideosAdapter;
import com.example.mohamed.moviesapp.model.Movie;
import com.example.mohamed.moviesapp.model.Review;
import com.example.mohamed.moviesapp.model.Video;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity implements LongOperation.OnLoadingFinish ,LoadReviews.OnLoadReviews {

    final String API_KEY = BuildConfig.THE_MOVIE_DB_API_TOKEN;

    RecyclerView VideosRecycler;
    RecyclerView ReviewsRecycler;

    FavoritesProvider favoritesProvider;
    Movie movie ;



    LinearLayoutManager videosLinearLayoutManager;
    LinearLayoutManager reviewsLinearLayoutManager;


    ArrayList<Video> videos ;
    VideosAdapter videosAdapter;

    ArrayList<Review> reviews;
    ReviewsAdapter reviewsAdapter;


    final String STATE_REVIEWS_KEY = "reviews";
    final String STATE_VIDEOS_KEY= "videos";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ReviewsRecycler = findViewById(R.id.ReviewsRecycler);
        reviewsLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        favoritesProvider = new FavoritesProvider(getApplicationContext());

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
                setTitle(movie.getOriginalTitle());
            }
        }


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

        VideosAdapter.OnShareClickListener onShareClickListener = new VideosAdapter.OnShareClickListener() {
            @Override
            public void OnShareClick(Video item) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v=" + item.getKey());
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        ReviewsAdapter.OnReviewClickListener onReviewClickListener  = new ReviewsAdapter.OnReviewClickListener() {
            @Override
            public void OnReviewClick(Review item) {

            }
        };



        if (savedInstanceState != null) {

            videos = savedInstanceState.getParcelableArrayList(STATE_VIDEOS_KEY);
            reviews = savedInstanceState.getParcelableArrayList(STATE_REVIEWS_KEY);

        }else{
            videos =new ArrayList<>();
            reviews =new ArrayList<>();
            GetApiData();
        }


        //initialize videos recycler
        VideosRecycler = findViewById(R.id.VideosRecycler);
        videosLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        videosAdapter = new VideosAdapter(getApplicationContext(),videos , onVideoClickListener , onShareClickListener);
        VideosRecycler.setAdapter(videosAdapter);
        VideosRecycler.setLayoutManager(videosLinearLayoutManager);
        //-------//

        reviewsAdapter = new ReviewsAdapter(getApplicationContext(),reviews,onReviewClickListener );
        ReviewsRecycler.setAdapter(reviewsAdapter);
        ReviewsRecycler.setLayoutManager(reviewsLinearLayoutManager);


    }

    private void GetApiData(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        if (activeNetwork != null && activeNetwork.isConnected()) {
            new LongOperation(this).execute("http://api.themoviedb.org/3/movie/"+ movie.getId() + "/videos?api_key="+ API_KEY);
            new LoadReviews(this).execute("http://api.themoviedb.org/3/movie/"+ movie.getId() + "/reviews?api_key="+ API_KEY);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);

       if(favoritesProvider.isFavorite(movie.getId())){
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
                    favoritesProvider.delete(FavoritesProvider.CONTENT_URI,null,
                            new String[]{
                            String.valueOf(movie.getId())
                    });
                }else{
                    item.setChecked(true);
                    item.setIcon(R.drawable.ic_star);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FavoritesProvider.ID, movie.getId());
                    contentValues.put(FavoritesProvider.TITLE, movie.getOriginalTitle());
                    contentValues.put(FavoritesProvider.POSTER, movie.getPosterPath());
                    contentValues.put(FavoritesProvider.OVERVIEW, movie.getOverview());
                    contentValues.put(FavoritesProvider.VOTE, movie.getVoteAverage());
                    contentValues.put(FavoritesProvider.RELEASE, movie.getReleaseDate());
                    favoritesProvider.insert(FavoritesProvider.CONTENT_URI,contentValues);

                }
                break;
        }
        return true;
    }

    @Override
    public void LoadingFinished(JSONObject jsonObject) {
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

                this.videos = videos;
                videosAdapter.swapData(videos);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnLoadReviewsFinished(JSONObject jsonObject) {
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

                this.reviews = reviews;
                reviewsAdapter.swapData(reviews);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelableArrayList(STATE_REVIEWS_KEY,reviews);
        savedInstanceState.putParcelableArrayList(STATE_VIDEOS_KEY,videos);
        super.onSaveInstanceState(savedInstanceState);
    }

}