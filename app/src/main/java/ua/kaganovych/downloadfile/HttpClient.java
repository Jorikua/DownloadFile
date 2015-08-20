package ua.kaganovych.downloadfile;

import com.squareup.okhttp.OkHttpClient;

public class HttpClient {

    private static OkHttpClient sOkHttpClient;

    public static OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            sOkHttpClient = new OkHttpClient();
        }
        return sOkHttpClient;
    }
}
