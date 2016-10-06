package com.app.andrew.moviesviewer;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

import utilities.NetworkConnection;

/**
 * Created by andrew on 10/5/16.
 */

public class DetailsFragment extends Fragment {
    private Movie movie;
    private TextView titleText;
    private TextView releaseDataText;
    private TextView overviewText;
    private RatingBar ratingBar;
    private ImageView imageView;
    private Bitmap poster;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_fragment, container);
        movie = getArguments().getParcelable(getString(R.string.movie_data));
        titleText = (TextView)view.findViewById(R.id.movie_title_text);
        overviewText = (TextView)view.findViewById(R.id.overview_text);
        releaseDataText = (TextView) view.findViewById(R.id.release_date);
        ratingBar = (RatingBar)view.findViewById(R.id.movie_rating);
        imageView = (ImageView) view.findViewById(R.id.movie_image);

        ratingBar.setRating((float) (movie.getRating() / 2));
        releaseDataText.setText(movie.getDate());
        titleText.setText(movie.getTitle());
        overviewText.setText(movie.getOverview());

//        imageView.getLayoutParams().height = MainActivity.IMAGE_HEIGHT;
        imageView.getLayoutParams().width = MainActivity.IMAGE_WIDTH;

        ImageLoader loader = new ImageLoader();
        loader.execute();


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    class ImageLoader extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageView.setImageBitmap(poster);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                poster = BitmapFactory.decodeStream(new URL(movie.getUrl()).openConnection().getInputStream());
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
