package com.app.andrew.moviesviewer.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.app.andrew.moviesviewer.DataHolder.Movie;
import com.app.andrew.moviesviewer.R;
import com.squareup.picasso.Picasso;

/**
 * Created by andrew on 10/5/16.
 */

public class MovieViewadapter extends ArrayAdapter<Movie> {
    private Context context;
    private Movie[] movies;

    public MovieViewadapter(Context context, int resource, Movie[] movies) {
        super(context, resource);
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.length;
    }

    @Nullable
    @Override
    public Movie getItem(int position) {
        return movies[position];
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.movie_layout, parent, false);
            holder = new ViewHolder();
            holder.setImageView((ImageView)convertView.findViewById(R.id.image));
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();
        Picasso.with(context).load(movies[position].getUrl()).into(holder.getImageView());
    //    holder.getImageView().getLayoutParams().width = MainActivity.IMAGE_WIDTH;
//        ImageView view = (ImageView) convertView.findViewById(R.id.image);
//        view.getLayoutParams().width = MainActivity.IMAGE_WIDTH;
//        view.getLayoutParams().height = MainActivity.IMAGE_HEIGHT;

        //// TODO: 10/5/16 test .resize(width, height), i think the picasso will be changed
//        Picasso.with(context).load(movies[position].getUrl()).into(view);
        return convertView;
    }
    static class ViewHolder{
        private ImageView imageView;

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }
    }
}