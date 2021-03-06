package com.app.andrew.moviesviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.andrew.moviesviewer.DataBase.InsertIntoDataBaseTask;
import com.app.andrew.moviesviewer.DataHolder.DataBaseInsertionData;
import com.app.andrew.moviesviewer.DataHolder.Movie;

public class DetailsActivity extends AppCompatActivity implements DetailsFragment.InsertIntoDataBase{
    private Movie movie;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("movies", movie);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movie = (Movie) savedInstanceState.getSerializable("movies");
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(savedInstanceState == null){
            movie = (Movie) getIntent().getSerializableExtra(getString(R.string.movie_data));
            Bundle bundle = new Bundle();
            bundle.putSerializable(getString(R.string.movie_data), movie);
            bundle.putBoolean("favourite", getIntent().getBooleanExtra(getString(R.string.is_favourite_key), false));
            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.details_activity, fragment).commit();
        }
    }

    @Override
    public void insert(DataBaseInsertionData data) {
        InsertIntoDataBaseTask dataBaseTask = new InsertIntoDataBaseTask();
        data.setActivity(DetailsActivity.this);
        dataBaseTask.execute(data);
    }
}
