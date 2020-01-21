package com.gudangide.submission4.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.ID;

public class FavoriteHelper {
    private String DATABASE_TABLE;
    private static DatabaseHelper databaseHelper;
    private static FavoriteHelper INSTANCE;

    private static SQLiteDatabase database;

    public FavoriteHelper(Context context, String dbTable) {
        databaseHelper = new DatabaseHelper(context);
        DATABASE_TABLE = dbTable;
    }

    public FavoriteHelper(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public static FavoriteHelper getInstance(Context context){
        if (INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){
                if (INSTANCE == null){
                    INSTANCE = new FavoriteHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close(){
        databaseHelper.close();

        if (database.isOpen()){
            database.close();
        }
    }

    public Cursor queryAll() {
        return database.query(DATABASE_TABLE,
                null,
                null,
                null,
                null,
                null,
                _ID + " DESC");
    }

    public Cursor queryById(String id) {
        return database.query(DATABASE_TABLE, null,
                ID + " = ?",
                new String[]{id},
                null,
                null,
                null,
                null);
    }

    public long insert(ContentValues values){
        return database.insert(DATABASE_TABLE, null, values);
    }

    public int update(String id, ContentValues values){
        return database.update(DATABASE_TABLE, values, _ID + " = ?", new String[]{id});
    }

    public int deleteById(String id){
        return database.delete(DATABASE_TABLE, ID + " = ?", new String[]{id});
    }
}
