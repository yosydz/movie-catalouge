package com.gudangide.submission4.db;

import android.provider.BaseColumns;

public class DatabaseContract {

    public static String TABLE_TV_NAME = "favorite_tv";
    public static String TABLE_MOVIE_NAME = "favorite_movie";

    public static final class FavoriteColumns implements BaseColumns{
        public static String ID = "id";
        public static String POSTER = "poster";
        public static String TITLE = "title";
        public static String RATING = "rating";
        public static String GENRE = "genre";
    }
}
