package com.app.andrew.moviesviewer.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.andrew.moviesviewer.DataHolder.Trailer;
import com.app.andrew.moviesviewer.DetailsFragment;
import com.app.andrew.moviesviewer.R;
import com.app.andrew.moviesviewer.utilities.NetworkConnection;

import java.util.ArrayList;

/**
 * Created by andrew on 10/23/16.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private ArrayList<Trailer> trailers;
    public TrailersAdapter(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_view, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.imageView.setImageResource(R.mipmap.ic_trailer);
        holder.textView.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView)itemView.findViewById(R.id.trailer_image);
            this.textView = (TextView)itemView.findViewById(R.id.trailer_name);
        }
    }
}
