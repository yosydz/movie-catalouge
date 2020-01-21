package com.gudangide.submission4;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devs.readmoreoption.ReadMoreOption;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.gson.JsonArray;
import com.gudangide.submission4.adapters.RecomMovieAdapter;
import com.gudangide.submission4.db.FavoriteHelper;
import com.gudangide.submission4.models.pojo.Genre;
import com.gudangide.submission4.models.pojo.Recomendation;
import com.gudangide.submission4.networks.ApiService;
import com.gudangide.submission4.networks.Constants;
import com.gudangide.submission4.viewmodels.DetailViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.GENRE;
import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.ID;
import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.POSTER;
import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.RATING;
import static com.gudangide.submission4.db.DatabaseContract.FavoriteColumns.TITLE;
import static com.gudangide.submission4.db.DatabaseContract.TABLE_MOVIE_NAME;
import static com.gudangide.submission4.db.DatabaseContract.TABLE_TV_NAME;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private TextView desc, title, date, reviews;
    private ImageView banner, poster, favorite;
    private RatingBar ratingBar;
    private List<Recomendation> recomendationList;
    private List<Recomendation> recomendation;
    private List<String> listGenre;
    private List<Genre> genres;
    private ApiService service;
    private RecyclerView rViewRecomen;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private ConstraintLayout errorLayout;
    private FavoriteHelper favoriteHelper;
    private TextView tvError;
    private ImageButton refresh;
    private DetailViewModel viewModel;
    public static final int REQUEST_OKE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        rViewRecomen = findViewById(R.id.recycle_recomen);
        rViewRecomen.setHasFixedSize(true);
        desc = findViewById(R.id.desc);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);
        reviews = findViewById(R.id.reviews);
        favorite =findViewById(R.id.iv_favorite);
        banner = findViewById(R.id.iv_banner);
        poster = findViewById(R.id.iv_poster);
        ratingBar = findViewById(R.id.ratingBar);
        scrollView = findViewById(R.id.scrollViews);

        errorLayout = findViewById(R.id.error_layout);
        tvError = findViewById(R.id.tv_message);
        refresh = findViewById(R.id.refresh);

        favoriteHelper = FavoriteHelper.getInstance(getApplicationContext());
        favoriteHelper.open();

        progressBar = findViewById(R.id.spin_kit);
        Sprite rotatingCircle = new WanderingCubes();
        progressBar.setIndeterminateDrawable(rotatingCircle);

        viewModel = new ViewModelProvider(DetailActivity.this, new ViewModelProvider.NewInstanceFactory()).get(DetailViewModel.class);
        showLoading(true);
        generateData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        favoriteHelper.close();
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        setResult(REQUEST_OKE, mIntent);
        super.onBackPressed();
    }

    private void generateData() {
        if (getIntent().hasExtra("idMovie")) {
            int idMovie = getIntent().getIntExtra("idMovie", 0);
            if (viewModel.getMovieDetail().getValue() == null) {
                viewModel.generateDetailMovie(idMovie);
                viewModel.getResponseCode().observe(this, integer -> {
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
                            viewModel.generateDetailMovie(idMovie);
                            generateRecomendationMovie(idMovie);
                        });
                    }
                });
            }
            favoriteHelper = new FavoriteHelper(this, TABLE_MOVIE_NAME);

            viewModel.getMovieDetail().observe(this, movieDetail -> {
                title.setText(movieDetail.getTitle());
                String startDateString = movieDetail.getReleaseDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                try {
                    date.setText(sdf2.format(sdf.parse(startDateString)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                reviews.setText(getResources().getString(R.string.reviewers, movieDetail.getVoteCount().toString()));
                ratingBar.setRating(movieDetail.getVoteAverage().floatValue() / 2);
                Glide.with(DetailActivity.this).load(Constants.POSTER_URL + movieDetail.getPosterPath()).into(poster);
                Glide.with(DetailActivity.this).load(Constants.BACKDROP_URL + movieDetail.getBackdropPath()).into(banner);
                showLoading(false);

                ReadMoreOption readMoreOption = new ReadMoreOption.Builder(DetailActivity.this)
                        .textLength(4, ReadMoreOption.TYPE_LINE) // OR
                        //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
                        .moreLabel(getResources().getString(R.string.more_view))
                        .lessLabel(getResources().getString(R.string.show_less))
                        .moreLabelColor(Color.GRAY)
                        .lessLabelColor(Color.WHITE)
                        .labelUnderLine(true)
                        .expandAnimation(true)
                        .build();

                readMoreOption.addReadMoreTo(desc, movieDetail.getOverview());
                Cursor cursor = favoriteHelper.queryById(movieDetail.getId());
                String id = null;
                if (cursor != null){
                    while (cursor.moveToNext()){
                        id = cursor.getString(cursor.getColumnIndex("id"));
                    }
                }
                AtomicBoolean status = new AtomicBoolean(false);
                if (id != null){
                    favorite.setImageResource(R.drawable.ic_favorite_red);
                    status.set(true);
                }else {
                    favorite.setImageResource(R.drawable.ic_favorite_border);
                    status.set(false);
                }

                favorite.setOnClickListener(view -> {
                    if (status.get()){
                        long result = favoriteHelper.deleteById(movieDetail.getId());
                        if (result > 0){
                            favorite.setImageResource(R.drawable.ic_favorite_border);
                            Toast.makeText(DetailActivity.this, "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
                            status.set(false);
                        }else {
                            Toast.makeText(DetailActivity.this, "Gagal menghapus favorit", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        ContentValues values = new ContentValues();
                        values.put(ID, movieDetail.getId());
                        values.put(POSTER, movieDetail.getPosterPath());
                        values.put(TITLE, movieDetail.getTitle());
                        values.put(RATING, movieDetail.getVoteAverage());
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(movieDetail.getGenres().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        StringBuilder txtGenre = new StringBuilder();
                        JSONObject jsonObject = new JSONObject();
                        for (int i = 0; i < jsonArray.length(); i++ ){
                            try {
                                jsonObject = jsonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (jsonArray.length() <= 1) {
                                try {
                                    txtGenre.append(jsonObject.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else if (i == 1) {
                                try {
                                    txtGenre.append(jsonObject.getString("name")).append(", ");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (i == 2) {
                                try {
                                    txtGenre.append(jsonObject.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        values.put(GENRE, String.valueOf(txtGenre));
                        long result = favoriteHelper.insert(values);
                        if (result > 0){
                            favorite.setImageResource(R.drawable.ic_favorite_red);
                            Toast.makeText(DetailActivity.this, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show();
                            status.set(true);
                        }else {
                            Toast.makeText(DetailActivity.this, "Gagal menambah favorit", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
            generateRecomendationMovie(idMovie);

        } else if (getIntent().hasExtra("idTv")) {
            int idTv = getIntent().getIntExtra("idTv", 0);
            if (viewModel.getTvDetail().getValue() == null) {
                viewModel.generateDetailTv(idTv);
                viewModel.getResponseCode().observe(this, integer -> {
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
                            viewModel.generateDetailTv(idTv);
                            generateRecomendationTv(idTv);
                        });
                    }
                });
            }

            favoriteHelper = new FavoriteHelper(this, TABLE_TV_NAME);

            viewModel.getTvDetail().observe(this, tvDetail -> {
                title.setText(tvDetail.getTitle());
                String startDateString = tvDetail.getReleaseDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                try {
                    date.setText(sdf2.format(sdf.parse(startDateString)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                reviews.setText(getResources().getString(R.string.reviewers, tvDetail.getVoteCount().toString()));
                ratingBar.setRating(tvDetail.getVoteAverage().floatValue() / 2);
                Glide.with(DetailActivity.this).load(Constants.POSTER_URL + tvDetail.getPosterPath()).into(poster);
                Glide.with(DetailActivity.this).load(Constants.BACKDROP_URL + tvDetail.getBackdropPath()).into(banner);
                showLoading(false);

                ReadMoreOption readMoreOption = new ReadMoreOption.Builder(DetailActivity.this)
                        .textLength(4, ReadMoreOption.TYPE_LINE) // OR
                        //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
                        .moreLabel(getResources().getString(R.string.more_view))
                        .lessLabel(getResources().getString(R.string.show_less))
                        .moreLabelColor(Color.GRAY)
                        .lessLabelColor(Color.WHITE)
                        .labelUnderLine(true)
                        .expandAnimation(true)
                        .build();

                readMoreOption.addReadMoreTo(desc, tvDetail.getOverview());

                Cursor cursor = favoriteHelper.queryById(tvDetail.getId());
                String id = null;
                if (cursor.getCount() > 0){
                    while (cursor.moveToNext()){
                        id = cursor.getString(cursor.getColumnIndex("id"));
                    }
                }
                AtomicBoolean status = new AtomicBoolean(false);
                if (id != null){
                    favorite.setImageResource(R.drawable.ic_favorite_red);
                    status.set(true);
                }else {
                    favorite.setImageResource(R.drawable.ic_favorite_border);
                    status.set(false);
                }

                favorite.setOnClickListener(view -> {
                    if (status.get()){
                        long result = favoriteHelper.deleteById(tvDetail.getId());
                        if (result > 0){
                            favorite.setImageResource(R.drawable.ic_favorite_border);
                            Toast.makeText(DetailActivity.this, "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
                            status.set(false);
                        }else {
                            Toast.makeText(DetailActivity.this, "Gagal menghapus favorit", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        ContentValues values = new ContentValues();
                        values.put(ID, tvDetail.getId());
                        values.put(POSTER, tvDetail.getPosterPath());
                        values.put(TITLE, tvDetail.getTitle());
                        values.put(RATING, tvDetail.getVoteAverage());
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(tvDetail.getGenres().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        StringBuilder txtGenre = new StringBuilder();
                        JSONObject jsonObject = new JSONObject();
                        for (int i = 0; i < jsonArray.length(); i++ ){
                            try {
                                jsonObject = jsonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (jsonArray.length() <= 1) {
                                try {
                                    txtGenre.append(jsonObject.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else if (i == 1) {
                                try {
                                    txtGenre.append(jsonObject.getString("name")).append(", ");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (i == 2) {
                                try {
                                    txtGenre.append(jsonObject.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        values.put(GENRE, String.valueOf(txtGenre));
                        long result = favoriteHelper.insert(values);
                        if (result > 0){
                            favorite.setImageResource(R.drawable.ic_favorite_red);
                            Toast.makeText(DetailActivity.this, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show();
                            status.set(true);
                        }else {
                            Toast.makeText(DetailActivity.this, "Gagal menambah favorit", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });
            generateRecomendationTv(idTv);
        }
    }

    private void generateRecomendationMovie(int idMovie) {
        if (viewModel.getRecomendationMovie().getValue() == null) {
            viewModel.generateRecomendationMovie(idMovie);
        }
        viewModel.getRecomendationMovie().observe(this, recomendations -> {
            LinearLayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
            rViewRecomen.setLayoutManager(layoutManager);
            RecomMovieAdapter adapter = new RecomMovieAdapter(recomendations, DetailActivity.this);
            adapter.setOnItemClickListener((position, v) -> {
                Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                Log.d("idMovie", String.valueOf(recomendations.get(position).getId()));
                intent.putExtra("idMovie", recomendations.get(position).getId());
                startActivity(intent);
                finish();
            });
            rViewRecomen.setAdapter(adapter);
        });
    }

    private void generateRecomendationTv(int idTv) {
        if (viewModel.getRecomendationTv().getValue() == null) {
            viewModel.generateRecomendationTv(idTv);
        }

        viewModel.getRecomendationTv().observe(this, recomendations -> {
            LinearLayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
            rViewRecomen.setLayoutManager(layoutManager);
            RecomMovieAdapter adapter = new RecomMovieAdapter(recomendations, DetailActivity.this);
            adapter.setOnItemClickListener((position, v) -> {
                Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                intent.putExtra("idTv", recomendations.get(position).getId());
                startActivity(intent);
                finish();
            });
            rViewRecomen.setAdapter(adapter);
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
            scrollView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }
}
