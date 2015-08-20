package ua.kaganovych.downloadfile;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import java.util.Random;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int notificationId = new Random().nextInt();

        Intent serviceIntent = new Intent(context, DownloadService.class);
        serviceIntent.putExtra(Const.URL, Const.URL_PATH);
        PendingIntent servicePendingIntent = PendingIntent.getService(context, notificationId, serviceIntent, 0);

        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("New files!")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("You can download new files")
                .addAction(R.mipmap.ic_launcher, "Update", servicePendingIntent)
                .setTicker("New files!")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        mNotifyManager.notify(Const.ID, mBuilder.build());
    }
}
