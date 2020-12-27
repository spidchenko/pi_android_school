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
    private GalleryActivityViewModel mViewModel;

    // TODO: 12/22/20 Suggestion: Look into ButterKnife library. It is easy to use & integrate and it will simplify view bindings.
    private RecyclerView mRvImages;
    private GalleryAdapter mRecyclerAdapter;

    ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d(TAG, "Permission callback! = " + isGranted);
                if (isGranted) {
                    enableCamera();
                } else {
                    Toast.makeText(this, getString(R.string.need_photo_permission), Toast.LENGTH_LONG).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mRvImages = findViewById(R.id.rv_gallery_images);

        mViewModel = new ViewModelProvider(this).get(GalleryActivityViewModel.class);

        initAppBar();
        initRecyclerView();

        mViewModel.getImageFiles().observe(this, files -> {
            Log.d(TAG, "Observed LiveData: " + files);
            mRecyclerAdapter.setImages((ArrayList<File>) files);
            mRecyclerAdapter.notifyDataSetChanged();
        });

    }

    // TODO: 12/22/20 can be moved to a separate class (to keep things clean)
    ItemTouchHelper getSwipeToDismissTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Log.d(TAG, "ViewHolder Swiped! Position= " + position);
                mViewModel.deleteFile(getApplicationContext().getContentResolver(),
                        mRecyclerAdapter.getFileAtPosition(position));
            }
        });
    }

    public void actionTakePhoto(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            enableCamera();
        } else {
            Log.d(TAG, "actionTakePhoto: Permission not granted! Trying to ask for...");
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

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

    private void enableCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new GalleryAdapter(null);
        mRvImages.setAdapter(mRecyclerAdapter);
        mRvImages.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper helper = getSwipeToDismissTouchHelper();
        helper.attachToRecyclerView(mRvImages);
    }
}