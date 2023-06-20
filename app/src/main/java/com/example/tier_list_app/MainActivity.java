package com.example.tier_list_app;

import androidx.appcompat.app.AppCompatActivity;
import com.example.tier_list_app.database.DBHelper;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    DBHelper helper = new DBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}