package com.app.andrew.moviesviewer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.app.andrew.moviesviewer.DataHolder.Movie;
import com.app.andrew.moviesviewer.R;
import com.app.andrew.moviesviewer.utilities.ImageConverter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieViewadapter extends ArrayAdapter<Movie> {
    private Context context;
    private ArrayList<Movie> movies;

    public MovieViewadapter(Context context, int resource, ArrayList<Movie> movies) {
        super(context, resource);
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Nullable
    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.movie_layout, parent, false);
            holder = new ViewHolder();
            holder.setImageView((ImageView) convertView.findViewById(R.id.image));
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        if (movies.get(position).getUrl() == null){
            Bitmap image = ImageConverter.bytetoBitmap(movies.get(position).getImage());
            holder.getImageView().setImageBitmap(image);
        } else {
            Picasso.with(context).load(movies.get(position).getUrl()).into(holder.getImageView());
        }
        return convertView;
    }

    static class ViewHolder {
        private ImageView imageView;

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }
    }
}
