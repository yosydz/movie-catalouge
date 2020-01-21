package com.gudangide.submission4.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.gudangide.submission4.DetailActivity;
import com.gudangide.submission4.R;
import com.gudangide.submission4.adapters.PopularTvAdapter;
import com.gudangide.submission4.adapters.TvAdapter;
import com.gudangide.submission4.models.pojo.TvShow;
import com.gudangide.submission4.networks.Constants;
import com.gudangide.submission4.viewmodels.TvViewModel;
import com.synnapps.carouselview.CarouselView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TvFragment extends Fragment {

    private RecyclerView rViewTv;
    private RecyclerView rViewTvPopular;
    private CarouselView carouselView;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private TvViewModel viewModel;

    private ConstraintLayout errorLayout;
    private TextView tvError;
    private ImageButton refresh;

    public TvFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_tv, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rViewTv = view.findViewById(R.id.recycle_tv);
        rViewTv.setHasFixedSize(true);

        rViewTvPopular = view.findViewById(R.id.recycle_tv_popular);
        rViewTvPopular.setHasFixedSize(true);

        carouselView = view.findViewById(R.id.carouselView);

        scrollView = view.findViewById(R.id.scrollView);

        progressBar = view.findViewById(R.id.spin_kit);
        Sprite rotatingCircle = new WanderingCubes();
        progressBar.setIndeterminateDrawable(rotatingCircle);

        errorLayout = view.findViewById(R.id.error_layout);
        tvError = view.findViewById(R.id.tv_message);
        refresh = view.findViewById(R.id.refresh);

        viewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(TvViewModel.class);

        showLoading(true);
        generateTv();
        generatePopular();
    }

    private void generatePopular() {
        if (viewModel.getPopularList().getValue() == null) {
            viewModel.generatePopular();
        }
        viewModel.getPopularList().observe(getActivity(), tvShows -> {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rViewTvPopular.setLayoutManager(layoutManager);
            PopularTvAdapter popularTvAdapter = new PopularTvAdapter(getContext(), tvShows);
            popularTvAdapter.setOnItemClickListener((position, v) -> {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("idTv", tvShows.get(position).getId());
                startActivity(intent);
            });
            rViewTvPopular.setAdapter(popularTvAdapter);
            showLoading(false);
        });
    }

    private void generateTv() {
        if (viewModel.getTvList().getValue() == null) {
            viewModel.generateTvList();
            viewModel.getResponseCode().observe(getActivity(), integer -> {
                String message;
                if (integer != 200) {
                    switch (integer) {
                        case 404:
                            message = getResources().getString(R.string.error_not_found);
                            break;
                        case 500:
                            message = getResources().getString(R.string.error_server_broken);
                            break;
                        case 12001:
                            message = getResources().getString(R.string.error_network);
                            break;
                        default:
                            message = getResources().getString(R.string.error_unknow, integer);
                            break;
                    }
                    showError(message);
                    refresh.setOnClickListener(view -> {
                        errorLayout.setVisibility(View.GONE);
                        showLoading(true);
                        viewModel.generateTvList();
                        generatePopular();
                    });
                }
            });
        }
        viewModel.getTvList().observe(getActivity(), tvShows -> {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rViewTv.setLayoutManager(layoutManager);
            TvAdapter tvAdapter = new TvAdapter(tvShows, getContext());
            tvAdapter.setOnItemClickListener((position, v) -> {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("idTv", tvShows.get(position).getId());
                startActivity(intent);
            });

            rViewTv.setAdapter(tvAdapter);
            showLoading(false);
            carouselView.setImageListener((position, imageView) -> {
                TvShow tvShow = tvShows.get(position);
                Glide.with(getContext())
                        .load(Constants.BACKDROP_URL + tvShow.getBackdropPath())
                        .into(imageView);
            });

            carouselView.setPageCount(7);
        });
    }

    private void showLoading(Boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            showLoading(false);
        }
        tvError.setText(message);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);

        final MenuItem item = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getResources().getString(R.string.search_hint_tv));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
