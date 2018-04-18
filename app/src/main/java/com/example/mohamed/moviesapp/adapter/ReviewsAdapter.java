package com.example.mohamed.moviesapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mohamed.moviesapp.R;
import com.example.mohamed.moviesapp.model.Review;

import java.util.ArrayList;

public class ReviewsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Review> reviewArrayList;

    private final OnReviewClickListener listener;

    public ReviewsAdapter(Context context ,ArrayList<Review> reviewArrayList ,OnReviewClickListener onReviewClickListener){
        this.reviewArrayList = reviewArrayList;
        this.context  = context;
        this.listener = onReviewClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.review_row,parent,false);

        return new ReviewRow(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ReviewRow)holder).content_tv.setText(reviewArrayList.get(position).getContent());
        ((ReviewRow)holder).author_tv.setText(reviewArrayList.get(position).getAuthor());
        ((ReviewRow)holder).bind(reviewArrayList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    public class ReviewRow extends RecyclerView.ViewHolder{

        TextView content_tv ;
        TextView author_tv ;
        ReviewRow(View itemView) {
            super(itemView);
            content_tv = itemView.findViewById(R.id.review_row_content);
            author_tv = itemView.findViewById(R.id.review_row_author);

        }
        void bind(final Review item, final OnReviewClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnReviewClick(item);
                }
            });
        }
    }


    public interface OnReviewClickListener {
        void OnReviewClick(Review item);
    }
}
