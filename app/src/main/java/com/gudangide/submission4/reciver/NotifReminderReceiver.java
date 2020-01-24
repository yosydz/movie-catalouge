package com.gudangide.submission4.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gudangide.submission4.R;
import com.gudangide.submission4.models.ResponseMovie;
import com.gudangide.submission4.models.pojo.Movie;
import com.gudangide.submission4.networks.ApiService;
import com.gudangide.submission4.networks.Constants;
import com.gudangide.submission4.networks.RetrofitUtils;
import com.gudangide.submission4.notifications.NotificationReminder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gudangide.submission4.notifications.NotificationReminder.CHANNEL_RELEASE_ID;
import static com.gudangide.submission4.notifications.NotificationReminder.CHANNEL_RELEASE_NAME;
import static com.gudangide.submission4.notifications.NotificationReminder.CHANNEL_REMINDER_ID;
import static com.gudangide.submission4.notifications.NotificationReminder.CHANNEL_REMINDER_NAME;
import static com.gudangide.submission4.notifications.NotificationReminder.EXTRA_TYPE;
import static com.gudangide.submission4.notifications.NotificationReminder.ID_RELEASE;
import static com.gudangide.submission4.notifications.NotificationReminder.ID_REMINDER;
import static com.gudangide.submission4.notifications.NotificationReminder.TYPE_RELEASE;

public class NotifReminderReceiver extends BroadcastReceiver {

    List<Movie> movieList;

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(EXTRA_TYPE);
        if (type.equals(TYPE_RELEASE)){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = format.format(new Date());

            ApiService service = RetrofitUtils.getInstance().create(ApiService.class);
            Call<ResponseMovie> call = service.getReleaseMovie(Constants.API_KEY, currentDate, currentDate);
            call.enqueue(new Callback<ResponseMovie>() {
                @Override
                public void onResponse(Call<ResponseMovie> call, Response<ResponseMovie> response) {
                    if (response.isSuccessful()){
                        movieList = response.body().getResults();
                        for (Movie movie : movieList){
                            NotificationReminder notificationReminder = new NotificationReminder();
                            notificationReminder.showNotification(
                                    context,
                                    CHANNEL_RELEASE_ID,
                                    CHANNEL_RELEASE_NAME,
                                    context.getResources().getString(R.string.release_reminder),
                                    movie.getTitle(),
                                    ID_RELEASE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseMovie> call, Throwable t) {

                }
            });

        }else{
            NotificationReminder notificationReminder = new NotificationReminder();
            notificationReminder.showNotification(
                    context,
                    CHANNEL_REMINDER_ID,
                    CHANNEL_REMINDER_NAME,
                    context.getResources().getString(R.string.daily_reminder),
                    context.getResources().getString(R.string.daily_reminder_notification),
                    ID_REMINDER);
        }
    }
}
