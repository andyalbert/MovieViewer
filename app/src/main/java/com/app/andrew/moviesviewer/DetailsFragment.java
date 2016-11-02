package com.app.andrew.moviesviewer;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.andrew.moviesviewer.Listeners.RecyclerClickListener;
import com.app.andrew.moviesviewer.Adapters.ReviewsAdapter;
import com.app.andrew.moviesviewer.Adapters.TrailersAdapter;
import com.app.andrew.moviesviewer.DataBase.DataBaseContract.*;
import com.app.andrew.moviesviewer.DataBase.DataBaseHelper;
import com.app.andrew.moviesviewer.DataHolder.Movie;
import com.app.andrew.moviesviewer.DataHolder.Review;
import com.app.andrew.moviesviewer.DataHolder.Trailer;
import com.app.andrew.moviesviewer.utilities.ImageConverter;
import com.app.andrew.moviesviewer.utilities.NetworkConnection;

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
    private RecyclerView reviewsRecyclerView;
    private ReviewsAdapter reviewsAdapter;
    private ArrayList<Review> reviews;
    private ArrayList<Trailer> trailers;
    private RecyclerView trailersRecyclerView;
    private TrailersAdapter trailersAdapter;
    private TextView reviewHeader;
    private View reviewSeparator;
    private TextView trailersHeader;
    private View trailersSeparator;
    private InsertIntoDataBaseTask insertIntoDataBaseTask;
    private boolean currentState;
    private boolean originalState; // used to detect whether to change the db state or not
    private SharedPreferences preferences;
    private Activity activity;
    private DownloadImageTask downloadImageTask;
    private boolean isFavourite;
    private ReflectLocalData localData;
    private View view;
    private RecyclerView.LayoutManager reviewLayoutManager;
    private RecyclerView.LayoutManager trailerLayoutManager;
    private Bundle savedState;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("movie", movie);
        outState.putBoolean("isfavourite", isFavourite);
        outState.putSerializable("trailers", trailers);
        outState.putSerializable("reviews", reviews);
        outState.putBoolean("currentstate", currentState);
        outState.putBoolean("originalstate", originalState);
        outState.putParcelable("reviewstate", reviewsRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelable("trailerstate", trailersRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        savedState = savedInstanceState;
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        if(savedInstanceState == null){
            movie = (Movie) getArguments().getSerializable(getString(R.string.movie_data));
            isFavourite = getArguments().getBoolean(getString(R.string.is_favourite_key));
        } else {
            movie = (Movie) savedInstanceState.getSerializable("movie");
            isFavourite = savedInstanceState.getBoolean("isfavourite");
            trailers = (ArrayList<Trailer>) savedInstanceState.getSerializable("trailers");
            reviews = (ArrayList<Review>) savedInstanceState.getSerializable("reviews");
            currentState = savedInstanceState.getBoolean("currentstate");
            originalState = savedInstanceState.getBoolean("originalstate");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(isFavourite)
            return;
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
                    editor.commit(); //todo (or apply ? )
                    currentState = false;
                } else {
                    item.setIcon(R.mipmap.ic_favourite);
                    editor.putBoolean(movie.getId(), true);
                    editor.commit();
                    currentState = true;
                }
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
                if(currentState != originalState)
                    getActivity().setResult(Activity.RESULT_OK);
            default:
                return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(currentState != originalState){
            insertIntoDataBaseTask = new InsertIntoDataBaseTask();
            insertIntoDataBaseTask.execute(currentState);
            originalState = currentState;
        }
     //   Toast.makeText(activity, "stop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
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
        this.view = view;

        //finding views
        imageView = (ImageView) view.findViewById(R.id.movie_image);
        titleText = (TextView) view.findViewById(R.id.movie_title_text);
        overviewText = (TextView) view.findViewById(R.id.overview_text);
        releaseDataText = (TextView) view.findViewById(R.id.release_date);
        ratingBar = (RatingBar) view.findViewById(R.id.movie_rating);
        reviewsRecyclerView = (RecyclerView) view.findViewById(R.id.reviews_recycler_view);
        trailersRecyclerView = (RecyclerView) view.findViewById(R.id.trailers_recycler_view);
        reviewLayoutManager  = new LinearLayoutManager(getActivity());
        trailerLayoutManager = new LinearLayoutManager(getActivity());
        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);
        trailersRecyclerView.setLayoutManager(trailerLayoutManager);
//        if(savedInstanceState != null){
//            reviewsRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("reviewstate"));
//            trailersRecyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("trailerstate"));
//
//        } else {
//
//        }

        //       reviewsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        reviewSeparator = view.findViewById(R.id.reviews_separator);
        reviewHeader = (TextView) view.findViewById(R.id.review_header);
        trailersHeader = (TextView) view.findViewById(R.id.trailers_header);
        trailersSeparator = view.findViewById(R.id.trailers_separator);

        //setting views values
        ratingBar.setRating((float) (movie.getRating() / 2));
        releaseDataText.setText(movie.getDate());
        titleText.setText(movie.getTitle());
        overviewText.setText(movie.getOverview());

        if(savedInstanceState != null){
            if(!isFavourite){
                imageView.setImageBitmap(ImageConverter.bytetoBitmap(movie.getImage()));
                if (reviews.size() > 0) {
                    setReviews();
                }
                if (trailers.size() > 0) {
                    setTrailers();
                    startTrailerItemsClickListener();
                }
            }else{
                imageView.setImageBitmap(ImageConverter.bytetoBitmap(movie.getImage()));
                if (reviews.size() > 0) {
                    setReviews();
                }
                if (trailers.size() > 0) {
                    setTrailers();
                    startTrailerItemsClickListener();
                }
            }
        } else{
            if(!isFavourite){
                downloadImageTask = new DownloadImageTask();
                downloadImageTask.execute(movie.getUrl());

                ReviewsAndTrailersTask reviewsAndTrailersTask = new ReviewsAndTrailersTask();
                reviewsAndTrailersTask.execute(movie.getId());
                //imageView.getLayoutParams().height = MainActivity.IMAGE_HEIGHT;
                //imageView.getLayoutParams().width = MainActivity.IMAGE_WIDTH;
                //Picasso.with(getActivity()).load(movie.getUrl()).into(imageView); //todo is this right ? i replaced it with imagemap
            } else {
                imageView.setImageBitmap(ImageConverter.bytetoBitmap(movie.getImage()));
                localData = new ReflectLocalData();
                localData.execute();
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    class ReviewsAndTrailersTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (reviews.size() > 0) {
                setReviews();
            }
            if (trailers.size() > 0) {
                setTrailers();
                startTrailerItemsClickListener();
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
                    trailer.setName(arr.getJSONObject(i).getString("name"));
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
                    review.setComment(arr.getJSONObject(i).getString("content"));
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

    class InsertIntoDataBaseTask extends AsyncTask<Boolean, Void, Void> {
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
                    values.put(TrailerTable.COLUMN_NAME, trailers.get(i).getName());
                    helper.getWritableDatabase().insert(TrailerTable.TABLE_NAME, null, values);
                }

                for (int i = 0; i < reviews.size(); i++) {
                    values = new ContentValues();
                    values.put(ReviewTable.COLUMN_AUTHOR, reviews.get(i).getAuthor());
                    values.put(ReviewTable.COLUMN_COMMENT, reviews.get(i).getComment());
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
            byte[] byteImage = ImageConverter.bitmapTobyte(bitmap);
            movie.setImage(byteImage);
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

    class ReflectLocalData extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if(reviews.size() > 0)
                setReviews();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(trailers.size() > 0){
                setTrailers();
                startTrailerItemsClickListener();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            DataBaseHelper helper = new DataBaseHelper(activity);

            //retrieving reviews
            Cursor cursor = helper.getReadableDatabase().query(ReviewTable.TABLE_NAME, null, ReviewTable.COLUMN_REFERENCE + " = ?", new String[]{movie.getId()}, null, null, null);
            reviews = new ArrayList<>();
            Review review;
            while (cursor.moveToNext()){
                review = new Review();
                review.setAuthor(cursor.getString(1));
                review.setComment(cursor.getString(2));
                reviews.add(review);
            }
            publishProgress();
            cursor.close();
            //retrieve traiers
             cursor = helper.getReadableDatabase().query(TrailerTable.TABLE_NAME, null, TrailerTable.COLUMN_REFERENCE + " = ?", new String[]{movie.getId()}, null, null, null);
            trailers = new ArrayList<>();
            Trailer trailer;
            while (cursor.moveToNext()){
                trailer = new Trailer();
                trailer.setUrl(cursor.getString(1));
                trailer.setName(cursor.getString(2));
                trailers.add(trailer);
            }
            cursor.close();
            return null;
        }
    }

    private void startTrailerItemsClickListener() {
        //// TODO: 10/23/16 remove this shit
        trailersRecyclerView.addOnItemTouchListener(new RecyclerClickListener(getActivity(), new RecyclerClickListener.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(NetworkConnection.isConnected(activity)){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.youtube_link) + trailers.get(position).getUrl()));
//                        intent.setPackage("com.android.chrome");
                    startActivity(Intent.createChooser(intent, "Complete action using"));
                } else
                    Snackbar.make(DetailsFragment.this.view, getString(R.string.no_internet_message), Snackbar.LENGTH_SHORT).show();
            }
        }));
    }

    private void setTrailers() {
        trailersHeader.setVisibility(View.VISIBLE);
        trailersSeparator.setVisibility(View.VISIBLE);
        trailersAdapter = new TrailersAdapter(trailers);
        trailersRecyclerView.setAdapter(trailersAdapter);
        if(savedState != null)
            trailersRecyclerView.getLayoutManager().onRestoreInstanceState(savedState.getParcelable("trailerstate"));
        trailersRecyclerView.setNestedScrollingEnabled(false);
        trailersRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setReviews() {
        reviewSeparator.setVisibility(View.VISIBLE);
        reviewHeader.setVisibility(View.VISIBLE);
        reviewsAdapter = new ReviewsAdapter(reviews);
        reviewsRecyclerView.setAdapter(reviewsAdapter);
        if(savedState != null)
            reviewsRecyclerView.getLayoutManager().onRestoreInstanceState(savedState.getParcelable("reviewstate"));
        reviewsRecyclerView.setNestedScrollingEnabled(false);
        reviewsRecyclerView.setVisibility(View.VISIBLE);
    }
}
