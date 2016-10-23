package com.app.andrew.moviesviewer;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.andrew.moviesviewer.Adapters.ReviewsAdapter;
import com.app.andrew.moviesviewer.Adapters.TrailersViewAdapter;
import com.app.andrew.moviesviewer.DataBase.DataBaseContract.*;
import com.app.andrew.moviesviewer.DataBase.DataBaseHelper;
import com.app.andrew.moviesviewer.DataHolder.Movie;
import com.app.andrew.moviesviewer.DataHolder.Review;
import com.app.andrew.moviesviewer.DataHolder.Trailer;
import com.app.andrew.moviesviewer.utilities.ImageConverter;

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
    private ArrayList<Trailer> trailers;
    private GridView trailersGridView;
    private TrailersViewAdapter trailersViewAdapter;
    private TextView reviewHeader;
    private View reviewSeparator;
    private TextView trailersHeader;
    private View trailersSeparator;
    private DataBaseManagementTask dataBaseManagementTask;
    private boolean currentState;
    private boolean originalState; // used to detect whether to change the db state or not
    private SharedPreferences preferences;
    private Activity activity;
    private DownloadImageTask downloadImageTask;
    //    private Bitmap poster;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movie = (Movie) getArguments().getSerializable(getString(R.string.movie_data));
        setHasOptionsMenu(true);
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details_menu, menu);
        MenuItem item = menu.findItem(R.id.item_is_favourite);
        if (preferences.getBoolean(movie.getId(), false)) {
            item.setIcon(R.mipmap.ic_favourite);
            originalState = true;//available in db
        }
        else
            originalState = false; // not in the database
        currentState = originalState;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_is_favourite:
                SharedPreferences.Editor editor = preferences.edit();
                if (preferences.getBoolean(movie.getId(), false)) { //movie is favourite, remove it
                    item.setIcon(R.mipmap.ic_not_favourite);
                    editor.remove(movie.getId());
                    editor.apply();
                    currentState = false;
                } else {
                    item.setIcon(R.mipmap.ic_favourite);
                    editor.putBoolean(movie.getId(), true);
                    editor.apply();
                    currentState = true;
                }
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
            default:
                return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(currentState != originalState){
            dataBaseManagementTask = new DataBaseManagementTask();
            dataBaseManagementTask.execute(currentState);
            originalState = currentState;
        }
        Toast.makeText(activity, "stop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context; //used for the getdatabase in the asynctask, in case the activity is closeds
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_fragment, container);
        imageView = (ImageView) view.findViewById(R.id.movie_image);
        downloadImageTask = new DownloadImageTask();
        downloadImageTask.execute(movie.getUrl());

        originalState = false;
        recyclerView = (RecyclerView) view.findViewById(R.id.reviews_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        trailersGridView = (GridView) view.findViewById(R.id.trailers_gridview);
        //       recyclerView.setItemAnimator(new DefaultItemAnimator());
        reviewSeparator = view.findViewById(R.id.reviews_separator);
        reviewHeader = (TextView) view.findViewById(R.id.review_header);
        trailersHeader = (TextView) view.findViewById(R.id.trailers_header);
        trailersSeparator = view.findViewById(R.id.trailers_separator);
        ReviewsAndTrailersTask reviewsAndTrailersTask = new ReviewsAndTrailersTask();
        reviewsAndTrailersTask.execute(movie.getId());


        titleText = (TextView) view.findViewById(R.id.movie_title_text);
        overviewText = (TextView) view.findViewById(R.id.overview_text);
        releaseDataText = (TextView) view.findViewById(R.id.release_date);
        ratingBar = (RatingBar) view.findViewById(R.id.movie_rating);



        ratingBar.setRating((float) (movie.getRating() / 2));
        releaseDataText.setText(movie.getDate());
        titleText.setText(movie.getTitle());
        overviewText.setText(movie.getOverview());

//        imageView.getLayoutParams().height = MainActivity.IMAGE_HEIGHT;
        //       imageView.getLayoutParams().width = MainActivity.IMAGE_WIDTH;
        //Picasso.with(getActivity()).load(movie.getUrl()).into(imageView); //todo is this right ? i replaced it with imagemap
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    class ReviewsAndTrailersTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (reviews.size() > 0) {
                reviewSeparator.setVisibility(View.VISIBLE);
                reviewHeader.setVisibility(View.VISIBLE);
                reviewsAdapter = new ReviewsAdapter(reviews);
                recyclerView.setAdapter(reviewsAdapter);
                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setVisibility(View.VISIBLE);
            }
            if (trailers.size() > 0) {
                trailersHeader.setVisibility(View.VISIBLE);
                trailersSeparator.setVisibility(View.VISIBLE);
                trailersViewAdapter = new TrailersViewAdapter(getActivity(), R.id.trailers_gridview, trailers);
                trailersGridView.setAdapter(trailersViewAdapter);
                trailersGridView.setVisibility(View.VISIBLE);
                trailersGridView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return false;
                    }

                });
                trailersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(getString(R.string.youtube_link) + trailers.get(position).getUrl()));
//                        intent.setPackage("com.android.chrome");
                        startActivity(Intent.createChooser(intent, "Complete action using"));
                    }
                });
            }
        }

        @Override
        protected Void doInBackground(String... args) {
            if (args.length == 0)
                return null;
            reviews = new ArrayList<>();
            trailers = new ArrayList<>();
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/" + args[0] + "/videos?api_key=" + getString(R.string.movie_db_key) + "&language=en-US ");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";

                JSONObject json = new JSONObject(result);
                JSONArray arr = json.getJSONArray("results");
                Trailer trailer;
                for (int i = 0; i < arr.length(); i++) {
                    trailer = new Trailer();
                    trailer.setUrl(arr.getJSONObject(i).getString("key"));
                    trailers.add(trailer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/" + args[0] + "/reviews?api_key=" + getString(R.string.movie_db_key) + "&language=en-US ");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";

                JSONObject json = new JSONObject(result);
                JSONArray arr = json.getJSONArray("results");
                Review review;
                for (int i = 0; i < arr.length(); i++) {
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

    class DataBaseManagementTask extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... params) {
            DataBaseHelper helper = new DataBaseHelper(activity);
            if (params[0]) { //add
                ContentValues values = new ContentValues();
                values.put(MovieTable._ID, movie.getId());
                values.put(MovieTable.COLUMN_IMAGE, movie.getImage());
                values.put(MovieTable.COLUMN_DATE, movie.getDate());
                values.put(MovieTable.COLUMN_IMAGE_URL, movie.getUrl());
                values.put(MovieTable.COLUMN_OVERVIEW, movie.getOverview());
                values.put(MovieTable.COLUMN_RATING, movie.getRating());
                values.put(MovieTable.COLUMN_TITLE, movie.getTitle());
                helper.getWritableDatabase().insert(MovieTable.TABLE_NAME, null, values);

                for (int i = 0; i < trailers.size(); i++) {
                    values = new ContentValues();
                    values.put(TrailerTable.COLUMN_URL, trailers.get(i).getUrl());
                    values.put(TrailerTable.COLUMN_REFERENCE, movie.getId());
                    helper.getWritableDatabase().insert(TrailerTable.TABLE_NAME, null, values);
                }

                for (int i = 0; i < reviews.size(); i++) {
                    values = new ContentValues();
                    values.put(ReviewTable.COLUMN_AUTHOR, reviews.get(i).getAuthor());
                    values.put(ReviewTable.COLUMN_COMMENT, reviews.get(i).getDescription());
                    values.put(ReviewTable.COLUMN_REFERENCE, movie.getId());
                    helper.getWritableDatabase().insert(ReviewTable.TABLE_NAME, null, values);
                }
            } else {
                helper.getWritableDatabase().delete(ReviewTable.TABLE_NAME, ReviewTable.COLUMN_REFERENCE + " =  ?", new String[]{movie.getId()});
                helper.getWritableDatabase().delete(TrailerTable.TABLE_NAME, TrailerTable.COLUMN_REFERENCE + " =  ?", new String[]{movie.getId()});
                helper.getWritableDatabase().delete(MovieTable.TABLE_NAME, MovieTable._ID + " =  ?", new String[]{movie.getId()});
            }
            helper.close();
            return null;
        }
    }

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            byte[] byteImgae = ImageConverter.bitmapTobyte(bitmap);
            movie.setImage(byteImgae);
            if(bitmap != null)
                imageView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL imageUrl = new URL(params[0]);
                InputStream in = imageUrl.openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}
