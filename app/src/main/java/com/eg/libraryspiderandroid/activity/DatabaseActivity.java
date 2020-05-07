package com.eg.libraryspiderandroid.activity;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.eg.libraryspiderandroid.R;
import com.eg.libraryspiderandroid.barcodeposition.BarcodePosition;
import com.eg.libraryspiderandroid.barcodeposition.BarcodePositionDao;
import com.eg.libraryspiderandroid.util.AppDatabase;
import com.eg.libraryspiderandroid.util.OkHttpUtil;
import com.google.gson.Gson;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DatabaseActivity extends AppCompatActivity {
    private ScrollView scrollView;
    private TextView textView;

    private WifiManager wifiManager;
    private BarcodePositionDao barcodePositionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        init();
    }

    private void init() {
        scrollView = findViewById(R.id.scrollView);
        textView = findViewById(R.id.textView);

        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        AppDatabase appDatabase = AppDatabase.getAppDatabase(this);
        barcodePositionDao = appDatabase.getBarcodePositionDao();
    }

    private String currentLocation = "library";

    /**
     * 决定当前位置，以决定url
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_setHome:
                currentLocation = "home";
                Toasty.info(this, "currentLocation = " + currentLocation).show();
                return true;
            case R.id.menu_item_setLibrary:
                currentLocation = "library";
                Toasty.info(this, "currentLocation = " + currentLocation).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class InsertAsyncTask extends AsyncTask<BarcodePosition, Void, Void> {
        private BarcodePositionDao barcodePositionDao;

        InsertAsyncTask(BarcodePositionDao barcodePositionDao) {
            this.barcodePositionDao = barcodePositionDao;
        }

        @Override
        protected Void doInBackground(BarcodePosition... barcodePositions) {
            try {
                barcodePositionDao.insert(barcodePositions);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    static class UpdateAsyncTask extends AsyncTask<BarcodePosition, Void, Void> {
        private BarcodePositionDao barcodePositionDao;

        UpdateAsyncTask(BarcodePositionDao barcodePositionDao) {
            this.barcodePositionDao = barcodePositionDao;
        }

        @Override
        protected Void doInBackground(BarcodePosition... barcodePositions) {
            barcodePositionDao.update(barcodePositions);
            return null;
        }
    }

    static class QueryByBarcodeAsyncTask extends AsyncTask<String, Void, BarcodePosition> {
        private BarcodePositionDao barcodePositionDao;

        QueryByBarcodeAsyncTask(BarcodePositionDao barcodePositionDao) {
            this.barcodePositionDao = barcodePositionDao;
        }

        @Override
        protected BarcodePosition doInBackground(String... barcodes) {
            return barcodePositionDao.queryByBarcode(barcodes[0]);
        }
    }

    static class QueryByMongoIdAsyncTask extends AsyncTask<String, Void, BarcodePosition> {
        private BarcodePositionDao barcodePositionDao;

        QueryByMongoIdAsyncTask(BarcodePositionDao barcodePositionDao) {
            this.barcodePositionDao = barcodePositionDao;
        }

        @Override
        protected BarcodePosition doInBackground(String... mongoIds) {
            return barcodePositionDao.queryByBarcode(mongoIds[0]);
        }
    }

    static class QueryNotCrawlAsyncTask extends AsyncTask<Integer, Void, List<BarcodePosition>> {
        private BarcodePositionDao barcodePositionDao;

        QueryNotCrawlAsyncTask(BarcodePositionDao barcodePositionDao) {
            this.barcodePositionDao = barcodePositionDao;
        }

        @Override
        protected List<BarcodePosition> doInBackground(Integer... amounts) {
            return barcodePositionDao.queryNotCrawl(amounts[0]);
        }
    }

    static class QueryNotCrawledAsyncTask extends AsyncTask<Integer, Void, List<BarcodePosition>> {
        private BarcodePositionDao barcodePositionDao;

        QueryNotCrawledAsyncTask(BarcodePositionDao barcodePositionDao) {
            this.barcodePositionDao = barcodePositionDao;
        }

        @Override
        protected List<BarcodePosition> doInBackground(Integer... amounts) {
            return barcodePositionDao.queryNotCrawl(amounts[0]);
        }
    }

    static class QueryCrawlButNotSubmitAsyncTask extends AsyncTask<Integer, Void, List<BarcodePosition>> {
        private BarcodePositionDao barcodePositionDao;

        QueryCrawlButNotSubmitAsyncTask(BarcodePositionDao barcodePositionDao) {
            this.barcodePositionDao = barcodePositionDao;
        }

        @Override
        protected List<BarcodePosition> doInBackground(Integer... amounts) {
            return barcodePositionDao.queryCrawlButNotSubmit(amounts[0]);
        }
    }

    static class QueryCrawlAndSubmitAsyncTask extends AsyncTask<Integer, Void, List<BarcodePosition>> {
        private BarcodePositionDao barcodePositionDao;

        QueryCrawlAndSubmitAsyncTask(BarcodePositionDao barcodePositionDao) {
            this.barcodePositionDao = barcodePositionDao;
        }

        @Override
        protected List<BarcodePosition> doInBackground(Integer... amounts) {
            return barcodePositionDao.queryCrawlAndSubmit(amounts[0]);
        }
    }

    private List<String> lines = new ArrayList<>();

    /**
     * 显示输出日志
     *
     * @param text
     */
    private synchronized void addText(final String text) {
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

    public void startRequestMission(View view) {
        requestData();
    }

    /**
     * 家里请求barcode
     */
    private void requestData() {
        //发送请求
        OkHttpUtil.getCall("/bookPosition/requestPositionMission?password=ETwrayANWeniq6HY").enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {
                String json = response.body().string();
                if (StringUtils.isEmpty(json))
                    return;
                List<BarcodePosition> barcodePositionList = JSON.parseArray(json,
                        BarcodePosition.class);
                //如果为空，requestData任务结束
                boolean isEmpty = CollectionUtils.isEmpty(barcodePositionList);
                if (isEmpty)
                    return;
                //遍历结果集
                for (final BarcodePosition barcodePosition : barcodePositionList) {
                    //根据mongoId查询数据库
                    BarcodePosition findBarcodePosition = null;
                    try {
                        findBarcodePosition = new QueryByMongoIdAsyncTask(barcodePositionDao)
                                .execute(barcodePosition.getHoldingMongoId()).get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    //如果没有此barcode，保存到数据库
                    if (findBarcodePosition == null) {
                        barcodePosition.setSubmit(false);
                        new InsertAsyncTask(barcodePositionDao).execute(barcodePosition);
                    }
                }
                //继续
                requestData();
            }
        });
    }

    private List<Boolean> progressList;
    private final Object progressLock = new Object();
    //线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(6);

    /**
     * 检查进度
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
     * 在图书馆内网爬位置数据，保存到手机数据库
     */
    public void crawlDataFromLibrary(View view) {
        addText("currentLocation = " + currentLocation);
        //查询数据库
        List<BarcodePosition> barcodePositionList = null;
        try {
            barcodePositionList = new QueryNotCrawlAsyncTask(barcodePositionDao).execute(200).get();
            addText("查出数据：" + barcodePositionList.size() + " 条");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (CollectionUtils.isEmpty(barcodePositionList)) {
            addText("crawlDataFromLibrary the end!!!!!!!!");
            executorService.shutdown();
            return;
        }
        //初始化进度
        progressList = new ArrayList<>();
        for (int i = 0; i < barcodePositionList.size(); i++) {
            progressList.add(false);
        }
        //遍历列表
        for (int i = 0; i < barcodePositionList.size(); i++) {
            final int finalI = i;
            final List<BarcodePosition> finalBarcodePositionList = barcodePositionList;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    final BarcodePosition barcodePosition = finalBarcodePositionList.get(finalI);
                    final String barcode = barcodePosition.getBarcode();
                    final String url;
                    if (currentLocation.equals("library")) {
                        url = "http://10.0.15.12/TSDW/GotoFlash.aspx?szBarCode=" + barcode;
                    } else if (currentLocation.equals("home")) {
                        url = "http://192.168.99.193:5001/libraryapp/bookPosition/goToFlashDemo";
                    } else {
                        addText("url不识别，return");
                        return;
                    }
//                    String wifiSsid = wifiManager.getConnectionInfo().getSSID();
//                    if (wifiSsid.equals("dqlib") || wifiSsid.equals("office"))
//                        url = "http://10.0.15.12/TSDW/GotoFlash.aspx?szBarCode=" + barcode;
//                    else
//                        url = "http://192.168.99.193:5001/libraryapp/bookPosition/goToFlashDemo";
                    //向图书馆内网发请求获取位置信息
                    OkHttpUtil.getCallByCompleteUrl(url).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                            e.printStackTrace();
                            addText("请求图书位置信息发生错误：" + e.getMessage() + "，barcode=" + barcode
                                    + ", url=" + url);
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            //解析html
                            String html = response.body().string();
                            final String position = StringUtils.substringBetween(
                                    html, "var strWZxxxxxx = \"", "\";");
                            String message = StringUtils.substringBetween(
                                    html, "var strMsg = \"", "\";");
                            barcodePosition.setPosition(position);
                            barcodePosition.setMessage(message);
                            addText(barcodePosition.getHoldingIndex() + " "
                                    + barcodePosition.getBarcode() + " "
                                    + barcodePosition.getPosition() + " "
                                    + barcodePosition.getMessage());
                            //更新数据库
                            barcodePosition.setCrawl(true);
                            new UpdateAsyncTask(barcodePositionDao).execute(barcodePosition);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //检查进度
                            synchronized (progressLock) {
                                //一个barCode完成了，更新进度
                                progressList.set(finalI, true);
                                //检查是不是所有的任务都完成了，如果都完成了就继续
                                if (checkProgress()) {
                                    crawlDataFromLibrary(null);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 在图书馆内网爬完了回到家，把手机数据库中的数据，
     * 通过内网提交到我的电脑上，或者直接提交到公网服务也行
     */
    public void submitData(View view) {
        //查询数据库
        List<BarcodePosition> barcodePositionList = null;
        try {
            //需要找到已经爬取了的，但是没提交的数据
            barcodePositionList = new QueryCrawlButNotSubmitAsyncTask(barcodePositionDao).execute(200).get();
            addText("查出数据：" + barcodePositionList.size() + " 条 " + System.currentTimeMillis());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (CollectionUtils.isEmpty(barcodePositionList)) {
            addText("submitData the end!!!!!!!!");
            executorService.shutdown();
            return;
        }
        //稍微显示一下进度
        addText(barcodePositionList.get(0).getHoldingIndex() + "");
        //签名
        for (BarcodePosition barcodePosition : barcodePositionList) {
            long timestamp = System.currentTimeMillis();
            barcodePosition.setTimestamp(timestamp);
            String signKey = "vPUYt6q1AzmmjzXG";
            String sign = DigestUtils.md5Hex(barcodePosition.getBarcode()
                    + barcodePosition.getPosition() + timestamp + signKey);
            barcodePosition.setSign(sign);
        }
        //向服务器提交数据
        String provider = Build.BRAND + "--" + Build.MODEL + "--" + Build.VERSION.RELEASE;
        RequestBody requestBody = new FormBody.Builder()
                .add("provider", provider)
                .add("barcodePositionJson", new Gson().toJson(barcodePositionList))
                .build();
        Request request = new Request.Builder()
                .url(OkHttpUtil.BASE_URL + "/bookPosition/submitPositionMission")
                .post(requestBody)
                .build();
        final List<BarcodePosition> finalBarcodePositionList = barcodePositionList;
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                e.printStackTrace();
                addText("submitData发生错误：" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                String responseString = response.body().string();
                addText("submitData响应：" + responseString);
                if (!responseString.equals("ok"))
                    addText("response string 不是ok，出现错误");
                //更新为已提交
                for (BarcodePosition barcodePosition : finalBarcodePositionList) {
                    barcodePosition.setSubmit(true);
                    new UpdateAsyncTask(barcodePositionDao).execute(barcodePosition);
                }
                //继续
                submitData(null);
            }
        });
    }

    /**
     * 重置数据库中的所有位置数据的，已提交，标记为false
     *
     * @param view
     */
    public void resetSubmitToFalseInDatabase(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<BarcodePosition> barcodePositionList = null;
                while (true) {
                    try {
                        barcodePositionList = new QueryCrawlAndSubmitAsyncTask(barcodePositionDao).execute(200).get();
                        addText("found " + barcodePositionList.size());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (CollectionUtils.isEmpty(barcodePositionList)) {
                        addText("resetSubmitToFalseInDatabase finished!!!!!!!");
                        return;
                    }
                    for (BarcodePosition barcodePosition : barcodePositionList) {
                        barcodePosition.setSubmit(false);
                        new UpdateAsyncTask(barcodePositionDao).execute(barcodePosition);
                        addText(barcodePosition.getHoldingIndex() + " " + barcodePosition.getBarcode());
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
