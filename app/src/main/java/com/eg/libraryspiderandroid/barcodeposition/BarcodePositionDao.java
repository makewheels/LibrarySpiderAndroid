package com.eg.libraryspiderandroid.barcodeposition;

import androidx.room.Dao;
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

    @Query("select * from BarcodePosition where barcode=:barcode")
    BarcodePosition queryByBarcode(String barcode);

    @Query("select * from BarcodePosition where isCrawl=0 limit :amount")
    List<BarcodePosition> queryNotCrawl(int amount);

    @Query("select * from BarcodePosition where isCrawl=1 and isSubmit=0 limit :amount")
    List<BarcodePosition> queryCrawlButNotSubmit(int amount);
}
