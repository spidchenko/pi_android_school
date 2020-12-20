package com.spidchenko.week2task.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.GalleryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.os.Environment.DIRECTORY_PICTURES;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity.LOG_TAG";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final int REQUEST_IMAGE_CAPTURE = 30;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private RecyclerView mRvImages;
    private GalleryAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mRvImages = findViewById(R.id.rv_gallery_images);

        initRecyclerView();
        insertData();

    }

    //Save parent activity state on up home navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "Options item selected");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            Log.d(TAG, "Pressed Back UP button");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
//                mViewModel.deleteFileAtPosition(position);
                mRecyclerAdapter.dismiss(position);
                deletePhotoFile(mRecyclerAdapter.getFileAtPosition(position));
            }
        });
    }


    public void actionTakePhoto(View view) {
        if (hasCameraPermission()) {
            enableCamera();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
        );
    }

    //TODO need to click second time, check this
    private void enableCamera() {
        Log.d(TAG, "enableCamera: Permissions granted");
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private void getPublicDirectory() {
        File pictureFolder = Environment.getExternalStoragePublicDirectory(
                DIRECTORY_PICTURES);
        if (!pictureFolder.exists()) {
            if (!pictureFolder.mkdir()) {
                Log.e(TAG, "Failed to create directory: " + pictureFolder);
            }
        }
    }

    //TODO FileRepository
    ArrayList<File> getPhotoFiles() {
        File dirWithOurPhotos = getAppSpecificAlbumStorageDir(this);
        if (dirWithOurPhotos != null) {
            File[] files = dirWithOurPhotos.listFiles();
            if (files != null) {
                for (File file : files) {
                    Log.d(TAG, "onCreate: We have photos! - " + dirWithOurPhotos + file.getName());
                }
                return new ArrayList<>(Arrays.asList(files));
            }
        }
        return null;
    }

    //TODO FileRepository
    private void deletePhotoFile(File file) {
        file.delete();
    }

    private boolean hasCameraPermission() {
        boolean result = true;
        for (String perm : REQUIRED_PERMISSIONS) {
            result = result && (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED);
        }
        return result;
    }

    @Nullable
    File getAppSpecificAlbumStorageDir(Context context) {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        File file = new File(context.getExternalFilesDir(
                DIRECTORY_PICTURES), getString(R.string.app_name));
        if (file == null || !file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new GalleryAdapter(null);
        mRvImages.setAdapter(mRecyclerAdapter);

        int orientation = this.getResources().getConfiguration().orientation;
        mRvImages.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper helper = getSwipeToDismissTouchHelper();
        helper.attachToRecyclerView(mRvImages);
    }

    private void insertData() {
        mRecyclerAdapter.setImages(getPhotoFiles());
        mRecyclerAdapter.notifyDataSetChanged();
    }

}