package com.spidchenko.week2task;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        Intent intent = getIntent();
        WebView webView = findViewById(R.id.webView);

        //Add zoom controls:
        webView.getSettings().setBuiltInZoomControls(true);

        //Resize image to screen width:
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.loadUrl(intent.getStringExtra(MainActivity.EXTRA_URL));
    }
}