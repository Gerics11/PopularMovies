package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.popularmovies.data.MovieContract.*;


class MovieDbHelper extends SQLiteOpenHelper {
    //name of the table created in sql
    private static final String DATABASE_NAME = "movies.db";
    //current version of the database
    private static final int DATABASE_VERSION = 1;
    //MovieDbHelper Constructor
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //SQL command to create the table
        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                        MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_MOVIEID + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_RELEASEDATE + " TEXT, " +
                        MovieEntry.COLUMN_IMAGE + " BLOB, " +
                        MovieEntry.COLUMN_RATING + " DOUBLE NOT NULL, " +
                        MovieEntry.COLUMN_PLOT + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
