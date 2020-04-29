package com.eg.libraryspiderandroid.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.eg.libraryspiderandroid.R;
import com.eg.libraryspiderandroid.barcodeposition.BarcodePosition;
import com.eg.libraryspiderandroid.barcodeposition.BarcodePositionDao;
import com.eg.libraryspiderandroid.util.AppDatabase;

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
            barcodePositionDao.insert(barcodePositions);
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

    /**
     * 请求barCode数据，保存到手机数据库
     */
    public void requestData(View view) {
        BarcodePosition barcodePosition = new BarcodePosition();
        barcodePosition.setBarcode("044444440" + System.currentTimeMillis());
        barcodePosition.setMessage("test for messageesss");
        barcodePosition.setMongoId("505050505050011144");
        new InsertAsyncTask(barcodePositionDao).execute(barcodePosition);
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
