package com.gudangide.submission4.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.gudangide.submission4.DetailActivity;
import com.gudangide.submission4.R;
import com.gudangide.submission4.adapters.SearchAdapterMovie;
import com.gudangide.submission4.adapters.SearchAdapterTv;
import com.gudangide.submission4.models.ResponseGenre;
import com.gudangide.submission4.models.ResponseMovie;
import com.gudangide.submission4.models.ResponseTv;
import com.gudangide.submission4.models.pojo.Genre;
import com.gudangide.submission4.models.pojo.Movie;
import com.gudangide.submission4.models.pojo.TvShow;
import com.gudangide.submission4.network.ApiService;
import com.gudangide.submission4.network.Constants;
import com.gudangide.submission4.network.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private RecyclerView rvSearch;
    private ProgressBar progressBar;
    private String query;
    private RecyclerView scrollView;
    private ConstraintLayout errorLayout;
    public static final String SEARCH_TYPE = "search_type";
    public static String QUERY_SEARCH;
    private TextView tvError;
    private ImageButton refresh;

    private List<String> list;
    private List<Movie> movies;
    private List<Genre> genres;
    private List<Movie> movieList;
    private Movie movie1;
    private Integer statusCode;
    private ApiService service;

    private List<TvShow> tv;
    private List<TvShow> tvList;
    private TvShow tv1;

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

        Bundle bundle = this.getArguments();
        String type = bundle.getString(SEARCH_TYPE);
        if (type.equals("movie")) {
            query = bundle.getString(QUERY_SEARCH);
            generateSearchMovie(query);
        } else if (type.equals("tv")) {
            query = bundle.getString(QUERY_SEARCH);
            generateSearchTv(query);
        }
    }

    private void generateSearchTv(String query) {
        service = RetrofitUtils.getInstance().create(ApiService.class);
        Call<ResponseGenre> callGenre = service.getGenreTv(Constants.API_KEY, Constants.LANGUAGE);
        callGenre.enqueue(new Callback<ResponseGenre>() {
            @Override
            public void onResponse(Call<ResponseGenre> call, Response<ResponseGenre> response) {
                if (response.isSuccessful()) {
                    genres = response.body().getGenres();
                    generateTvSearch(query);
                } else {
                    Log.e(TAG, "Error Genre Code : " + response.code());
                    String message;
                    switch (statusCode) {
                        case 404:
                            message = getResources().getString(R.string.error_not_found);
                            break;
                        case 500:
                            message = getResources().getString(R.string.error_server_broken);
                            break;
                        default:
                            message = getResources().getString(R.string.error_unknow, statusCode);
                            break;
                    }
                    showError(message);
                    refresh.setOnClickListener(view -> {
                        errorLayout.setVisibility(View.GONE);
                        showLoading(true);
                        generateSearchMovie(query);
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseGenre> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
                showError(getResources().getString(R.string.error_network));
                refresh.setOnClickListener(view -> {
                    errorLayout.setVisibility(View.GONE);
                    showLoading(true);
                    generateSearchMovie(query);
                });
            }
        });
    }

    private void generateTvSearch(String query) {
        Call<ResponseTv> callSearchTv = service.getSearchTv(Constants.API_KEY, Constants.LANGUAGE, query);
        callSearchTv.enqueue(new Callback<ResponseTv>() {
            @Override
            public void onResponse(Call<ResponseTv> call, Response<ResponseTv> response) {
                statusCode = response.code();
                if (response.isSuccessful()) {
                    tv = response.body().getResults();
                    tvList = new ArrayList<>();

                    for (TvShow tvShow : tv) {
                        list = new ArrayList<>();
                        for (Integer value : tvShow.getGenreIds()) {
                            for (Genre dataGenre : genres) {
                                if (value.equals(dataGenre.getId())) {
                                    list.add(dataGenre.getName());
                                }
                            }
                        }

                        tv1 = new TvShow();
                        tv1.setId(tvShow.getId());
                        tv1.setName(tvShow.getName());
                        tv1.setGenre(list);
                        tv1.setVoteAverage(tvShow.getVoteAverage());
                        tv1.setPosterPath(tvShow.getPosterPath());
                        tvList.add(tv1);
                    }

                    if (tvList.size() <=0){
                        showError(getResources().getString(R.string.tv_not_found, query));
                        refresh.setOnClickListener(view -> {
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fl_main, new TvFragment(), "tag");
                            transaction.commit();
                        });
                    }else {
                        rvSearch.setHasFixedSize(true);
                        rvSearch.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
                        SearchAdapterTv searchAdapterTv = new SearchAdapterTv(tvList, getContext());
                        searchAdapterTv.setOnItemClickListener((position, v) -> {
                            Intent intent = new Intent(getActivity(), DetailActivity.class);
                            intent.putExtra("idTv", tvList.get(position).getId());
                            startActivity(intent);
                        });
                        rvSearch.setAdapter(searchAdapterTv);
                        showLoading(false);
                    }

                } else {
                    Log.e(TAG, "Error Code Movie: " + response.code());
                    String message;
                    switch (statusCode) {
                        case 404:
                            message = getResources().getString(R.string.error_not_found);
                            break;
                        case 500:
                            message = getResources().getString(R.string.error_server_broken);
                            break;
                        default:
                            message = getResources().getString(R.string.error_unknow, statusCode);
                            break;
                    }
                    showError(message);
                    refresh.setOnClickListener(view -> {
                        errorLayout.setVisibility(View.GONE);
                        showLoading(true);
                        generateSearchMovie(query);
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseTv> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
                statusCode = 12001;
                showError(getResources().getString(R.string.error_network));
                refresh.setOnClickListener(view -> {
                    errorLayout.setVisibility(View.GONE);
                    showLoading(true);
                    generateSearchMovie(query);
                });
            }
        });
    }

    private void generateSearchMovie(String query) {
        service = RetrofitUtils.getInstance().create(ApiService.class);
        Call<ResponseGenre> callGenre = service.getGenreMovie(Constants.API_KEY, Constants.LANGUAGE);
        callGenre.enqueue(new Callback<ResponseGenre>() {
            @Override
            public void onResponse(Call<ResponseGenre> call, Response<ResponseGenre> response) {
                if (response.isSuccessful()) {
                    genres = response.body().getGenres();
                    generateMovieSearch(query);
                } else {
                    Log.e(TAG, "Error Genre Code : " + response.code());
                    String message;
                    switch (statusCode) {
                        case 404:
                            message = getResources().getString(R.string.error_not_found);
                            break;
                        case 500:
                            message = getResources().getString(R.string.error_server_broken);
                            break;
                        default:
                            message = getResources().getString(R.string.error_unknow, statusCode);
                            break;
                    }
                    showError(message);
                    refresh.setOnClickListener(view -> {
                        errorLayout.setVisibility(View.GONE);
                        showLoading(true);
                        generateSearchMovie(query);
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseGenre> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
                showError(getResources().getString(R.string.error_network));
                refresh.setOnClickListener(view -> {
                    errorLayout.setVisibility(View.GONE);
                    showLoading(true);
                    generateSearchMovie(query);
                });
            }
        });
    }

    private void generateMovieSearch(String query) {
        Call<ResponseMovie> callSearchMovie = service.getSearchMovie(Constants.API_KEY, Constants.LANGUAGE, query);
        callSearchMovie.enqueue(new Callback<ResponseMovie>() {
            @Override
            public void onResponse(Call<ResponseMovie> call, Response<ResponseMovie> response) {
                statusCode = response.code();
                if (response.isSuccessful()) {
                    movies = response.body().getResults();
                    movieList = new ArrayList<>();

                    for (Movie movie : movies) {
                        list = new ArrayList<>();
                        for (Integer value : movie.getGenreIds()) {
                            for (Genre dataGenre : genres) {
                                if (value.equals(dataGenre.getId())) {
                                    list.add(dataGenre.getName());
                                }
                            }
                        }

                        movie1 = new Movie();
                        movie1.setId(movie.getId());
                        movie1.setTitle(movie.getTitle());
                        movie1.setGenre(list);
                        movie1.setVoteAverage(movie.getVoteAverage());
                        movie1.setPosterPath(movie.getPosterPath());
                        movieList.add(movie1);
                    }

                    if (movieList.size() <=0){
                        showError(getResources().getString(R.string.movie_not_found, query));
                        refresh.setOnClickListener(view -> {
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fl_main, new MovieFragment(), "tag");
                            transaction.commit();
                        });
                    }else {
                        rvSearch.setHasFixedSize(true);
                        rvSearch.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
                        SearchAdapterMovie searchAdapterMovie = new SearchAdapterMovie(movieList, getContext());
                        searchAdapterMovie.setOnItemClickListener((position, v) -> {
                            Intent intent = new Intent(getActivity(), DetailActivity.class);
                            intent.putExtra("idMovie", movieList.get(position).getId());
                            startActivity(intent);
                        });
                        rvSearch.setAdapter(searchAdapterMovie);
                        showLoading(false);
                    }

                } else {
                    Log.e(TAG, "Error Code Movie: " + response.code());
                    String message;
                    switch (statusCode) {
                        case 404:
                            message = getResources().getString(R.string.error_not_found);
                            break;
                        case 500:
                            message = getResources().getString(R.string.error_server_broken);
                            break;
                        default:
                            message = getResources().getString(R.string.error_unknow, statusCode);
                            break;
                    }
                    showError(message);
                    refresh.setOnClickListener(view -> {
                        errorLayout.setVisibility(View.GONE);
                        showLoading(true);
                        generateSearchMovie(query);
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseMovie> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
                statusCode = 12001;
                showError(getResources().getString(R.string.error_network));
                refresh.setOnClickListener(view -> {
                    errorLayout.setVisibility(View.GONE);
                    showLoading(true);
                    generateSearchMovie(query);
                });
            }
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
