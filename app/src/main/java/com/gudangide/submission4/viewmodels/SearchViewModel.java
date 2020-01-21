package com.gudangide.submission4.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gudangide.submission4.models.ResponseGenre;
import com.gudangide.submission4.models.ResponseMovie;
import com.gudangide.submission4.models.pojo.Genre;
import com.gudangide.submission4.models.pojo.Movie;
import com.gudangide.submission4.networks.ApiService;
import com.gudangide.submission4.networks.Constants;
import com.gudangide.submission4.networks.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {
    private static final String TAG = SearchViewModel.class.getSimpleName();
    private List<String> list;
    private List<Movie> movies;
    private List<Genre> genres;
    private List<Movie> movieList;
    private Movie movie1;
    private Integer statusCode;
    private MutableLiveData<List<Movie>> listMovie = new MutableLiveData<>();
    private MutableLiveData<Integer> code = new MutableLiveData<>();
    private MutableLiveData<List<Movie>> listPopular = new MutableLiveData<>();
    private ApiService service;

    public void generateData(String query){
        service = RetrofitUtils.getInstance().create(ApiService.class);
        Call<ResponseGenre> callGenre = service.getGenreMovie(Constants.API_KEY, Constants.LANGUAGE);
        callGenre.enqueue(new Callback<ResponseGenre>() {
            @Override
            public void onResponse(Call<ResponseGenre> call, Response<ResponseGenre> response) {
                if (response.isSuccessful()){
                    genres = response.body().getGenres();
                    generateSearchMovie(query);
                }else {
                    Log.e(TAG, "Error Genre Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseGenre> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
            }
        });
    }

    private void generateSearchMovie(String query) {
        Call<ResponseMovie> callSearchMovie = service.getSearchMovie(Constants.API_KEY, Constants.LANGUAGE, query);
        callSearchMovie.enqueue(new Callback<ResponseMovie>() {
            @Override
            public void onResponse(Call<ResponseMovie> call, Response<ResponseMovie> response) {
                statusCode = response.code();
                if (response.isSuccessful()){
                    movies = response.body().getResults();
                    movieList = new ArrayList<>();

                    for (Movie movie : movies){
                        list = new ArrayList<>();
                        for (Integer value : movie.getGenreIds()){
                            for (Genre dataGenre : genres){
                                if (value.equals(dataGenre.getId())){
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

                    listMovie.postValue(movieList);
                    code.postValue(statusCode);
                }else {
                    Log.e(TAG, "Error Code Movie: " + response.code());
                    code.postValue(statusCode);
                }
            }

            @Override
            public void onFailure(Call<ResponseMovie> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
                statusCode = 12001;
                code.postValue(statusCode);
            }
        });
    }

    public LiveData<List<Movie>> getSearchMovieList() {
        return listMovie;
    }

    public LiveData<Integer> getResponseCode() {
        return code;
    }
}
