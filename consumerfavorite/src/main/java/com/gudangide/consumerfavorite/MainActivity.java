package com.gudangide.consumerfavorite;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.gudangide.consumerfavorite.adapter.FavoriteMovieAdapter;
import com.gudangide.consumerfavorite.db.DatabaseContract;
import com.gudangide.consumerfavorite.helper.MappingHelper;
import com.gudangide.consumerfavorite.model.Favorite;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoadFavoritCallback{

    private RecyclerView rvFavMovie;
    private ProgressBar progressBar;
    private static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    private FavoriteMovieAdapter favoriteAdapter;
    private ArrayList<Favorite> favoriteArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Consumer Favorite");
        }

        progressBar = findViewById(R.id.spin_kit);
        Sprite rotatingCircle = new WanderingCubes();
        progressBar.setIndeterminateDrawable(rotatingCircle);

        rvFavMovie = findViewById(R.id.rv_movie);
        rvFavMovie.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        rvFavMovie.setHasFixedSize(true);

        if (savedInstanceState != null) {
            ArrayList<Favorite> listMovie = savedInstanceState.getParcelableArrayList(EXTRA_MOVIE);
            favoriteArrayList = listMovie;
            if (listMovie != null) {
                progressBar.setVisibility(View.GONE);
                favoriteAdapter = new FavoriteMovieAdapter(listMovie, this);
                favoriteAdapter.setOnItemClickListener((position, v) -> Toast.makeText(this, favoriteArrayList.get(position).getTitle(), Toast.LENGTH_SHORT).show());
                rvFavMovie.setAdapter(favoriteAdapter);
            }
        }else {
            new LoadFavoriteMovie(this, this).execute();
        }

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver myObserver = new DataObserver(handler, this);
        getContentResolver().registerContentObserver(DatabaseContract.FavoriteColumns.CONTENT_URI, true, myObserver);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_MOVIE, favoriteArrayList);
    }

    @Override
    public void preExecute() {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
    }

    @Override
    public void postExecute(ArrayList<Favorite> favorites) {
        progressBar.setVisibility(View.GONE);
        if (favorites.size() > 0){
            favoriteArrayList = favorites;
            favoriteAdapter = new FavoriteMovieAdapter(favoriteArrayList, MainActivity.this);
            favoriteAdapter.setOnItemClickListener((position, v) -> Toast.makeText(this, favoriteArrayList.get(position).getTitle(), Toast.LENGTH_SHORT).show());
            rvFavMovie.setAdapter(favoriteAdapter);
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
        }
    }

    private static class LoadFavoriteMovie extends AsyncTask<Void, Void, ArrayList<Favorite>> {

        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadFavoritCallback> weakCallback;

        private LoadFavoriteMovie(Context context, LoadFavoritCallback callback) {
            weakContext = new WeakReference<>(context);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Favorite> doInBackground(Void... voids) {
            Context context = weakContext.get();
            Cursor dataCursor = context.getContentResolver().query(DatabaseContract.FavoriteColumns.CONTENT_URI, null, null, null, null);
            return MappingHelper.mapCursorToArrayList(dataCursor);
        }

        @Override
        protected void onPostExecute(ArrayList<Favorite> favorites) {
            super.onPostExecute(favorites);
            weakCallback.get().postExecute(favorites);
        }

    }

    public static class DataObserver extends ContentObserver {
        final Context context;
        public DataObserver(Handler handler, Context context) {
            super(handler);
            this.context = context;
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            new LoadFavoriteMovie(context, (LoadFavoritCallback) context).execute();
        }
    }
}

interface LoadFavoritCallback {
    void preExecute();
    void postExecute(ArrayList<Favorite> favorites);
}
