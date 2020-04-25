package com.eg.libraryspiderandroid;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.eg.libraryspiderandroid.util.OkHttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Button btn_run;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_run = findViewById(R.id.btn_run);
        textView = findViewById(R.id.textView);
        addListeners();

    }

    /**
     * 执行任务
     *
     * @param barcodePositionList
     */
    private void runMission(List<BarcodePosition> barcodePositionList) {
        for (final BarcodePosition barcodePosition : barcodePositionList) {
            final String barcode = barcodePosition.getBarcode();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(barcode);
                }
            });
            String url = "http://10.0.15.12/TSDW/GotoFlash.aspx?szBarCode=" + barcode;
            OkHttpUtil.getCallByCompleteUrl(url).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("发生错误runMission" + e.getMessage());
                        }
                    });
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //解析html
                    String html = response.body().string();
                    String position = StringUtils.substringBetween(html, "var strWZxxxxxx = \"", "\";");
                    String message = StringUtils.substringBetween(html, "var strMsg = \"", "\";");
                    barcodePosition.setPosition(position);
                    barcodePosition.setMessage(message);
                    //签名
                    long timestamp = System.currentTimeMillis();
                    barcodePosition.setTimestamp(timestamp);
                    String signKey = "vPUYt6q1AzmmjzXG";
                    String sign = DigestUtils.md5Hex(barcode + position + timestamp + signKey);
                    barcodePosition.setSign(sign);
                }
            });
        }
    }

    /**
     * 提交任务
     *
     * @param barcodePositionList
     */
    private void submitMission(List<BarcodePosition> barcodePositionList) {
        String provider = Build.BRAND + " " + Build.MODEL + " " + Build.VERSION.RELEASE;
        System.out.println(provider);
        RequestBody formBody = new FormBody.Builder()
                .add("provider", provider)
                .add("barcodePositionJson", new Gson().toJson(barcodePositionList))
                .build();
        Request request = new Request.Builder()
                .url(OkHttpUtil.BASE_URL + "/book/submitPositionMission")
                .post(formBody)
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("submitMission发生错误：" + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            textView.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void addListeners() {
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取任务
                OkHttpUtil.getCall("/book/requestPositionMission?password=ETwrayANWeniq6HY").enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response)
                            throws IOException {
                        String json = response.body().string();
                        List<BarcodePosition> barcodePositionList =
                                new Gson().fromJson(json, new TypeToken<List<BarcodePosition>>() {
                                }.getType());
                        //执行任务
                        runMission(barcodePositionList);
                        //提交任务
                        submitMission(barcodePositionList);
                    }
                });
            }
        });
    }
}
