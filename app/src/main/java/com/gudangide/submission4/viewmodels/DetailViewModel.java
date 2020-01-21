package com.gudangide.submission4.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gudangide.submission4.models.ResponseGenre;
import com.gudangide.submission4.models.ResponseRecomendation;
import com.gudangide.submission4.models.pojo.Genre;
import com.gudangide.submission4.models.pojo.MovieDetail;
import com.gudangide.submission4.models.pojo.Recomendation;
import com.gudangide.submission4.models.pojo.TvDetail;
import com.gudangide.submission4.networks.ApiService;
import com.gudangide.submission4.networks.Constants;
import com.gudangide.submission4.networks.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailViewModel extends ViewModel {

    private static final String TAG = DetailViewModel.class.getSimpleName();
    private List<Recomendation> recomendationList;
    private List<Recomendation> recomendation;
    private List<String> listGenre;
    private List<Genre> genres;
    private ApiService service;
    private MutableLiveData<MovieDetail> listDetailMovie = new MutableLiveData<>();
    private MutableLiveData<TvDetail> listDetailTv = new MutableLiveData<>();
    private MutableLiveData<Integer> code = new MutableLiveData<>();
    private MutableLiveData<List<Recomendation>> listRecomMovie = new MutableLiveData<>();
    private MutableLiveData<List<Recomendation>> listRecomTv = new MutableLiveData<>();
    private Integer statusCode;

    public void generateDetailMovie(int id) {
        service = RetrofitUtils.getInstance().create(ApiService.class);
        Call<ResponseGenre> genreCall = service.getGenreMovie(Constants.API_KEY, Constants.LANGUAGE);
        genreCall.enqueue(new Callback<ResponseGenre>() {
            @Override
            public void onResponse(Call<ResponseGenre> call, Response<ResponseGenre> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error Genre Code : " + response.code());
                    return;
                }
                genres = response.body().getGenres();
            }

            @Override
            public void onFailure(Call<ResponseGenre> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
            }
        });

        Call<MovieDetail> movieDetailCall = service.getDetailMovie(id, Constants.API_KEY, Constants.LANGUAGE);
        movieDetailCall.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                statusCode = response.code();
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error Detail Code : " + statusCode);
                    code.postValue(statusCode);
                } else {
                    MovieDetail movieDetail = response.body();
                    listDetailMovie.postValue(movieDetail);
                    code.postValue(statusCode);
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
                statusCode = 12001;
                code.postValue(statusCode);
            }
        });
    }

    public void generateRecomendationMovie(int id) {
        service = RetrofitUtils.getInstance().create(ApiService.class);
        Call<ResponseRecomendation> recomendationCall = service.getMovieRecomen(id, Constants.API_KEY, Constants.LANGUAGE);
        recomendationCall.enqueue(new Callback<ResponseRecomendation>() {
            @Override
            public void onResponse(Call<ResponseRecomendation> call, Response<ResponseRecomendation> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error Detail Code : " + response.code());
                    return;
                }
                recomendation = response.body().getResults();
                recomendationList = new ArrayList<>();

                for (Recomendation recomendation : recomendation) {
                    listGenre = new ArrayList<>();
                    Log.d("Isi : ", recomendation.getTitle());

                    for (Integer idGenre : recomendation.getGenreIds()) {
                        for (Genre genre : genres) {
                            if (idGenre.equals(genre.getId())) {
                                listGenre.add(genre.getName());
                            }
                        }
                    }

                    Recomendation recomen = new Recomendation();
                    recomen.setId(recomendation.getId());
                    recomen.setPosterPath(recomendation.getPosterPath());
                    recomen.setTitle(recomendation.getTitle());
                    recomen.setVoteAverage(recomendation.getVoteAverage());
                    recomen.setGenre(listGenre);
                    recomendationList.add(recomen);
                }
                listRecomMovie.postValue(recomendationList);
            }

            @Override
            public void onFailure(Call<ResponseRecomendation> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
            }
        });

    }

    public void generateDetailTv(int id) {
        service = RetrofitUtils.getInstance().create(ApiService.class);
        Call<ResponseGenre> genreCall = service.getGenreTv(Constants.API_KEY, Constants.LANGUAGE);
        genreCall.enqueue(new Callback<ResponseGenre>() {
            @Override
            public void onResponse(Call<ResponseGenre> call, Response<ResponseGenre> response) {
                if (response.isSuccessful()) {
                    genres = response.body().getGenres();
                } else {
                    Log.e(TAG, "Error Genre Code : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseGenre> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
            }
        });

        Call<TvDetail> movieDetailCall = service.getDetailTv(id, Constants.API_KEY, Constants.LANGUAGE);
        movieDetailCall.enqueue(new Callback<TvDetail>() {
            @Override
            public void onResponse(Call<TvDetail> call, Response<TvDetail> response) {
                statusCode = response.code();
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error Detail Code : " + response.code());
                    code.postValue(statusCode);
                } else {
                    TvDetail tvDetail = response.body();
                    listDetailTv.postValue(tvDetail);

                    Log.d("Data Movie Detail : ", tvDetail.getTitle());
                    code.postValue(statusCode);
                }
            }

            @Override
            public void onFailure(Call<TvDetail> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
                statusCode = 12001;
                code.postValue(statusCode);
            }
        });
    }

    public void generateRecomendationTv(int id) {
        service = RetrofitUtils.getInstance().create(ApiService.class);
        Call<ResponseRecomendation> recomendationCall = service.getTvRecomen(id, Constants.API_KEY, Constants.LANGUAGE);
        recomendationCall.enqueue(new Callback<ResponseRecomendation>() {
            @Override
            public void onResponse(Call<ResponseRecomendation> call, Response<ResponseRecomendation> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Error Detail Code : " + response.code());
                } else {
                    recomendation = response.body().getResults();
                    recomendationList = new ArrayList<>();

                    for (Recomendation recomendation : recomendation) {
                        listGenre = new ArrayList<>();

                        for (Integer idGenre : recomendation.getGenreIds()) {
                            for (Genre genre : genres) {
                                if (idGenre.equals(genre.getId())) {
                                    listGenre.add(genre.getName());
                                }
                            }
                        }

                        Recomendation recomen = new Recomendation();
                        recomen.setId(recomendation.getId());
                        recomen.setPosterPath(recomendation.getPosterPath());
                        recomen.setTitle(recomendation.getTitle());
                        recomen.setVoteAverage(recomendation.getVoteAverage());
                        recomen.setGenre(listGenre);
                        recomendationList.add(recomen);
                    }
                    listRecomTv.postValue(recomendationList);
                }

            }

            @Override
            public void onFailure(Call<ResponseRecomendation> call, Throwable t) {
                Log.e("Error Message : ", t.getMessage());
            }
        });
    }

    public LiveData<MovieDetail> getMovieDetail() {
        return listDetailMovie;
    }

    public LiveData<TvDetail> getTvDetail() {
        return listDetailTv;
    }

    public LiveData<List<Recomendation>> getRecomendationMovie() {
        return listRecomMovie;
    }

    public LiveData<List<Recomendation>> getRecomendationTv() {
        return listRecomTv;
    }


    public LiveData<Integer> getResponseCode() {
        return code;
    }
}
