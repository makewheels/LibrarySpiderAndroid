package com.eg.libraryspiderandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.eg.libraryspiderandroid.R;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import es.dmoral.toasty.Toasty;

public class EntranceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        copyDbFile();

    }

    private void copyDbFile() {
        //先看文件是不是已经存在了，如果已经存在，就返回
        File databaseFile = getDatabasePath("AppDatabase");
        if (databaseFile.exists()) {
            Toasty.info(this, "AppDatabase already exists").show();
            return;
        }
        try {
            InputStream inputStream = getAssets().open("AppDatabase");
            OutputStream outputStream = new FileOutputStream(databaseFile);
            IOUtils.copy(inputStream, outputStream);
            Toasty.success(this, "copy AppDatabase finish").show();
        } catch (IOException e) {
            e.printStackTrace();
            Toasty.error(this, "copy AppDatabase error " + e.getMessage()).show();
        }
    }

    public void toNetworkActivity(View view) {
        startActivity(new Intent(this, NetworkActivity.class));
    }

    public void toDatabaseActivity(View view) {
        startActivity(new Intent(this, DatabaseActivity.class));
    }
}
