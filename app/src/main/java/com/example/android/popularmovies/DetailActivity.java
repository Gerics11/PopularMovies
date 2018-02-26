package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView ivPoster = findViewById(R.id.iv_poster);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvReleaseDate = findViewById(R.id.tv_releasedate);
        TextView tvRating = findViewById(R.id.tv_rating);
        TextView tvPlot = findViewById(R.id.tv_plot);

        Movie movie = getIntent().getExtras().getParcelable("movie");

        Picasso.with(DetailActivity.this).load(movie.getImage()).into(ivPoster);
        tvTitle.setText(movie.getTitle());

        String releaseDateString = getString(R.string.release_substring) + movie.getReleaseDate();
        tvReleaseDate.setText(releaseDateString);

        String ratingText = getString(R.string.rating_substring) + movie.getRating();
        tvRating.setText(ratingText);

        tvPlot.setText(movie.getPlot());
    }
}
