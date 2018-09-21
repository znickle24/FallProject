package com.zandernickle.fallproject_pt1;

import android.support.v7.app.AppCompatActivity;

public class CustomAppCompatActivity extends AppCompatActivity {

    public CustomAppCompatActivity() {
        super();
    }

    protected boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }
}
