package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private MovieLoader movieLoader;
    //default sorting is Popularity
    private static String sorting;

    private static final String KEY_SORTING = "sorting";

    private static final int LOADER_ID = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (checkForInternetConnection(this)) {
            if (savedInstanceState != null) {
                sorting = savedInstanceState.getString(KEY_SORTING);
                switch (sorting) {
                    case JsonUtils.SORTING_POPULAR:
                        ab.setTitle(JsonUtils.SORTING_POPULAR);
                        break;
                    case JsonUtils.SORTING_TOPRATED:
                        ab.setTitle(JsonUtils.SORTING_TOPRATED);
                        break;
                    case JsonUtils.SORTING_FAVORITES:
                        ab.setTitle(JsonUtils.SORTING_FAVORITES);
                }
            } else {
                sorting = JsonUtils.SORTING_POPULAR;
            }
        } else {
            sorting = JsonUtils.SORTING_FAVORITES;
            ab.setTitle(JsonUtils.SORTING_FAVORITES);
        }
        recyclerView = findViewById(R.id.recyclerview_main);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        if (sorting.equals(JsonUtils.SORTING_FAVORITES)) {
            List<Movie> movies = getMoviesFromDb();
            adapter = new MovieAdapter(this, movies);
            recyclerView.setAdapter(adapter);
        } else {
            startLoader();
            movieLoader.forceLoad();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_SORTING, sorting);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sorting.equals(JsonUtils.SORTING_FAVORITES)) {
            List<Movie> movies = getMoviesFromDb();
            adapter = new MovieAdapter(this, movies);
            recyclerView.setAdapter(adapter);
        }
    }
    //create or restart loader
    private void startLoader() {
        LoaderManager loaderManager = getLoaderManager();
        if (movieLoader == null) {
            movieLoader = (MovieLoader) loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            movieLoader = (MovieLoader) loaderManager.restartLoader(LOADER_ID, null, this);
        }
    }

    public static boolean checkForInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
                == NetworkInfo.State.CONNECTED) {
            return true;
        }
        Toast.makeText(context, context.getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public MovieLoader onCreateLoader(int i, Bundle bundle) {
        return new MovieLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        adapter = new MovieAdapter(this, movies);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    public static class MovieLoader extends AsyncTaskLoader<List<Movie>> {

        MovieLoader(Context context) {
            super(context);
            forceLoad();
        }

        @Override
        public List<Movie> loadInBackground() {
            URL url;
            if (sorting.equals(JsonUtils.SORTING_POPULAR)) {
                url = JsonUtils.createQueryUrl(JsonUtils.URL_POPULAR);
            } else {
                url = JsonUtils.createQueryUrl(JsonUtils.URL_TOPRATED);
            }
            return JsonUtils.readMoviesFromJson(JsonUtils.getJsonString(url));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (!checkForInternetConnection(MainActivity.this)) {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.menu_switchsorting:
                if (sorting.equals(JsonUtils.SORTING_FAVORITES)) {
                    sorting = JsonUtils.SORTING_TOPRATED;
                    ab.setTitle(JsonUtils.SORTING_TOPRATED);

                    if (movieLoader == null) startLoader();
                    else movieLoader.reset();
                    startLoader();
                } else if (sorting.equals(JsonUtils.SORTING_TOPRATED)) {
                    sorting = JsonUtils.SORTING_POPULAR;
                    ab.setTitle(JsonUtils.SORTING_POPULAR);

                    if (movieLoader == null) startLoader();
                    movieLoader.reset();
                    startLoader();
                } else {
                    sorting = JsonUtils.SORTING_FAVORITES;
                    ab.setTitle(JsonUtils.SORTING_FAVORITES);

                    List<Movie> movies = getMoviesFromDb();
                    adapter = new MovieAdapter(this, movies);
                    recyclerView.setAdapter(adapter);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<Movie> getMoviesFromDb() {
        String[] projection = {
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_MOVIEID,
                MovieContract.MovieEntry.COLUMN_RELEASEDATE,
                MovieContract.MovieEntry.COLUMN_IMAGE,
                MovieContract.MovieEntry.COLUMN_RATING,
                MovieContract.MovieEntry.COLUMN_PLOT,
        };
        Uri favMoviesUri = MovieContract.MovieEntry.CONTENT_URI;
        Cursor cursor = getContentResolver().query(favMoviesUri, projection, null, null, null);

        List<Movie> movies = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                String movieId = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIEID));
                String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASEDATE));
                double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING));
                String plot = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PLOT));

                movies.add(new Movie(title, movieId, releaseDate, null, rating, plot));
            }
        } finally {
            cursor.close();
        }
        return movies;
    }
}
