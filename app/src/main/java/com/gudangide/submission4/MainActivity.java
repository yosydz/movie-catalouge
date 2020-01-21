package com.gudangide.submission4;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.gudangide.submission4.fragments.FavoriteFragment;
import com.gudangide.submission4.fragments.MovieFragment;
import com.gudangide.submission4.fragments.TvFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.gudangide.submission4.DetailActivity.REQUEST_OKE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        Fragment activeFragment = getSupportFragmentManager().findFragmentById(R.id.fl_main);
        //check fragment via instanceof

        if (activeFragment instanceof MovieFragment) {
            toolbar.setTitle(getResources().getString(R.string.title_movie));
        } else {
            toolbar.setTitle(getResources().getString(R.string.title_tv));
        }
        if (savedInstanceState == null) {
            toolbar.setTitle(getResources().getString(R.string.title_movie));
            fragment = new MovieFragment();
            loadFragment(fragment);
        }
//        toolbar.setTitle(getResources().getString(R.string.title_movie));
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        toggle.setHomeAsUpIndicator(R.drawable.ic_burger);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView date = headerView.findViewById(R.id.date);
        TextView dateMonth = headerView.findViewById(R.id.single_date);

        Date today = Calendar.getInstance().getTime();
        Locale locale = getResources().getConfiguration().locale;
        SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy", locale);
        String current = formatter.format(today);

        SimpleDateFormat formatter2 = new SimpleDateFormat("dd", locale);
        String current2 = formatter2.format(today);

        date.setText(current);
        dateMonth.setText(current2);

        TextView versiapp = findViewById(R.id.versiaplikasi);
        versiapp.setText(getResources().getString(R.string.version, BuildConfig.VERSION_NAME));

        BottomNavigationView botNav = findViewById(R.id.bot_nav);

        botNav.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_movie :
                    toolbar.setTitle(getResources().getString(R.string.title_movie));
                    fragment = new MovieFragment();
                    break;

                case R.id.navigation_tvshow :
                    toolbar.setTitle(getResources().getString(R.string.title_tv));
                    fragment = new TvFragment();
                    break;

                case R.id.navigation_favorite :
                    toolbar.setTitle(getResources().getString(R.string.title_favorite));
                    fragment = new FavoriteFragment();
                    break;
            }

            loadFragment(fragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_main, fragment, "tag");
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_contactus:
            case R.id.nav_faq:
            case R.id.nav_tac:
                showToast();
                break;

            case R.id.nav_bahasa:
                Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_OKE){
            fragment = new FavoriteFragment();
            loadFragment(fragment);
        }
    }

    private void showToast() {
        Toast.makeText(this, getResources().getString(R.string.toast), Toast.LENGTH_SHORT).show();
    }
}
