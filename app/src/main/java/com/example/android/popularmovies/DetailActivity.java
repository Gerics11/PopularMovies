package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private Movie movie;

    private LinearLayout trailerLayout;
    private LinearLayout reviewLayout;
    private ImageView ivPoster;
    private ImageView favoriteButton;

    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //init views
        reviewLayout = findViewById(R.id.layout_reviews);
        trailerLayout = findViewById(R.id.layout_trailers);
        favoriteButton = findViewById(R.id.button_favorite);
        ivPoster = findViewById(R.id.iv_poster);

        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvReleaseDate = findViewById(R.id.tv_releasedate);
        TextView tvRating = findViewById(R.id.tv_rating);
        TextView tvPlot = findViewById(R.id.tv_plot);

        movie = getIntent().getExtras().getParcelable("movie");

        isFavorite = checkIfFavorite(Integer.valueOf(movie.getMovieId()));
        //set fav button color
        Drawable d = VectorDrawableCompat.create(getResources(), R.drawable.ic_star, null);
        d = DrawableCompat.wrap(d);
        favoriteButton.setImageDrawable(d);
        if (isFavorite) {
            DrawableCompat.setTint(d, getResources().getColor(R.color.colorStarYellow));
        } else {
            DrawableCompat.setTint(d, getResources().getColor(R.color.colorBlack));
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //add and remove from favorites DB
                Drawable d = VectorDrawableCompat.create(getResources(), R.drawable.ic_star, null);
                d = DrawableCompat.wrap(d);

                favoriteButton.setImageDrawable(d);
                if (isFavorite) {
                    isFavorite = false;
                    DrawableCompat.setTint(d, getResources().getColor(R.color.colorBlack));
                    removeFromDb();

                } else {
                    isFavorite = true;
                    DrawableCompat.setTint(d, getResources().getColor(R.color.colorStarYellow));
                    addToDb();
                }
            }
        });

        if (movie.getImage() != null) {         //load image from net
            Picasso.with(DetailActivity.this).load(movie.getImage()).into(ivPoster);
        } else {                                //load image from db
            String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIEID,
                    MovieContract.MovieEntry.COLUMN_IMAGE};
            String whereClause = MovieContract.MovieEntry.COLUMN_MOVIEID + "=?";
            String[] selectionArgs = {String.valueOf(movie.getMovieId())};
            Uri queryUri = MovieContract.CONTENT_BASE_URI.buildUpon()
                    .appendPath(MovieContract.PATH_MOVIES)
                    .appendPath(String.valueOf(movie.getMovieId()))
                    .build();

            ContentResolver contentResolver = getContentResolver();

            Cursor cursor = contentResolver.query(queryUri, projection, whereClause, selectionArgs, null);
            cursor.moveToFirst();
            byte[] imageBlob = cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE));
            Drawable image = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length));
            ivPoster.setImageDrawable(image);
            cursor.close();
        }
        //fill textviews
        tvTitle.setText(movie.getTitle());

        String releaseDateString = getString(R.string.release_substring) + movie.getReleaseDate();
        tvReleaseDate.setText(releaseDateString);

        String ratingText = getString(R.string.rating_substring) + movie.getRating();
        tvRating.setText(ratingText);

        tvPlot.setText(movie.getPlot());

        Thread loadExtraThread; //load extras if connection is available
        if (MainActivity.checkForInternetConnection(this)) {
            loadExtraThread = new Thread(loadExtras);
            loadExtraThread.start();
        }
    }

    private void populateView(LinearLayout layout, List<List<String>> array) {
        switch (layout.getId()) {
            case R.id.layout_trailers:
                for (final List<String> strings : array) {
                    TextView textView = new TextView(this);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    layoutParams.setMargins(0, 26, 0, 26);
                    textView.setLayoutParams(layoutParams);

                    textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);

                    String text = getString(R.string.trailer_textview_substring) + strings.get(1) +
                            getString(R.string.trailer_textview_substring2) + strings.get(0);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "http://www.youtube.com/watch?v=" + strings.get(2)
                            ));
                            startActivity(videoIntent);
                        }
                    });
                    textView.setText(text);
                    trailerLayout.addView(textView);
                }
                break;
            case R.id.layout_reviews:
                for (List<String> strings : array) {
                    LinearLayout linearLayout = new LinearLayout(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 26, 0, 26);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    linearLayout.setLayoutParams(layoutParams);

                    ImageView imageView = new ImageView(this);
                    imageView.setBackgroundResource(R.drawable.ic_user);

                    TextView textView = new TextView(this);
                    String text = strings.get(0) + "\n\n" + strings.get(1);
                    textView.setText(text);

                    linearLayout.addView(imageView);
                    linearLayout.addView(textView);

                    trailerLayout.addView(linearLayout);
                }
                break;
        }
    }

    private final Runnable loadExtras = new Runnable() {
        @Override
        public void run() {
            String jsonString = JsonUtils.getJsonString(JsonUtils.createQueryUrl
                    (JsonUtils.URL_TRAILERS.replace("##", String.valueOf(movie.getMovieId()))));
            final List<List<String>> trailers = JsonUtils.fetchTrailersFromJson(jsonString);
            jsonString = JsonUtils.getJsonString(JsonUtils.createQueryUrl
                    (JsonUtils.URL_REVIEWS.replace("##", String.valueOf(movie.getMovieId()))));
            final List<List<String>> reviews = JsonUtils.fetchReviewsFromJson(jsonString);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateView(trailerLayout, trailers);
                    populateView(reviewLayout, reviews);
                }
            });
        }
    };

    private boolean checkIfFavorite(int id) {
        String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIEID};
        String whereClause = MovieContract.MovieEntry.COLUMN_MOVIEID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        Uri queryUri = MovieContract.CONTENT_BASE_URI.buildUpon()
                .appendPath(MovieContract.PATH_MOVIES)
                .appendPath(String.valueOf(id))
                .build();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(queryUri, projection, whereClause, selectionArgs, null);

        boolean fav = cursor.moveToFirst();
        cursor.close();

        return fav;
    }

    private void addToDb() {
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(MovieEntry.COLUMN_MOVIEID, movie.getMovieId());
        values.put(MovieEntry.COLUMN_RELEASEDATE, movie.getReleaseDate());
        values.put(MovieEntry.COLUMN_IMAGE, getImage());
        values.put(MovieEntry.COLUMN_RATING, movie.getRating());
        values.put(MovieEntry.COLUMN_PLOT, movie.getPlot());

        ContentResolver resolver = getContentResolver();
        resolver.insert(MovieEntry.CONTENT_URI, values);

        isFavorite = true;
    }

    private void removeFromDb() {
        String whereClause = MovieContract.MovieEntry.COLUMN_MOVIEID + "=?";
        String[] selectionArgs = {String.valueOf(movie.getMovieId())};
        Uri deleteUri = MovieContract.CONTENT_BASE_URI.buildUpon()
                .appendPath(MovieContract.PATH_MOVIES)
                .appendPath(String.valueOf(movie.getMovieId()))
                .build();
        ContentResolver resolver = getContentResolver();

        resolver.delete(deleteUri, whereClause, selectionArgs);
    }

    private byte[] getImage() {
        Bitmap bitmap = ((BitmapDrawable) ivPoster.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

        return bos.toByteArray();
    }


}
