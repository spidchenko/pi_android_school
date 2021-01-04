package com.spidchenko.week2task.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.GalleryAdapter;
import com.spidchenko.week2task.viewmodel.GalleryActivityViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity.LOG_TAG";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);



    }



//    public void actionTakePhoto(View view) {
//        if (ContextCompat.checkSelfPermission(getApplicationContext(),
//                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            enableCamera();
//        } else {
//            Log.d(TAG, "actionTakePhoto: Permission not granted! Trying to ask for...");
//            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
//        }
//    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initAppBar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

}