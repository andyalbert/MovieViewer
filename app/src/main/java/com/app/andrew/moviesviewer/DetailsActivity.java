package com.app.andrew.moviesviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.andrew.moviesviewer.DataHolder.Movie;

/**
 * Created by andrew on 10/5/16.
 */

public class DetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        Movie movie =(Movie) getIntent().getSerializableExtra(getString(R.string.movie_data));
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.movie_data), movie);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(bundle);
        if(savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(R.id.details_activity, fragment).commit();
    }
}
