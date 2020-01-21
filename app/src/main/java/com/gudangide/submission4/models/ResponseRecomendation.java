package com.gudangide.submission4.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.gudangide.submission4.models.pojo.Recomendation;

import java.util.List;

public class ResponseRecomendation {

    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<Recomendation> results = null;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Recomendation> getResults() {
        return results;
    }

    public void setResults(List<Recomendation> results) {
        this.results = results;
    }
}
