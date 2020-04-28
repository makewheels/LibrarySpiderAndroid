package com.eg.libraryspiderandroid.barcodeposition;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {BarcodePosition.class}, version = 1, exportSchema = false)
public abstract class BarcodePositionDatabase extends RoomDatabase {
    public abstract BarcodePositionDao getBarcodePositionDao();
}
