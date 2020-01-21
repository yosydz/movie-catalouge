package com.gudangide.submission4.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.gudangide.submission4.DetailActivity;
import com.gudangide.submission4.R;
import com.gudangide.submission4.adapters.SearchAdapterMovie;
import com.gudangide.submission4.viewmodels.SearchViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private RecyclerView rvSearch;
    private ProgressBar progressBar;
    private String query;
    private RecyclerView scrollView;
    private ConstraintLayout errorLayout;
    public static final String SEARCH_TYPE = "search_type";
    public static String QUERY_SEARCH;
    private TextView tvError;
    private ImageButton refresh;
    private SearchViewModel searchViewModel;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSearch = view.findViewById(R.id.rv_seach);
        progressBar = view.findViewById(R.id.spin_kit_search);
        Sprite rotatingCircle = new WanderingCubes();
        progressBar.setIndeterminateDrawable(rotatingCircle);

        errorLayout = view.findViewById(R.id.error_layout);
        tvError = view.findViewById(R.id.tv_message);
        refresh = view.findViewById(R.id.refresh);
        showLoading(true);

        searchViewModel = new ViewModelProvider(getActivity(), new ViewModelProvider.NewInstanceFactory()).get(SearchViewModel.class);
        Bundle bundle = this.getArguments();
        String type = bundle.getString(SEARCH_TYPE);
        if (type.equals("movie")){
            query = bundle.getString(QUERY_SEARCH);
            generateSearchMovie(query);
        }else if (type.equals("tv")){
            query = bundle.getString(QUERY_SEARCH);
            generateSearchTv(query);
        }
    }

    private void generateSearchTv(String query) {

    }

    private void generateSearchMovie(String query) {
        searchViewModel.generateData(query);
        searchViewModel.getResponseCode().observe(getActivity(), integer -> {
            String message;
            if (integer != 200){
                switch (integer){
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
                    generateSearchMovie(query);
                });
            }

        });

        searchViewModel.getSearchMovieList().observe(getActivity(), movies -> {
            rvSearch.setHasFixedSize(true);
            rvSearch.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            SearchAdapterMovie searchAdapterMovie = new SearchAdapterMovie(movies, getContext());
            searchAdapterMovie.setOnItemClickListener((position, v) -> {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("idMovie", movies.get(position).getId());
                startActivity(intent);
            });
            rvSearch.setAdapter(searchAdapterMovie);
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

    private void showLoading(Boolean state) {
        if (state) {
            progressBar.setVisibility(View.VISIBLE);
            rvSearch.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvSearch.setVisibility(View.VISIBLE);
        }
    }
}
