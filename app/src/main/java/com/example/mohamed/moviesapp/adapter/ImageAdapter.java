package com.example.mohamed.moviesapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.mohamed.moviesapp.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Movie> moviesList;

    public ImageAdapter(Context c, ArrayList<Movie> moviesList) {

        mContext = c;
        this.moviesList= moviesList;
    }

    public int getCount() {
        return moviesList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        String imageUrl = "http://image.tmdb.org/t/p/w185/";
        Picasso.with(mContext).load(imageUrl +moviesList.get(position).getPosterPath()).into(imageView);

        return imageView;
    }

    public void swapData(ArrayList<Movie> movies) {
        moviesList = movies;  // assign the passed-in `movies` to our `moviesList`
        notifyDataSetChanged(); // refresh the list
    }

}