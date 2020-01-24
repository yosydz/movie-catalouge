package com.gudangide.consumerfavorite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gudangide.consumerfavorite.R;
import com.gudangide.consumerfavorite.model.Favorite;
import com.gudangide.consumerfavorite.network.Constants;

import java.util.List;

public class FavoriteMovieAdapter extends RecyclerView.Adapter<FavoriteMovieAdapter.ViewHolder> {

    private List<Favorite> favoriteList;
    private Context context;
    private static ClickListener clickListener;

    public FavoriteMovieAdapter(List<Favorite> favoriteList, Context context) {
        this.favoriteList = favoriteList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteMovieAdapter.ViewHolder holder, int position) {
        Favorite favorite = favoriteList.get(position);
        Glide.with(context).load(Constants.POSTER_URL + favorite.getPoster()).into(holder.ivPoster);
        holder.title.setText(favorite.getTitle());
        holder.rating.setText(String.valueOf(favorite.getRating()));
        holder.genre.setText(favorite.getGenre());
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivPoster;
        TextView title, rating, genre;

        public ViewHolder(@NonNull View view) {
            super(view);
            itemView.setOnClickListener(this);
            ivPoster = view.findViewById(R.id.iv_poster);
            title = view.findViewById(R.id.title);
            rating = view.findViewById(R.id.rating);
            genre = view.findViewById(R.id.genre);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        FavoriteMovieAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}