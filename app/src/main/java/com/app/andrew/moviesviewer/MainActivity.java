package com.app.andrew.moviesviewer;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.DataInteraction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import com.app.andrew.moviesviewer.DataBase.DataBaseContract;
import com.app.andrew.moviesviewer.DataBase.DataBaseHelper;
import com.app.andrew.moviesviewer.DataHolder.DataBaseInsertionData;
import com.app.andrew.moviesviewer.DataHolder.Movie;
import com.app.andrew.moviesviewer.DataHolder.Review;
import com.app.andrew.moviesviewer.DataHolder.Trailer;
import com.app.andrew.moviesviewer.utilities.NetworkConnection;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainFragment.SetDetailsFragmentData, DetailsFragment.InsertIntoDataBase {
    public static int IMAGE_WIDTH;
    public static int IMAGE_HEIGHT;
    private boolean twoPanes;
    private View view;
    private InsertIntoDataBaseTask dataBaseTask;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("width", IMAGE_WIDTH);
        outState.putInt("height", IMAGE_HEIGHT);
        outState.putBoolean("twopanes", twoPanes);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        IMAGE_HEIGHT = savedInstanceState.getInt("height");
        IMAGE_WIDTH = savedInstanceState.getInt("width");
        twoPanes = savedInstanceState.getBoolean("twopanes");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.activity_main);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        //final ActionBar bar = getSupportActionBar();
       // bar.setDisplayHomeAsUpEnabled(true);
        //bar.setDisplayUseLogoEnabled(true);
        if(savedInstanceState != null)
            return;
        setImageDimentions();
        getFragmentManager().beginTransaction().replace(R.id.main_view, new MainFragment()).commit();
        if(findViewById(R.id.secondary_view) == null){
            twoPanes = false;

        }else{
            twoPanes = true;
            //todo check this

        }
/*
        if(savedInstanceState == null){
            getFragmentManager().beginTransaction().replace(R.id.activity_main, new MainFragment()).commit();
            setImageDimentions();
        }
*/
    }

    public void setImageDimentions(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int lengh = size.y;

        IMAGE_WIDTH = width / 2;
        IMAGE_HEIGHT = lengh / 2;
    }

    @Override
    public void insert(DataBaseInsertionData data) {
        dataBaseTask = new InsertIntoDataBaseTask();
        data.setActivity(MainActivity.this);
        dataBaseTask.execute(data);
    }
/*
    class InsertIntoDataBaseTask extends AsyncTask<DataBaseInsertionData, Void, Void> {
        @Override
        protected Void doInBackground(DataBaseInsertionData... params) {
            DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
            Movie movie = params[0].getMovie();
            if (params[0].isAdd()) { //add
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
*/
    @Override
    public void loadLocalData(Movie movie, boolean b) {
        if(twoPanes){
            //// TODO: 11/2/2016 fill here 
        }else{
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(getString(R.string.movie_data), movie);
            intent.putExtra(getString(R.string.is_favourite_key), true);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void loadNetworkData(Movie movie) {
        if(twoPanes){
            //// TODO: 11/2/2016 fill here 
        }else{
            if (!NetworkConnection.isConnected(this)) {
                Snackbar.make(view, getString(R.string.no_internet_message), Snackbar.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(getString(R.string.movie_data), movie);
            startActivity(intent);
        }
    }
}
