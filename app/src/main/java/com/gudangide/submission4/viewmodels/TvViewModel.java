package com.gudangide.submission4.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gudangide.submission4.fragments.TvFragment;
import com.gudangide.submission4.models.ResponseGenre;
import com.gudangide.submission4.models.ResponseTv;
import com.gudangide.submission4.models.pojo.Genre;
import com.gudangide.submission4.models.pojo.TvShow;
import com.gudangide.submission4.network.ApiService;
import com.gudangide.submission4.network.Constants;
import com.gudangide.submission4.network.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TvViewModel extends ViewModel {

    private List<String> list;
    private List<TvShow> tv;
    private List<Genre> genres;
    private List<TvShow> tvList;
    private List<TvShow> tvListPopular;
    private TvShow tvShow;
    private Integer statusCode;
    private static final String TAG = TvFragment.class.getSimpleName();
    private MutableLiveData<Integer> code = new MutableLiveData<>();
    private MutableLiveData<List<TvShow>> lisTv = new MutableLiveData<>();
    private MutableLiveData<List<TvShow>> lisPopular = new MutableLiveData<>();
    private ApiService service;

    public void generateTvList() {
        service = RetrofitUtils.getInstance().create(ApiService.class);
        Call<ResponseGenre> genreCall = service.getGenreTv(Constants.API_KEY, Constants.LANGUAGE);
        genreCall.enqueue(new Callback<ResponseGenre>() {
            @Override
            public void onResponse(Call<ResponseGenre> call, Response<ResponseGenre> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error Genre Code : " + response.code());
                    return;
                }
                genres = response.body().getGenres();
                generateTv();
            }

            @Override
            public void onFailure(Call<ResponseGenre> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
            }
        });
    }

    public void generateTv(){
        Call<ResponseTv> tvCall1 = service.getTvShow(Constants.API_KEY, Constants.LANGUAGE, 1);
        tvCall1.enqueue(new Callback<ResponseTv>() {
            @Override
            public void onResponse(Call<ResponseTv> call, Response<ResponseTv> response) {
                statusCode = response.code();
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error Code : " + response.code());
                    code.postValue(statusCode);
                    return;
                } else {
                    tv = response.body().getResults();
                    tvList = new ArrayList<>();
                    for (TvShow show : tv) {
                        list = new ArrayList<>();
                        for (Integer value : show.getGenreIds()) {
                            for (Genre dataGenre : genres) {

                                if (value.equals(dataGenre.getId())) {
                                    list.add(dataGenre.getName());
                                }
                            }
                        }
                        tvShow = new TvShow();
                        tvShow.setGenre(list);
                        tvShow.setBackdropPath(show.getBackdropPath());
                        tvShow.setId(show.getId());
                        tvShow.setBackdropPath(show.getBackdropPath());
                        tvShow.setName(show.getName());
                        tvShow.setVoteAverage(show.getVoteAverage());
                        tvShow.setPosterPath(show.getPosterPath());
                        tvList.add(tvShow);
                    }

                    lisTv.postValue(tvList);
                    code.postValue(statusCode);
                }
            }

            @Override
            public void onFailure(Call<ResponseTv> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
                statusCode = 12001;
                code.postValue(statusCode);
            }
        });
    }

    public void generatePopular() {
        Call<ResponseTv> callPopular = service.getTvShow(Constants.API_KEY, Constants.LANGUAGE, 2);
        callPopular.enqueue(new Callback<ResponseTv>() {
            @Override
            public void onResponse(Call<ResponseTv> call, Response<ResponseTv> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error Code : " + response.code());
                } else {
                    tv = response.body().getResults();
                    tvListPopular = new ArrayList<>();

                    for (TvShow listTv : tv) {
                        list = new ArrayList<>();
                        for (Integer value : listTv.getGenreIds()) {
                            for (Genre dataGenre : genres) {

                                if (value.equals(dataGenre.getId())) {
                                    list.add(dataGenre.getName());
                                }
                            }
                        }
                        tvShow = new TvShow();
                        tvShow.setId(listTv.getId());
                        tvShow.setGenre(list);
                        tvShow.setName(listTv.getName());
                        tvShow.setVoteAverage(listTv.getVoteAverage());
                        tvShow.setPosterPath(listTv.getPosterPath());
                        tvListPopular.add(tvShow);
                    }

                    lisPopular.postValue(tvListPopular);
                }
            }

            @Override
            public void onFailure(Call<ResponseTv> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
            }
        });
    }

    public LiveData<List<TvShow>> getTvList() {
        return lisTv;
    }

    public LiveData<Integer> getResponseCode() {
        return code;
    }

    public LiveData<List<TvShow>> getPopularList() {
        return lisPopular;
    }
}
