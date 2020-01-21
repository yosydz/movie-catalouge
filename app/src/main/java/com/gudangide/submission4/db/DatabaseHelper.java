package com.gudangide.submission4.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "favorite";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE_MOVIE = String.format("CREATE TABLE %s"
            + " (%s INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL,"
            + " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_MOVIE_NAME,
            DatabaseContract.FavoriteColumns._ID,
            DatabaseContract.FavoriteColumns.ID,
            DatabaseContract.FavoriteColumns.POSTER,
            DatabaseContract.FavoriteColumns.TITLE,
            DatabaseContract.FavoriteColumns.RATING,
            DatabaseContract.FavoriteColumns.GENRE
    );

    private static final String SQL_CREATE_TABLE_TV = String.format("CREATE TABLE %s"
                    + " (%s INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL,"
                    + " %s TEXT NOT NULL)",
            DatabaseContract.TABLE_TV_NAME,
            DatabaseContract.FavoriteColumns._ID,
            DatabaseContract.FavoriteColumns.ID,
            DatabaseContract.FavoriteColumns.POSTER,
            DatabaseContract.FavoriteColumns.TITLE,
            DatabaseContract.FavoriteColumns.RATING,
            DatabaseContract.FavoriteColumns.GENRE
    );

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(SQL_CREATE_TABLE_TV);
            sqLiteDatabase.execSQL(SQL_CREATE_TABLE_MOVIE);
        }catch (Exception e){
            Log.e("dbAdapter", e.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DatabaseContract.TABLE_MOVIE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ DatabaseContract.TABLE_TV_NAME);
        onCreate(sqLiteDatabase);
    }
}
