package com.eg.libraryspiderandroid.util;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.eg.libraryspiderandroid.barcodeposition.BarcodePosition;
import com.eg.libraryspiderandroid.barcodeposition.BarcodePositionDao;

@Database(entities = {BarcodePosition.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;

    public static synchronized AppDatabase getAppDatabase(Context context) {
        if (appDatabase == null)
            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "AppDatabase").build();
        return appDatabase;
    }

    public abstract BarcodePositionDao getBarcodePositionDao();
}
