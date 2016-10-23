package com.app.andrew.moviesviewer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.app.andrew.moviesviewer.DataHolder.Trailer;
import com.app.andrew.moviesviewer.R;

import java.util.ArrayList;

/**
 * Created by Andrew on 10/20/2016.
 */

public class TrailersViewAdapter extends ArrayAdapter<Trailer> {
    private Context context;
    private ArrayList<Trailer> trailers;
    public TrailersViewAdapter(Context context, int resource, ArrayList<Trailer> trailers) {
        super(context, resource);
        this.trailers = trailers;
        this.context = context;
    }

    @Override
    public int getCount() {
        return trailers.size();
    }

    @Nullable
    @Override
    public Trailer getItem(int position) {
        return trailers.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.movie_layout, parent, false);

            holder = new ViewHolder();
            holder.imageView = (ImageView)convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        }else
            holder = (ViewHolder) convertView.getTag();
        holder.imageView.setImageResource(R.mipmap.trailer_ic);
        return convertView;
    }

    class ViewHolder{
        public ImageView imageView;
    }
}
