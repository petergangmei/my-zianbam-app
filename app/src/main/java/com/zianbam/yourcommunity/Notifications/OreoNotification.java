package com.zianbam.yourcommunity.Notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.app.Notification;

//import com.zianbam.hangouts.Model.Notification;

public class OreoNotification extends ContextWrapper {
    private static final String CHANEL_ID = "com.zianbam.socialhangout";
    private static final String CHANEL_NAME = "zianbam";
    private NotificationManager notificationMaager;
    public OreoNotification(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                createChannel();
        }

    }
@TargetApi((Build.VERSION_CODES.O))
    private void createChannel() {
    NotificationChannel channel = new NotificationChannel(CHANEL_ID, CHANEL_NAME,
            NotificationManager.IMPORTANCE_HIGH);

    channel.enableLights(true);
    channel.enableVibration(true);
    channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

    getManager().createNotificationChannel(channel);

    }
    public NotificationManager getManager(){
        if (notificationMaager == null){
            notificationMaager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationMaager;
    }
    @TargetApi((Build.VERSION_CODES.O))
    public Notification.Builder getOreoNotification(String title, String body,
                                                    PendingIntent pendingIntent, Uri soundUri, String icon){

        long vibrate[] = {100,500,100,500};
        return  new Notification.Builder(getApplicationContext(), CHANEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(soundUri)
                .setAutoCancel(true);

    }
}
