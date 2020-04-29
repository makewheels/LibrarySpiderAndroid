package com.eg.libraryspiderandroid.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.eg.libraryspiderandroid.R;
import com.eg.libraryspiderandroid.barcodeposition.BarcodePosition;
import com.eg.libraryspiderandroid.barcodeposition.BarcodePositionDao;
import com.eg.libraryspiderandroid.util.AppDatabase;
import com.eg.libraryspiderandroid.util.OkHttpUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DatabaseActivity extends AppCompatActivity {
    private BarcodePositionDao barcodePositionDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        init();
    }

    private void init() {
        AppDatabase appDatabase = AppDatabase.getAppDatabase(this);
        barcodePositionDao = appDatabase.getBarcodePositionDao();
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
                List<BarcodePosition> barcodePositionList = JSON.parseArray(response.body().string(),
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

    /**
     * 请求barcode数据，保存到手机数据库
     */
    public void startRequestMission(View view) {
        requestData();
    }

    /**
     * 在图书馆内网爬位置数据，保存到手机数据库
     */
    public void crawlDataFromLibrary(View view) {

    }

    /**
     * 在图书馆内网爬完了回到家，把手机数据库中的数据，
     * 通过内网提交到我的电脑上，或者直接提交到公网服务也行
     */
    public void submitData(View view) {

    }
}
