package com.gudangide.submission4.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gudangide.submission4.R;
import com.gudangide.submission4.models.pojo.TvShow;
import com.gudangide.submission4.network.Constants;

import java.util.List;

public class PopularTvAdapter extends RecyclerView.Adapter<PopularTvAdapter.ViewHolder> {

    Context context;
    List<TvShow> arrayList;
    public static ClickListener clickListener;

    public PopularTvAdapter(Context context, List<TvShow> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new PopularTvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TvShow tvShow = arrayList.get(position);
        Glide.with(context)
                .load(Constants.POSTER_URL + tvShow.getPosterPath())
                .into(holder.ivPoster);
        holder.title.setText(tvShow.getName());
        holder.rating.setText(String.valueOf(tvShow.getVoteAverage()));
        StringBuilder txtGenre = new StringBuilder();
        int i = 0;
        for (String genre : tvShow.getGenre()) {
            i++;
            if (tvShow.getGenre().size() <= 1) {
                txtGenre.append(genre);
            } else if (i == 1) {
                txtGenre.append(genre).append(", ");
            } else if (i == 2) {
                txtGenre.append(genre);
            }
        }
        holder.genre.setText(txtGenre.toString());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
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
        PopularTvAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
