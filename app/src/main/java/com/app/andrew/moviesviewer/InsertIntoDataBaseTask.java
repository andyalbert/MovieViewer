package com.app.andrew.moviesviewer;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.app.andrew.moviesviewer.DataBase.DataBaseContract;
import com.app.andrew.moviesviewer.DataBase.DataBaseHelper;
import com.app.andrew.moviesviewer.DataHolder.DataBaseInsertionData;
import com.app.andrew.moviesviewer.DataHolder.Movie;
import com.app.andrew.moviesviewer.DataHolder.Review;
import com.app.andrew.moviesviewer.DataHolder.Trailer;

import java.util.ArrayList;

/**
 * Created by Andrew on 11/2/2016.
 */

public class InsertIntoDataBaseTask extends AsyncTask<DataBaseInsertionData, Void, Void> {
    @Override
    protected Void doInBackground(DataBaseInsertionData... params) {
        DataBaseHelper helper = new DataBaseHelper(params[0].getActivity());
        Movie movie = params[0].getMovie();
        if (params[0].isAdd()) { //add
            //// TODO: 11/2/2016 check this get pref
            if(params[0].getActivity().getSharedPreferences(params[0].getActivity().getString(R.string.movie_viewer_pref), Context.MODE_PRIVATE).getBoolean("isEmpty", false))
                params[0].getActivity().getSharedPreferences(params[0].getActivity().getString(R.string.movie_viewer_pref), Context.MODE_PRIVATE).edit().putBoolean("isEmpty", false).apply();

            ContentValues values = new ContentValues();
            values.put(DataBaseContract.MovieTable._ID, movie.getId());
            values.put(DataBaseContract.MovieTable.COLUMN_IMAGE, movie.getImage());
            values.put(DataBaseContract.MovieTable.COLUMN_DATE, movie.getDate());
            values.put(DataBaseContract.MovieTable.COLUMN_IMAGE_URL, movie.getUrl());
            values.put(DataBaseContract.MovieTable.COLUMN_OVERVIEW, movie.getOverview());
            values.put(DataBaseContract.MovieTable.COLUMN_RATING, movie.getRating());
            values.put(DataBaseContract.MovieTable.COLUMN_TITLE, movie.getTitle());
            helper.getWritableDatabase().insert(DataBaseContract.MovieTable.TABLE_NAME, null, values);

            ArrayList<Trailer> trailers = params[0].getTrailers();
            for (int i = 0; i < trailers.size(); i++) {
                values = new ContentValues();
                values.put(DataBaseContract.TrailerTable.COLUMN_URL, trailers.get(i).getUrl());
                values.put(DataBaseContract.TrailerTable.COLUMN_REFERENCE, movie.getId());
                values.put(DataBaseContract.TrailerTable.COLUMN_NAME, trailers.get(i).getName());
                helper.getWritableDatabase().insert(DataBaseContract.TrailerTable.TABLE_NAME, null, values);
            }

            ArrayList<Review> reviews = params[0].getReviews();
            for (int i = 0; i < reviews.size(); i++) {
                values = new ContentValues();
                values.put(DataBaseContract.ReviewTable.COLUMN_AUTHOR, reviews.get(i).getAuthor());
                values.put(DataBaseContract.ReviewTable.COLUMN_COMMENT, reviews.get(i).getComment());
                values.put(DataBaseContract.ReviewTable.COLUMN_REFERENCE, movie.getId());
                helper.getWritableDatabase().insert(DataBaseContract.ReviewTable.TABLE_NAME, null, values);
            }
        } else {
            helper.getWritableDatabase().delete(DataBaseContract.ReviewTable.TABLE_NAME, DataBaseContract.ReviewTable.COLUMN_REFERENCE + " =  ?", new String[]{movie.getId()});
            helper.getWritableDatabase().delete(DataBaseContract.TrailerTable.TABLE_NAME, DataBaseContract.TrailerTable.COLUMN_REFERENCE + " =  ?", new String[]{movie.getId()});
            helper.getWritableDatabase().delete(DataBaseContract.MovieTable.TABLE_NAME, DataBaseContract.MovieTable._ID + " =  ?", new String[]{movie.getId()});
        }
        helper.close();
        return null;
    }
}
