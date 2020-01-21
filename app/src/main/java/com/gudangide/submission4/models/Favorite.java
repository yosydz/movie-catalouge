package com.gudangide.submission4.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Favorite implements Parcelable {

    private int id;
    private String title;
    private String poster;
    private String rating;
    private String genre;

    public Favorite() {
    }

    public Favorite(int id, String title, String poster, String rating, String genre) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.rating = rating;
        this.genre = genre;
    }

    protected Favorite(Parcel in) {
        id = in.readInt();
        title = in.readString();
        poster = in.readString();
        rating = in.readString();
        genre = in.readString();
    }

    public static final Creator<Favorite> CREATOR = new Creator<Favorite>() {
        @Override
        public Favorite createFromParcel(Parcel in) {
            return new Favorite(in);
        }

        @Override
        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(poster);
        parcel.writeString(rating);
        parcel.writeString(genre);
    }
}
