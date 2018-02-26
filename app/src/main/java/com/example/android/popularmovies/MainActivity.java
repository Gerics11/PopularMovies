package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private MovieLoader movieLoader;
    //default sorting is Popularity
    private static String sorting = JsonUtils.URI_POPULAR;

    private static final int LOADER_ID = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview_main);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        if (!checkForInternetConnection()) {
            return;
        }
        startLoader();
        movieLoader.forceLoad();
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

    private boolean checkForInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()
                == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()
                == NetworkInfo.State.CONNECTED) {
            return true;
        }
        Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        return false;
    }


    @Override
    public MovieLoader onCreateLoader(int i, Bundle bundle) {
        //return movieLoader;
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
            URL url = JsonUtils.createQueryUrl(sorting);
            List<Movie> data = JsonUtils.readDataFromJson(JsonUtils.getJsonString(url));
            return data;
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
        if (!checkForInternetConnection()) {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.menu_switchsorting:
                if (sorting.equals(JsonUtils.URI_POPULAR)) {
                    sorting = JsonUtils.URI_TOP_RATED;
                    ab.setTitle(getString(R.string.sort_rating));
                    startLoader();
                } else {
                    sorting = JsonUtils.URI_POPULAR;
                    movieLoader.reset();
                    ab.setTitle(getString(R.string.sort_popularity));
                    startLoader();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
