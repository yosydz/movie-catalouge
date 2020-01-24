package com.gudangide.consumerfavorite.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {

    public static final String AUTHORITY = "com.gudangide.submission4";
    private static final String SCHEME = "content";

    public static String TABLE_TV_NAME = "favorite_tv";
    public static String TABLE_MOVIE_NAME = "favorite_movie";

    public static final class FavoriteColumns implements BaseColumns{
        public static String ID = "id";
        public static String POSTER = "poster";
        public static String TITLE = "title";
        public static String RATING = "rating";
        public static String GENRE = "genre";

        public static final Uri CONTENT_URI = new Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_MOVIE_NAME)
                .build();
    }
}
