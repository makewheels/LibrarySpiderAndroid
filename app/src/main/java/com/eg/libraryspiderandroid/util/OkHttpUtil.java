package com.eg.libraryspiderandroid.util;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkHttpUtil {
    public static String BASE_URL = "http://192.168.99.193:5001/libraryapp";
    private static OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * 初始化BASE_URL
     *
     * @param wifiSsid
     */
    public static void initBaseUrl(String wifiSsid) {
        if (wifiSsid.equals("dqlib") || wifiSsid.equals("office"))
            BASE_URL = "http://baidu.server.qbserver.cn:5001/libraryapp";
        else
        BASE_URL = "http://192.168.99.193:5001/libraryapp";
    }

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
