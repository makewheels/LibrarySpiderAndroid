package com.eg.libraryspiderandroid.barcodeposition;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BarcodePositionDao {
    @Insert
    void insert(BarcodePosition... barcodePositions);

    @Update
    int update(BarcodePosition... barcodePositions);

    @Delete
    int delete(BarcodePosition... barcodePositions);

    @Query("select * from BarcodePosition where isUpload=0 limit 10")
    List<BarcodePosition> find();

}
