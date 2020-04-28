package com.eg.libraryspiderandroid.activity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eg.libraryspiderandroid.R;
import com.eg.libraryspiderandroid.barcodeposition.BarcodePosition;
import com.eg.libraryspiderandroid.util.OkHttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkActivity extends AppCompatActivity {
    private Button btn_run;
    private TextView textView;
    private ScrollView scrollView;

    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);

        btn_run = findViewById(R.id.btn_run);
        textView = findViewById(R.id.textView);
        scrollView = findViewById(R.id.scrollView);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //初始化okhttpBaseUrl
        String wifiSsid = wifiManager.getConnectionInfo().getSSID();
        OkHttpUtil.initBaseUrl(wifiSsid);

        addListeners();
    }

    private List<String> lines = new ArrayList<>();

    /**
     * 显示输出日志
     *
     * @param text
     */
    private void addText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lines.add(text);
                while (lines.size() > 200) {
                    lines.remove(0);
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < lines.size(); i++) {
                    stringBuilder.append(lines.get(i));
                    if (i != lines.size() - 1)
                        stringBuilder.append("\n");
                }
                textView.setText(stringBuilder.toString());
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                    }
                }.start();
            }
        });
    }

    /**
     * 监听
     */
    private void addListeners() {
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMission();
            }
        });
    }

    /**
     * 启动任务
     */
    private void startMission() {
        //获取任务
        OkHttpUtil.getCall("/bookPosition/requestPositionMission?password=ETwrayANWeniq6HY").enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                addText("requestPositionMission发生错误：" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {
                String json = response.body().string();
                //初始化任务
                barcodePositionList = new Gson().fromJson(json
                        , new TypeToken<List<BarcodePosition>>() {
                        }.getType());
                progressList = new ArrayList<>();
                for (int i = 0; i < barcodePositionList.size(); i++) {
                    progressList.add(false);
                }
                //执行任务
                runMission();
            }
        });
    }

    private List<BarcodePosition> barcodePositionList;
    private List<Boolean> progressList;
    private final Object progressLock = new Object();

    /**
     * 检查barCodeList是不是都获取完了
     */
    private boolean checkProgress() {
        synchronized (progressLock) {
            for (Boolean isFinish : progressList) {
                if (!isFinish)
                    return false;
            }
        }
        return true;
    }

    /**
     * 执行任务
     */
    private void runMission() {
        //向内网发请求获取书的位置，这里应该用多线程
        for (int i = 0; i < barcodePositionList.size(); i++) {
            final BarcodePosition barcodePosition = barcodePositionList.get(i);
            final String barcode = barcodePosition.getBarcode();
            addText("服务器响应：" + barcode);

            String url;
            String wifiSsid = wifiManager.getConnectionInfo().getSSID();
            if (wifiSsid.equals("dqlib") || wifiSsid.equals("office"))
                url = "http://10.0.15.12/TSDW/GotoFlash.aspx?szBarCode=" + barcode;
            else
                url = "http://192.168.99.193:5001/libraryapp/bookPosition/goToFlashDemo";

            final int finalI = i;
            OkHttpUtil.getCallByCompleteUrl(url).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                    e.printStackTrace();
                    addText("runMission发生错误 " + e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //解析html
                    String html = response.body().string();
                    final String position = StringUtils.substringBetween(
                            html, "var strWZxxxxxx = \"", "\";");
                    String message = StringUtils.substringBetween(
                            html, "var strMsg = \"", "\";");
                    addText("从GotoFlash获取到：" + position + message);
                    barcodePosition.setPosition(position);
                    barcodePosition.setMessage(message);
                    //签名
                    long timestamp = System.currentTimeMillis();
                    barcodePosition.setTimestamp(timestamp);
                    String signKey = "vPUYt6q1AzmmjzXG";
                    String sign = DigestUtils.md5Hex(barcode + position + timestamp + signKey);
                    barcodePosition.setSign(sign);
                    synchronized (progressLock) {
                        //一个barCode的任务完成了，更新进度
                        progressList.set(finalI, true);
                        //检查是不是所有的任务都完成了，如果都完成了就提交任务
                        if (checkProgress()) {
                            submitMission();
                        }
                    }
                }
            });
        }
    }

    /**
     * 提交任务
     */
    private void submitMission() {
        String provider = Build.BRAND + "--" + Build.MODEL + "--" + Build.VERSION.RELEASE;
        System.out.println(provider);
        RequestBody formBody = new FormBody.Builder()
                .add("provider", provider)
                .add("barcodePositionJson", new Gson().toJson(barcodePositionList))
                .build();
        Request request = new Request.Builder()
                .url(OkHttpUtil.BASE_URL + "/bookPosition/submitPositionMission")
                .post(formBody)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                e.printStackTrace();
                addText("submitMission发生错误：" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                addText("submitMission响应：" + response.body().string());
            }
        });
    }

}
