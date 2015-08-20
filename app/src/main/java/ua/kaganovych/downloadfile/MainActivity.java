package ua.kaganovych.downloadfile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button mDownload;
    private TextView mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownload = (Button) findViewById(R.id.button);
        mDate = (TextView) findViewById(R.id.downloadTime);
        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(MainActivity.this, DownloadService.class);
                    intent.putExtra(Const.URL, Const.URL_PATH);
                    startService(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Please, check your internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long time = preferences.getLong(Const.TIME, 0);
        if (time > 0) {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(time));
            mDate.setText("Last update: " + date);
            mDownload.setText("Update");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (info != null && info.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }
}
