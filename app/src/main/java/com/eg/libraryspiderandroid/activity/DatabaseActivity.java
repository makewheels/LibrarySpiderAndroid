package com.eg.libraryspiderandroid.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.eg.libraryspiderandroid.R;
import com.eg.libraryspiderandroid.barcodeposition.BarcodePositionDao;
import com.eg.libraryspiderandroid.barcodeposition.BarcodePositionDatabase;

public class DatabaseActivity extends AppCompatActivity {
    private BarcodePositionDatabase barcodePositionDatabase;
    private BarcodePositionDao barcodePositionDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        barcodePositionDatabase = Room.databaseBuilder(this, BarcodePositionDatabase.class,
                "BarcodePositionDatabase").build();
        barcodePositionDao = barcodePositionDatabase.getBarcodePositionDao();
    }
}
