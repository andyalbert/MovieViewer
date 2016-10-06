package com.app.andrew.moviesviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by andrew on 10/5/16.
 */

public class DetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        Movie movie = getIntent().getExtras().getParcelable(getString(R.string.movie_data));
        Bundle bundle = new Bundle();
        bundle.putParcelable(getString(R.string.movie_data), movie);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(R.id.details_activity, fragment).commit();
    }
}
