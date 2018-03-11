package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private final Context context;
    private final List<Movie> movies;

    MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.itemTitle.setText(movie.getTitle());
        if (movie.getImage() != null) {             //load image from net
            Picasso.with(context).load(movie.getImage()).into(holder.itemImage);
        } else {                                    //load image from database
            String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIEID,
                    MovieContract.MovieEntry.COLUMN_IMAGE};
            String whereClause = MovieContract.MovieEntry.COLUMN_MOVIEID + "=?";
            String[] selectionArgs = {String.valueOf(movie.getMovieId())};
            Uri queryUri = MovieContract.CONTENT_BASE_URI.buildUpon()
                    .appendPath(MovieContract.PATH_MOVIES)
                    .appendPath(String.valueOf(movie.getMovieId()))
                    .build();

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(queryUri, projection, whereClause, selectionArgs, null);
            cursor.moveToFirst();
            //decode and show drawable from bytearray
            byte[] imageBlob = cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE));
            Drawable image = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length)); //todo FIX
            holder.itemImage.setImageDrawable(image);
            cursor.close();
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener {

        final ImageView itemImage;
        final TextView itemTitle;

        MovieAdapterViewHolder(View view) {
            super(view);
            itemImage = view.findViewById(R.id.item_image);
            itemTitle = view.findViewById(R.id.item_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) { //open detail activity
            Movie movie = movies.get(getAdapterPosition());
            Intent detailActivityIntent = new Intent(context, DetailActivity.class);
            detailActivityIntent.putExtra("movie", movie);
            context.startActivity(detailActivityIntent);
        }
    }
}
