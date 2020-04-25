package com.eg.libraryspiderandroid.util;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkHttpUtil {
    public static String BASE_URL = "http://192.168.99.193/libraryapp";
    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static Call getCall(String url) {
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .build();
        return okHttpClient.newCall(request);
    }

    public static Call getCallByCompleteUrl(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return okHttpClient.newCall(request);
    }

}
