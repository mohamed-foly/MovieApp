package com.example.mohamed.moviesapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mohamed.moviesapp.R;
import com.example.mohamed.moviesapp.model.Video;

import java.util.ArrayList;

public class VideosAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Video> videoArrayList;

    private final OnVideoClickListener listener;
    private final OnShareClickListener onShareClickListener;

    public VideosAdapter(Context context ,ArrayList<Video> videoArrayList ,OnVideoClickListener onVideoClickListener ,OnShareClickListener onShareClickListener){
        this.videoArrayList = videoArrayList;
        this.context  = context;
        this.listener = onVideoClickListener;
        this.onShareClickListener = onShareClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.video_row,parent,false);
        return new VideoRow(row);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((VideoRow)holder).name_tv.setText(videoArrayList.get(position).getName());

        ((VideoRow)holder).bind(videoArrayList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public class VideoRow extends RecyclerView.ViewHolder{

        TextView name_tv ;
        ImageButton share_btn;
        VideoRow(View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.video_row_name);
            share_btn = itemView.findViewById(R.id.share_btn);
        }
        void bind(final Video item, final OnVideoClickListener listener ) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnVideoClick(item);
                }
            });

            share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShareClickListener.OnShareClick(item);
                }
            });

        }
    }


    public interface OnVideoClickListener {
        void OnVideoClick(Video item);
    }
    public interface OnShareClickListener {
        void OnShareClick(Video item);
    }


    public void swapData(ArrayList<Video> videos) {
        videoArrayList = videos;  // assign the passed-in `movies` to our `moviesList`
        notifyDataSetChanged(); // refresh the list
    }
}
