package com.app.andrew.moviesviewer;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.core.deps.guava.base.Charsets;
import android.support.test.espresso.core.deps.guava.io.CharStreams;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

import utilities.NetworkConnection;

/**
 * Created by andrew on 10/5/16.
 */

public class MainFragment extends Fragment{
    private GridView gridView;
    private MovieViewadapter movieViewadapter;
    private Movie[] movies;
    private DataFetshingTask fetshingTask;
    private View view;
    boolean optionMenuState = false;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_top_rated:
                optionMenuState = false;
                loadData(getString(R.string.top_rated));
                return true;
            case R.id.item_most_popular:
                optionMenuState = true;
                loadData(getString(R.string.popular));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(optionMenuState){
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(false);
        }else {
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container);
        gridView = (GridView)view.findViewById(R.id.movies_gridview);
     //   Toast.makeText(getActivity(), "view", Toast.LENGTH_SHORT).show();
        loadData(getString(R.string.top_rated));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void loadData(String source){
        fetshingTask = new DataFetshingTask();
        if(!NetworkConnection.isConnected(getActivity()))
            Snackbar.make(view, getString(R.string.no_internet_message), Snackbar.LENGTH_SHORT).show();
        else
            //// TODO: 10/6/16 remove the key
            fetshingTask.execute(source, getString(R.string.movie_db_key));
    }

    class DataFetshingTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            gridView.setAdapter(movieViewadapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(!NetworkConnection.isConnected(getActivity()))
                    {
                        Snackbar.make(view, getString(R.string.no_internet_message), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(getString(R.string.movie_data), movies[i]);
                    startActivity(intent);
                }
            });
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL("https://api.themoviedb.org/3/movie/" + strings[0] + "?api_key=" + strings[1] + "&language=en-US ");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";

                JSONObject json = new JSONObject(result);
                JSONArray arr = json.getJSONArray("results");
                movies = new Movie[arr.length()];
                for(int i = 0;i < arr.length();i++){
                    movies[i] = new Movie();
                    movies[i].setRating(arr.getJSONObject(i).getDouble("vote_average"));
                    movies[i].setTitle(arr.getJSONObject(i).getString("title"));
                    movies[i].setDate(arr.getJSONObject(i).getString("release_date"));
                    movies[i].setUrl(getString(R.string.image_url) + arr.getJSONObject(i).getString("poster_path"));
                    movies[i].setOverview(arr.getJSONObject(i).getString("overview"));
                }

                movieViewadapter = new MovieViewadapter(getActivity(), R.id.movies_gridview, movies);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
