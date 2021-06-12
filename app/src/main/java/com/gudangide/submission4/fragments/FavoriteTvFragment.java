package com.gudangide.submission4.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.gudangide.submission4.DetailActivity;
import com.gudangide.submission4.R;
import com.gudangide.submission4.adapters.FavoriteTvAdapter;
import com.gudangide.submission4.db.FavoriteHelper;
import com.gudangide.submission4.helpers.MappingHelper;
import com.gudangide.submission4.models.Favorite;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.gudangide.submission4.DetailActivity.REQUEST_OKE;
import static com.gudangide.submission4.db.DatabaseContract.TABLE_TV_NAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteTvFragment extends Fragment implements LoadFavoritCallback {

    private RecyclerView rvFavTv;
    private FavoriteHelper favoriteHelper;
    private ProgressBar progressBar;
    private static final String EXTRA_TV = "EXTRA_TV";
    private FavoriteTvAdapter favoriteAdapter;
    private ArrayList<Favorite> favoriteArrayListTv;


    public FavoriteTvFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_tv, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.spin_kit);
        Sprite rotatingCircle = new WanderingCubes();
        progressBar.setIndeterminateDrawable(rotatingCircle);
        progressBar.setVisibility(View.GONE);

        rvFavTv = view.findViewById(R.id.rv_tv);
        rvFavTv.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        rvFavTv.setHasFixedSize(true);

        favoriteHelper = FavoriteHelper.getInstance(getContext());
        favoriteHelper = new FavoriteHelper(getContext(), TABLE_TV_NAME);
        favoriteHelper.open();

        if (savedInstanceState == null) {
            new LoadFavoriteTv(favoriteHelper, this).execute();
        } else {
            ArrayList<Favorite> list = savedInstanceState.getParcelableArrayList(EXTRA_TV);
            favoriteArrayListTv = list;
            if (list != null) {
                progressBar.setVisibility(View.GONE);
                favoriteAdapter = new FavoriteTvAdapter(list, getContext());
                favoriteAdapter.setOnItemClickListener((position, v) -> {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("idTv", list.get(position).getId());
                    startActivityForResult(intent, REQUEST_OKE);
                });
                rvFavTv.setAdapter(favoriteAdapter);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_TV, favoriteArrayListTv);
    }

    @Override
    public void preExecute() {
        getActivity().runOnUiThread(() -> progressBar.setVisibility(View.GONE));
    }

    @Override
    public void postExecute(ArrayList<Favorite> favorites) {
        progressBar.setVisibility(View.GONE);
        if (favorites.size() > 0){
            favoriteArrayListTv = favorites;
            favoriteAdapter = new FavoriteTvAdapter(favorites, getContext());
            favoriteAdapter.setOnItemClickListener((position, v) -> {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("idTv", favorites.get(position).getId());
                startActivityForResult(intent, REQUEST_OKE);
            });
            rvFavTv.setAdapter(favoriteAdapter);
        }else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Tidak Ada Data", Toast.LENGTH_SHORT).show();
        }
    }

    private static class LoadFavoriteTv extends AsyncTask<Void, Void, ArrayList<Favorite>> {

        private final WeakReference<FavoriteHelper> weakFavorite;
        private final WeakReference<LoadFavoritCallback> weakCallback;

        public LoadFavoriteTv(FavoriteHelper favoriteHelper, LoadFavoritCallback callback) {
            weakFavorite = new WeakReference<>(favoriteHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Favorite> doInBackground(Void... voids) {
            Cursor rawData = weakFavorite.get().queryAll();
            return MappingHelper.mapCursorToArrayList(rawData);
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
}
