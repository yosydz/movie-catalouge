package com.gudangide.submission4.helper;

import android.database.Cursor;

import com.gudangide.submission4.db.DatabaseContract;
import com.gudangide.submission4.models.Favorite;

import java.util.ArrayList;

import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.ID;
import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.POSTER;
import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.RATING;
import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.GENRE;
import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.TITLE;

public class MappingHelper {

    public static ArrayList<Favorite> mapCursorToArrayList(Cursor cursor){
        ArrayList<Favorite> favoritList = new ArrayList<>();

        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE));
            String poster = cursor.getString(cursor.getColumnIndexOrThrow(POSTER));
            String rating = cursor.getString(cursor.getColumnIndexOrThrow(RATING));
            String genre = cursor.getString(cursor.getColumnIndexOrThrow(GENRE));
            favoritList.add(new Favorite(id, title, poster, rating, genre));
        }
        return favoritList;
    }
}
