package ua.kaganovych.downloadfile;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadService extends IntentService {

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private static final String FOLDER_NAME = "/DownloadService/";
    private static final String FILE_NAME = "downloadFile";
    private static final String JPG = ".jpg";

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(DownloadService.this);
        mBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Download started");

        String url = intent.getStringExtra(Const.URL);
        Request request = new Request.Builder()
                .url(url)
                .build();

        File root = Environment.getExternalStorageDirectory();
        File dir = new File (root.getAbsolutePath() + FOLDER_NAME);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        InputStream input = null;
        OutputStream output = null;

        try {
            Response response = HttpClient.getOkHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                input = response.body().byteStream();
                long fileLength = response.body().contentLength();

                output = new FileOutputStream(dir + "/" + FILE_NAME + JPG);
                byte data[] = new byte[1024];
                long total = 0;
                int count, latestPercentDone;
                int percentDone = -1;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    latestPercentDone = Math.round(total * 100 / fileLength);
                    if (percentDone != latestPercentDone) {
                        percentDone = latestPercentDone;
                        mBuilder.setProgress(100, percentDone, false);
                        mNotifyManager.notify(Const.ID, mBuilder.build());
                    }
                    output.write(data, 0, count);
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            if (input != null){
                try {
                    input.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            if (output != null){
                try{
                    output.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        // When the loop is finished, updates the notification
        mBuilder.setContentText("Download complete")
                // Removes the progress bar
                .setProgress(0, 0, false)
                .setTicker("Download complete")
                .setContentIntent(resultPendingIntent);
        mNotifyManager.notify(Const.ID, mBuilder.build());

        long time = System.currentTimeMillis();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(Const.TIME, time);
        editor.apply();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }
}