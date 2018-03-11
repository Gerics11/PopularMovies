package com.example.android.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


class JsonUtils {

    //TODO INSERT KEY TO QUERY
    private static final String apiKey = "";

    static final String URL_POPULAR = "https://api.themoviedb.org/3/movie/popular?api_key=" + apiKey;
    static final String URL_TOPRATED = "http://api.themoviedb.org/3/movie/top_rated?api_key=" + apiKey;


    static final String URL_TRAILERS = "https://api.themoviedb.org/3/movie/##/trailers?api_key=" + apiKey;
    static final String URL_REVIEWS = "https://api.themoviedb.org/3/movie/##/reviews?api_key=" + apiKey;

    static final String SORTING_POPULAR = "Popular";
    static final String SORTING_TOPRATED = "Top Rated";
    static final String SORTING_FAVORITES = "Favorites";

    static URL createQueryUrl(String urlToParse) {
        URL queryURL = null;
        try {
            queryURL = new URL(urlToParse);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return queryURL;
    }

    static String getJsonString(URL url) {
        HttpURLConnection urlConnection;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(15000);
            urlConnection.connect();

            reader = new BufferedReader(new InputStreamReader
                    (urlConnection.getInputStream(), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    static List<Movie> readMoviesFromJson(String jsonToRead) {
        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject baseJson = new JSONObject(jsonToRead);
            JSONArray resultsArray = baseJson.getJSONArray("results");
            //populate movies list
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultAtIndex = resultsArray.getJSONObject(i);
                String title = resultAtIndex.getString("title");
                String movieId = resultAtIndex.getString("id");
                String releaseDate = resultAtIndex.getString("release_date");
                String image = resultAtIndex.getString("poster_path");
                double rating = resultAtIndex.getDouble("vote_average");
                String plot = resultAtIndex.getString("overview");

                movies.add(new Movie(title, movieId, releaseDate, image, rating, plot));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    static List<List<String>> fetchTrailersFromJson(String jsonToRead) {
        List<List<String>> trailers = new ArrayList<>();

        try {
            JSONObject baseJson = new JSONObject(jsonToRead);
            JSONArray resultsArray = baseJson.getJSONArray("youtube");
            //populate movies list
            for (int i = 0; i < resultsArray.length(); i++) {
                List<String> values = new ArrayList<>();

                JSONObject resultAtIndex = resultsArray.getJSONObject(i);
                values.add(resultAtIndex.getString("name"));
                values.add(resultAtIndex.getString("type"));
                values.add(resultAtIndex.getString("source"));

                trailers.add(values);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    static List<List<String>> fetchReviewsFromJson(String jsonToRead) {
        List<List<String>> reviews = new ArrayList<>();

        try {
            JSONObject baseJson = new JSONObject(jsonToRead);
            JSONArray resultsArray = baseJson.getJSONArray("results");
            //populate movies list
            for (int i = 0; i < resultsArray.length(); i++) {
                List<String> values = new ArrayList<>();

                JSONObject resultAtIndex = resultsArray.getJSONObject(i);
                values.add(resultAtIndex.getString("author"));
                values.add(resultAtIndex.getString("content"));

                reviews.add(values);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
