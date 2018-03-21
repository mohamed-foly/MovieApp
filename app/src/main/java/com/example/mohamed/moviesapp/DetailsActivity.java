package com.example.mohamed.moviesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mohamed.moviesapp.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle bundle = getIntent().getExtras();

        if(bundle !=null) {
            Movie movie = (Movie) bundle.get("movie");
            if (movie != null) {
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

            }
        }
    }
}
