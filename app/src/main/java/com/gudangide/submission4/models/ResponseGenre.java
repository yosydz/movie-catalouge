package com.gudangide.submission4.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.gudangide.submission4.models.pojo.Genre;

import java.util.List;

public class ResponseGenre {
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
