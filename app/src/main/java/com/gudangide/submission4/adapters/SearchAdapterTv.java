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

public class SearchAdapterTv extends RecyclerView.Adapter<SearchAdapterTv.ViewHolder> {

    private List<TvShow> searchTv;
    private Context context;
    private static ClickListener clickListener;

    public SearchAdapterTv(List<TvShow> searchTv, Context context) {
        this.searchTv = searchTv;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapterTv.ViewHolder holder, int position) {
        TvShow tv = searchTv.get(position);
        Glide.with(context).load(Constants.POSTER_URL + tv.getPosterPath()).into(holder.ivPoster);
        holder.title.setText(tv.getName());
        holder.rating.setText(String.valueOf(tv.getVoteAverage()));
        StringBuilder txtGenre = new StringBuilder();
        int i = 0;
        for (String genre : tv.getGenre()) {
            i++;
            if (i == 1) {
                txtGenre.append(genre).append(", ");
            }
            if (i == 2) {
                txtGenre.append(genre);
            }
        }
        holder.genre.setText(txtGenre.toString());
    }

    @Override
    public int getItemCount() {
        return searchTv.size();
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
        SearchAdapterTv.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
