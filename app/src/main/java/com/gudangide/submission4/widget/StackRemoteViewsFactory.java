package com.gudangide.submission4.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.gudangide.submission4.R;
import com.gudangide.submission4.db.FavoriteHelper;
import com.gudangide.submission4.helpers.MappingHelper;
import com.gudangide.submission4.models.Favorite;
import com.gudangide.submission4.network.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.gudangide.submission4.db.DatabaseContract.TABLE_MOVIE_NAME;

public class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final List<Bitmap> mWidgetItems = new ArrayList<>();
    private final Context mContext;
    private FavoriteHelper favoriteHelper;
    private static List<Favorite> imageFavorite = new ArrayList<>();

    StackRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        //required
        favoriteHelper = FavoriteHelper.getInstance(mContext);
        favoriteHelper = new FavoriteHelper(mContext, TABLE_MOVIE_NAME);
        favoriteHelper.open();
        Binder.restoreCallingIdentity(Binder.clearCallingIdentity());
    }

    @Override
    public void onDataSetChanged() {
        //Ini berfungsi untuk melakukan refresh saat terjadi perubahan.
        Cursor rawData = favoriteHelper.queryAll();
        if (rawData != null){
            imageFavorite.clear();
            imageFavorite.addAll(MappingHelper.mapCursorToArrayList(rawData));
        }
    }

    @Override
    public void onDestroy() {
        //required
    }

    @Override
    public int getCount() {
        return imageFavorite.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        try {
            Bitmap posterBitmap = Glide.with(mContext)
                    .asBitmap()
                    .load(Constants.POSTER_URL + imageFavorite.get(position).getPoster())
                    .submit()
                    .get();

            rv.setImageViewBitmap(R.id.imageView, posterBitmap);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        Bundle extras = new Bundle();
        extras.putInt(FavoriteMovieWidget.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
