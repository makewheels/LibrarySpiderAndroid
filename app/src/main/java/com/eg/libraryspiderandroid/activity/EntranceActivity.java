package com.eg.libraryspiderandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.eg.libraryspiderandroid.R;

public class EntranceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
    }

    public void toNetworkActivity(View view) {
        startActivity(new Intent(this, NetworkActivity.class));
    }

    public void toDatabaseActivity(View view) {
        startActivity(new Intent(this, DatabaseActivity.class));
    }
}
