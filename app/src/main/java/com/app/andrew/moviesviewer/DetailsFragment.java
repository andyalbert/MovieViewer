package com.app.andrew.moviesviewer;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.app.andrew.moviesviewer.Adapters.ReviewsAdapter;
import com.app.andrew.moviesviewer.DataHolder.Movie;
import com.app.andrew.moviesviewer.DataHolder.Review;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

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
    private RecyclerView recyclerView;
    private ReviewsAdapter reviewsAdapter;
    private ArrayList<Review> reviews;
//    private Bitmap poster;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_fragment, container);
        movie = (Movie) getArguments().getSerializable(getString(R.string.movie_data));

        //// TODO: 10/20/16 check for reviews and trailers, if nothing exist, then make it invisible 
        recyclerView = (RecyclerView) view.findViewById(R.id.reviews_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
 //       recyclerView.setItemAnimator(new DefaultItemAnimator());
        ReviewsTask reviewsTask = new ReviewsTask();
        reviewsTask.execute(movie.getId());


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
 //       imageView.getLayoutParams().width = MainActivity.IMAGE_WIDTH;
        Picasso.with(getActivity()).load(movie.getUrl()).into(imageView);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    class ReviewsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(reviews.size() > 0){
                reviewsAdapter = new ReviewsAdapter(reviews);
                recyclerView.setAdapter(reviewsAdapter);
            }
        }

        @Override
        protected Void doInBackground(String... args) {
            if(args.length == 0)
                return null;
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/" + args[0] + "/reviews?api_key=" + getString(R.string.movie_db_key) + "&language=en-US ");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";

                JSONObject json = new JSONObject(result);
                JSONArray arr = json.getJSONArray("results");
                reviews = new ArrayList<>();
                Review review;
                for(int i = 0;i < arr.length();i++){
                    review = new Review();
                    review.setAuthor(arr.getJSONObject(i).getString("author"));
                    review.setDescription(arr.getJSONObject(i).getString("content"));
                    reviews.add(review);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
