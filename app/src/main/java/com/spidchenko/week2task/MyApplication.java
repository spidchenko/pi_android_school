package com.spidchenko.week2task;

import android.app.Application;

import com.facebook.stetho.Stetho;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {

    public ExecutorService executorService = Executors.newFixedThreadPool(4);

    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}