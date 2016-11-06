package com.app.andrew.moviesviewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import com.app.andrew.moviesviewer.DataBase.DataBaseHelper;
import com.app.andrew.moviesviewer.DataBase.InsertIntoDataBaseTask;
import com.app.andrew.moviesviewer.DataHolder.DataBaseInsertionData;
import com.app.andrew.moviesviewer.DataHolder.Movie;
import com.app.andrew.moviesviewer.utilities.NetworkConnection;

import com.app.andrew.moviesviewer.DataBase.DataBaseContract.*;

public class MainActivity extends AppCompatActivity implements MainFragment.MainFragmentListener, DetailsFragment.InsertIntoDataBase {
    public static int IMAGE_WIDTH;
    public static int IMAGE_HEIGHT;
    private boolean twoPanes;
    private View view;
    private DetailsFragment detailsFragment;
    private Bundle detailsFragmentBundle;

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

        if(savedInstanceState != null)
            return;
        setImageDimentions();
        getFragmentManager().beginTransaction().replace(R.id.main_view, new MainFragment()).commit();
        if(findViewById(R.id.secondary_view) == null)
            twoPanes = false;
        else
            twoPanes = true;
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
        InsertIntoDataBaseTask dataBaseTask = new InsertIntoDataBaseTask();
        data.setActivity(MainActivity.this);
        dataBaseTask.execute(data);
    }

    @Override
    public void loadLocalData(Movie movie, boolean b) {
        if(twoPanes){
            if(detailsFragment == null){
                detailsFragment = new DetailsFragment();
                detailsFragmentBundle = new Bundle();
                detailsFragmentBundle.putBoolean("favourite", true);
                detailsFragmentBundle.putSerializable(getString(R.string.movie_data), movie);
                detailsFragment.setArguments(detailsFragmentBundle);
                getFragmentManager().beginTransaction().replace(R.id.secondary_view, detailsFragment).commit();
            } else {
                detailsFragment.updateDatabase();
                detailsFragment.update(true, movie);
            }
        }else{
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(getString(R.string.movie_data), movie);
            intent.putExtra(getString(R.string.is_favourite_key), true);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void loadNetworkData(Movie movie) {
        if (!NetworkConnection.isConnected(this)) {
            Snackbar.make(view, getString(R.string.no_internet_message), Snackbar.LENGTH_SHORT).show();
            return;
        }
        if(twoPanes) {
            if(detailsFragment == null){
                detailsFragment = new DetailsFragment();
                detailsFragmentBundle = new Bundle();
                detailsFragmentBundle.putBoolean("favourite", false);
                detailsFragmentBundle.putSerializable(getString(R.string.movie_data), movie);
                detailsFragment.setArguments(detailsFragmentBundle);
                getFragmentManager().beginTransaction().replace(R.id.secondary_view, detailsFragment).commit();
            }else{
                detailsFragment.updateDatabase();
                detailsFragment.update(false, movie);
            }

        }else{
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(getString(R.string.movie_data), movie);
            startActivity(intent);
        }
    }

    @Override
    public void removeAllFromDataBase() {
        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.movie_viewer_pref), Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
        preferences.edit().putBoolean("isEmpty", true).commit();
        EmptyTheDataBase emptyTheDataBase = new EmptyTheDataBase();
        emptyTheDataBase.execute();
    }

    @Override
    public void clearDetailsFragment() {
        if(twoPanes && detailsFragment != null){
            detailsFragment.updateDatabase();
            getFragmentManager().beginTransaction().remove(detailsFragment).commit();
            detailsFragment = null;
        }
    }

    class EmptyTheDataBase extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            DataBaseHelper helper = new DataBaseHelper(MainActivity.this);
            helper.getReadableDatabase().delete(ReviewTable.TABLE_NAME, null, null);
            helper.getReadableDatabase().delete(TrailerTable.TABLE_NAME, null, null);
            helper.getReadableDatabase().delete(MovieTable.TABLE_NAME, null, null);
            helper.close();
            return null;
        }
    }
}
