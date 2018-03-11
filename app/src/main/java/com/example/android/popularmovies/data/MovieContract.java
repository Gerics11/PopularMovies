package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class MovieContract {

        public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
        //base uri to build from
        public static final Uri CONTENT_BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        //path to database
        public static final String PATH_MOVIES = "movies";

        //class to contain database constants
        public static final class MovieEntry implements BaseColumns {
            //uri of the database
            public static final Uri CONTENT_URI = CONTENT_BASE_URI.buildUpon()
                    .appendPath(PATH_MOVIES).build();
            public static final String TABLE_NAME = "movies";

            public static final String COLUMN_TITLE = "title";
            public static final String COLUMN_MOVIEID = "movieid";
            public static final String COLUMN_RELEASEDATE = "releasedate";
            public static final String COLUMN_IMAGE = "image";
            public static final String COLUMN_RATING = "rating";
            public static final String COLUMN_PLOT = "plot";
        }
}
