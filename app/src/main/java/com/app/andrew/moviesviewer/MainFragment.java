package com.app.andrew.moviesviewer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

import com.app.andrew.moviesviewer.Adapters.MovieViewadapter;
import com.app.andrew.moviesviewer.DataBase.DataBaseHelper;
import com.app.andrew.moviesviewer.DataHolder.Movie;
import com.app.andrew.moviesviewer.utilities.NetworkConnection;
import com.app.andrew.moviesviewer.DataBase.DataBaseContract.*;

/**
 * Created by andrew on 10/5/16.
 */

public class MainFragment extends Fragment {
    private GridView gridView;
    private MovieViewadapter movieViewadapter;
    private ArrayList<Movie> movies;
    private DataFetshingTask fetshingTask;
    private View view;
    private int optionMenuState = 1;
    private LocalDataFetchingTask localDataFetchingTask;
    private DataBaseHelper helper;
    private MainFragmentListener mainFragmentListener;
    private Menu menu;
    private boolean clearEnabled;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        this.menu = menu;
        if(clearEnabled)
            menu.getItem(3).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        clearEnabled = false;
        menu.getItem(3).setVisible(false);
        switch (item.getItemId()) {
            case R.id.item_top_rated:
                if (!NetworkConnection.isConnected(getActivity())) {
                    Snackbar.make(view, getString(R.string.no_internet_message), Snackbar.LENGTH_SHORT).show();
                    return false;
                }
                mainFragmentListener.clearDetailsFragment();
                optionMenuState = 1;
                loadData(getString(R.string.top_rated));
                return true;
            case R.id.item_most_popular:
                if (!NetworkConnection.isConnected(getActivity())) {
                    Snackbar.make(view, getString(R.string.no_internet_message), Snackbar.LENGTH_SHORT).show();
                    return false;
                }
                mainFragmentListener.clearDetailsFragment();
                optionMenuState = 2;
                loadData(getString(R.string.popular));
                return true;
            case R.id.item_favourite:
                optionMenuState = 3;
                mainFragmentListener.clearDetailsFragment();
                localDataFetchingTask = new LocalDataFetchingTask();
                localDataFetchingTask.execute();
                if(!getActivity().getSharedPreferences(getString(R.string.movie_viewer_pref), Context.MODE_PRIVATE).getBoolean("isEmpty", false)){
                    menu.getItem(3).setVisible(true);
                    clearEnabled = true;
                }
                return true;
            case R.id.item_clear_all:
                mainFragmentListener.removeAllFromDataBase();
                gridView.setAdapter(null);
                movies = new ArrayList<>();
                item.setVisible(false);
                mainFragmentListener.clearDetailsFragment();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        switch (optionMenuState) {
            case 1:
                menu.getItem(0).setEnabled(false);
                menu.getItem(1).setEnabled(true);
                menu.getItem(2).setEnabled(true);
                return;
            case 2:
                menu.getItem(0).setEnabled(true);
                menu.getItem(1).setEnabled(false);
                menu.getItem(2).setEnabled(true);
                return;
            case 3:
                menu.getItem(0).setEnabled(true);
                menu.getItem(1).setEnabled(true);
                menu.getItem(2).setEnabled(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainFragmentListener = (MainFragmentListener)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearEnabled = false;
        setHasOptionsMenu(true);
        setRetainInstance(true);//// TODO: 11/5/2016 check 
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            movies = (ArrayList<Movie>) savedInstanceState.getSerializable("movies");
            clearEnabled = savedInstanceState.getBoolean("clear");
            optionMenuState = savedInstanceState.getInt("menuState");
            movieViewadapter = new MovieViewadapter(getActivity(), R.id.movies_gridview, movies);
            gridView.setAdapter(movieViewadapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (optionMenuState == 3) {
                        mainFragmentListener.loadLocalData(movies.get(i), true);
                      /*  Intent intent = new Intent(getActivity(), DetailsActivity.class);
                        intent.putExtra(getString(R.string.movie_data), movies.get(i));
                        intent.putExtra(getString(R.string.is_favourite_key), true);
                        startActivityForResult(intent, 1);*/
                    } else {
                        mainFragmentListener.loadNetworkData(movies.get(i));
                       /* if (!NetworkConnection.isConnected(getActivity())) {
                            Snackbar.make(view, getString(R.string.no_internet_message), Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(getActivity(), DetailsActivity.class);
                        intent.putExtra(getString(R.string.movie_data), movies.get(i));
                        startActivity(intent);*/
                    }
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("movies", movies);
        outState.putInt("menuState", optionMenuState);
        outState.putBoolean("clear", clearEnabled);
        super.onSaveInstanceState(outState);
    }

 /*   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK/* && optionMenuState == 3) {
            localDataFetchingTask = new LocalDataFetchingTask();
            localDataFetchingTask.execute();
        }
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container);
        gridView = (GridView) view.findViewById(R.id.movies_gridview);
        //   Toast.makeText(getActivity(), "view", Toast.LENGTH_SHORT).show();
        if (savedInstanceState == null)
            loadData(getString(R.string.top_rated));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void loadData(String source) {
        fetshingTask = new DataFetshingTask();
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
                    mainFragmentListener.loadNetworkData(movies.get(i));
                  /*  if (!NetworkConnection.isConnected(getActivity())) {
                        Snackbar.make(view, getString(R.string.no_internet_message), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(getString(R.string.movie_data), movies.get(i));
                    startActivity(intent);*/
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
                movies = new ArrayList<>();
                Movie movie;
                for (int i = 0; i < arr.length(); i++) {
                    movie = new Movie();
                    movie.setRating(arr.getJSONObject(i).getDouble("vote_average"));
                    movie.setTitle(arr.getJSONObject(i).getString("title"));
                    movie.setDate(arr.getJSONObject(i).getString("release_date").substring(0, 4));
                    movie.setUrl(getString(R.string.image_url) + arr.getJSONObject(i).getString("poster_path"));
                    movie.setOverview(arr.getJSONObject(i).getString("overview"));
                    movie.setId(String.valueOf(arr.getJSONObject(i).getInt("id")));
                    movies.add(movie);
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

    class LocalDataFetchingTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            gridView.setAdapter(movieViewadapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mainFragmentListener.loadLocalData(movies.get(position), true);
                  /*  Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra(getString(R.string.movie_data), movies.get(position));
                    intent.putExtra(getString(R.string.is_favourite_key), true);
                    startActivityForResult(intent, 1);*/
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {
            helper = new DataBaseHelper(getActivity());
            Cursor cursor = helper.getReadableDatabase().query(MovieTable.TABLE_NAME, null, null, null, null, null, null);
            movies = new ArrayList<>();
            Movie movie;
            while (cursor.moveToNext()) {
                movie = new Movie();
                movie.setDate(cursor.getString(4));
                movie.setId(cursor.getString(0));
                movie.setOverview(cursor.getString(5));
                movie.setRating(cursor.getDouble(3));
                movie.setTitle(cursor.getString(1));
                movie.setImage(cursor.getBlob(6));
                movie.setUrl(null);
                movies.add(movie);
            }
            movieViewadapter = new MovieViewadapter(getActivity(), R.id.movies_gridview, movies);

            return null;
        }
    }

    public interface MainFragmentListener {
        void loadLocalData(Movie movie, boolean b);
        void loadNetworkData(Movie movie);
        void removeAllFromDataBase();
        void clearDetailsFragment();
    }
}