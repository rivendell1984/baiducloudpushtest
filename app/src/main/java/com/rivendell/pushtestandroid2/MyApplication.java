package com.rivendell.pushtestandroid2;

import android.app.Application;

import com.baidu.techain.ac.TH;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TH.setAgreePolicy(getApplicationContext(), true);
        TH.init(getApplicationContext(), "700000765", "b9db79defea6e84c2b7ea7a98ae8f144", 100019);

    }
}
