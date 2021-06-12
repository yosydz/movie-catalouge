package com.gudangide.submission4.network;

import com.gudangide.submission4.models.ResponseGenre;
import com.gudangide.submission4.models.ResponseMovie;
import com.gudangide.submission4.models.ResponseRecomendation;
import com.gudangide.submission4.models.ResponseTv;
import com.gudangide.submission4.models.pojo.MovieDetail;
import com.gudangide.submission4.models.pojo.TvDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("discover/movie")
    Call<ResponseMovie> getMovie(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") Integer page
    );

    @GET("discover/tv")
    Call<ResponseTv> getTvShow(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") Integer page
    );

    @GET("genre/movie/list")
    Call<ResponseGenre> getGenreMovie(
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("genre/tv/list")
    Call<ResponseGenre> getGenreTv(
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("movie/{movie_id}")
    Call<MovieDetail> getDetailMovie(
            @Path("movie_id") Integer movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("tv/{tv_id}")
    Call<TvDetail> getDetailTv(
            @Path("tv_id") Integer tvId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("movie/{movie_id}/recommendations")
    Call<ResponseRecomendation> getMovieRecomen(
            @Path("movie_id") Integer movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("tv/{tv_id}/recommendations")
    Call<ResponseRecomendation> getTvRecomen(
            @Path("tv_id") Integer tvId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("search/movie")
    Call<ResponseMovie> getSearchMovie(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("query") String query
    );

    @GET("search/tv")
    Call<ResponseTv> getSearchTv(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("query") String query
    );

    @GET("discover/movie")
    Call<ResponseMovie> getReleaseMovie(
            @Query("api_key") String apiKey,
            @Query("primary_release_date.gte") String releaseGte,
            @Query("primary_release_date.lte") String releaseLte

    );
}
