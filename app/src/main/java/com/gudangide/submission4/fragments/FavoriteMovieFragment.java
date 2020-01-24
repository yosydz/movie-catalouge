package com.gudangide.submission4.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.gudangide.submission4.DetailActivity;
import com.gudangide.submission4.R;
import com.gudangide.submission4.adapters.FavoriteMovieAdapter;
import com.gudangide.submission4.db.DatabaseContract;
import com.gudangide.submission4.db.FavoriteHelper;
import com.gudangide.submission4.helper.MappingHelper;
import com.gudangide.submission4.models.Favorite;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.gudangide.submission4.DetailActivity.REQUEST_OKE;
import static com.gudangide.submission4.db.DatabaseContract.TABLE_MOVIE_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteMovieFragment extends Fragment implements LoadFavoritCallback {

    private RecyclerView rvFavMovie;
    private FavoriteHelper favoriteHelper;
    private ProgressBar progressBar;
    private static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    private FavoriteMovieAdapter favoriteAdapter;
    private ArrayList<Favorite> favoriteArrayList;


    public FavoriteMovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.spin_kit);
        Sprite rotatingCircle = new WanderingCubes();
        progressBar.setIndeterminateDrawable(rotatingCircle);

        rvFavMovie = view.findViewById(R.id.rv_movie);
        rvFavMovie.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        rvFavMovie.setHasFixedSize(true);

        favoriteHelper = FavoriteHelper.getInstance(getContext());
        favoriteHelper = new FavoriteHelper(getContext(), TABLE_MOVIE_NAME);
        favoriteHelper.open();

        if (savedInstanceState != null) {
            ArrayList<Favorite> listMovie = savedInstanceState.getParcelableArrayList(EXTRA_MOVIE);
            favoriteArrayList = listMovie;
            if (listMovie != null) {
                progressBar.setVisibility(View.GONE);
                favoriteAdapter = new FavoriteMovieAdapter(listMovie, getContext());
                favoriteAdapter.setOnItemClickListener((position, v) -> {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("idMovie", listMovie.get(position).getId());
                    startActivityForResult(intent, REQUEST_OKE);
                });
                rvFavMovie.setAdapter(favoriteAdapter);
            }
        }else {
            new LoadFavoriteMovie(getContext(), this).execute();
        }

        HandlerThread handlerThread = new HandlerThread("DataObserver");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        DataObserver myObserver = new DataObserver(handler, getContext());
        getActivity().getContentResolver().registerContentObserver(DatabaseContract.FavoriteColumns.CONTENT_URI, true, myObserver);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_MOVIE, favoriteArrayList);
    }

    @Override
    public void preExecute() {
        getActivity().runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
    }

    @Override
    public void postExecute(ArrayList<Favorite> favorites) {
        progressBar.setVisibility(View.GONE);
        if (favorites.size() > 0){
            favoriteArrayList = favorites;
            favoriteAdapter = new FavoriteMovieAdapter(favoriteArrayList, getContext());
            favoriteAdapter.setOnItemClickListener((position, v) -> {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("idMovie", favoriteArrayList.get(position).getId());
                startActivityForResult(intent, REQUEST_OKE);
            });
            rvFavMovie.setAdapter(favoriteAdapter);
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Tidak Ada Data", Toast.LENGTH_SHORT).show();
        }
    }

    private static class LoadFavoriteMovie extends AsyncTask<Void, Void, ArrayList<Favorite>>{

        private final WeakReference<Context> weakContext;
        private final WeakReference<LoadFavoritCallback> weakCallback;

        public LoadFavoriteMovie(Context context, LoadFavoritCallback callback) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        favoriteHelper.close();
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
