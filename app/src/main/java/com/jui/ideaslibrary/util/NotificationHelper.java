package com.jui.ideaslibrary.util;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import com.jui.ideaslibrary.MainActivity;
import com.jui.ideaslibrary.R;

import androidx.core.app.NotificationCompat;

public class NotificationHelper  {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;
    private Context ctx;
    public NotificationHelper(Context base) {
        this.ctx=base;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }
    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(ctx, channelID)
                .setContentTitle("Have you recorded an idea?")
                .setContentText("Write down your ideas for the day!")
                .setContentIntent(PendingIntent.getActivity(ctx,0, new Intent(ctx, MainActivity.class),0))

                .setSmallIcon(R.drawable.ideaicon);
    }
}