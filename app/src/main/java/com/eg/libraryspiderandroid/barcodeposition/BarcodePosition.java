package com.eg.libraryspiderandroid.barcodeposition;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class BarcodePosition {
    @PrimaryKey
    @NonNull
    private String holdingMongoId;

    private String barcode;
    private long holdingIndex;

    private String position;
    private String message;
    @Ignore
    private long timestamp;
    @Ignore
    private String sign;
    private boolean isSubmit;
    private boolean isCrawl;

    @NonNull
    public String getHoldingMongoId() {
        return holdingMongoId;
    }

    public void setHoldingMongoId(@NonNull String holdingMongoId) {
        this.holdingMongoId = holdingMongoId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public long getHoldingIndex() {
        return holdingIndex;
    }

    public void setHoldingIndex(long holdingIndex) {
        this.holdingIndex = holdingIndex;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public boolean isSubmit() {
        return isSubmit;
    }

    public void setSubmit(boolean submit) {
        isSubmit = submit;
    }

    public boolean isCrawl() {
        return isCrawl;
    }

    public void setCrawl(boolean crawl) {
        isCrawl = crawl;
    }
}
