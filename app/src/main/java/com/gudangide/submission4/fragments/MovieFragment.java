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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.gudangide.submission4.DetailActivity;
import com.gudangide.submission4.R;
import com.gudangide.submission4.adapters.MovieAdapter;
import com.gudangide.submission4.adapters.PopularMovieAdapter;
import com.gudangide.submission4.models.pojo.Movie;
import com.gudangide.submission4.network.Constants;
import com.gudangide.submission4.viewmodels.MovieViewModel;
import com.synnapps.carouselview.CarouselView;

import static com.gudangide.submission4.fragments.SearchFragment.QUERY_SEARCH;
import static com.gudangide.submission4.fragments.SearchFragment.SEARCH_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    private RecyclerView   rViewMovie;
    private RecyclerView   rViewMoviePopular;
    private ProgressBar    progressBar;
    private CarouselView   carouselView;
    private ScrollView     scrollView;
    private MovieViewModel viewModel;

    private ConstraintLayout errorLayout;
    private TextView         tvError;
    private ImageButton      refresh;


    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rViewMovie = view.findViewById(R.id.recycle_movie);

        rViewMoviePopular = view.findViewById(R.id.recycle_movie_popular);
        carouselView = view.findViewById(R.id.carouselView);

        scrollView = view.findViewById(R.id.scrollView);

        progressBar = view.findViewById(R.id.spin_kit);
        Sprite rotatingCircle = new WanderingCubes();
        progressBar.setIndeterminateDrawable(rotatingCircle);

        errorLayout = view.findViewById(R.id.error_layout);
        tvError = view.findViewById(R.id.tv_message);
        refresh = view.findViewById(R.id.refresh);

        viewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(MovieViewModel.class);

        showLoading(true);
        generateListMovie();
    }

    private void generateListMoviePopular() {
        if (viewModel.getPopularList().getValue() == null) {
            viewModel.generatePopular();
        }
        viewModel.getPopularList().observe(getActivity(), movies -> {
            rViewMoviePopular.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rViewMoviePopular.setLayoutManager(layoutManager);
            PopularMovieAdapter popularMovieAdapter = new PopularMovieAdapter(getContext(), movies);
            popularMovieAdapter.setOnItemClickListener((position, v) -> {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("idMovie", movies.get(position).getId());
                startActivity(intent);
            });
            rViewMoviePopular.setAdapter(popularMovieAdapter);
            showLoading(false);
        });
    }

    private void generateListMovie() {
        if (viewModel.getMovieList().getValue() == null) {
            viewModel.generateData();
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
                        viewModel.generateData();
                        generateListMoviePopular();
                    });
                }
            });
        }
        viewModel.getMovieList().observe(getActivity(), movies -> {
            rViewMovie.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            rViewMovie.setLayoutManager(layoutManager);
            MovieAdapter movieAdapter = new MovieAdapter(movies, getContext());
            movieAdapter.setOnItemClickListener((position, v) -> {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("idMovie", movies.get(position).getId());
                startActivity(intent);
            });
            rViewMovie.setAdapter(movieAdapter);
            carouselView.setImageListener((position, imageView) -> {
                Movie movie = movies.get(position);
                Glide.with(getContext())
                        .load(Constants.BACKDROP_URL + movie.getBackdropPath())
                        .into(imageView);
            });

            carouselView.setPageCount(7);
            generateListMoviePopular();
            showLoading(false);
        });
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
        searchView.setQueryHint(getResources().getString(R.string.search_hint_movie));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle bundle = new Bundle();
                bundle.putString(SEARCH_TYPE, "movie");
                bundle.putString(QUERY_SEARCH, query);

                SearchFragment fragment = new SearchFragment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fl_main, fragment, "tag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
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
}
