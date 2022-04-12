package com.oushang.iqiyi.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.oushang.iqiyi.R;

/**
 * Created by DELL on 2021-08-30 10:47
 *
 * @description:
 * @Last Modified by  DELL on 2021-08-30 10:47
 */
public final class NotificationBuilder {

    public static final String NOTIFICATION_CHANNEL_ID = "com.oushang.iqiyi.CHANNEL_ID";
    public static final int NOTIFICATION_ID = 0xA001;
    public static final int NOTIFICATION_ID_2 = 0xA002;

    private final Context mContext;
    private final NotificationManager mNotificationManager;

    public NotificationBuilder(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public Notification buildNotification() {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);

        return builder.setSmallIcon(R.drawable.ic_iqiyi)
                .setContentTitle("爱奇艺")
                .setContentText("")
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean nowPlayingChannelExists() {
        return mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null;
    }

    private boolean shouldCreateNowPlayingChannel() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNowPlayingChannel() {
        final NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "当前播放",
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("");
        mNotificationManager.createNotificationChannel(channel);
    }
}
