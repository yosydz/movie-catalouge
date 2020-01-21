package com.gudangide.submission4.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.gudangide.submission4.R;
import com.gudangide.submission4.fragments.FavoriteMovieFragment;
import com.gudangide.submission4.fragments.FavoriteTvFragment;

public class FavoritePagerAdapter extends FragmentStatePagerAdapter {

    private final Context context;

    public FavoritePagerAdapter(Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    private final int[] TAB_TITLES = new int[]{
            R.string.title_movie,
            R.string.title_tv
    };

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0 :
                fragment = new FavoriteMovieFragment();
                break;

            case 1 :
                fragment = new FavoriteTvFragment();
                break;
        }

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
