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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.GalleryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static android.os.Environment.DIRECTORY_PICTURES;

// TODO: 12/22/20 don't forget to fix your todos
public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity.LOG_TAG";

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
//                mViewModel.deleteFileAtPosition(position);
                mRecyclerAdapter.dismiss(position);
                deleteFile(mRecyclerAdapter.getFileAtPosition(position));
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


    private void enableCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    //TODO FileRepository
    private File getPublicDirectory() {
        File pictureFolder = new File(Environment.getExternalStoragePublicDirectory(
                DIRECTORY_PICTURES), "Simple flickr client");
        if (!pictureFolder.exists()) {
            if (!pictureFolder.mkdir()) {
                Log.e(TAG, "Failed to create public directory: " + pictureFolder);
            }
        }
        return pictureFolder;
    }

    //TODO FileRepository
    ArrayList<File> getPublicImageFiles() {
        File dirWithOurPhotos = getPublicDirectory();
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
    ArrayList<File> getCameraPhotoFiles() {
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
    private void deleteFile(File file) {
        file.delete();
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
        mRvImages.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper helper = getSwipeToDismissTouchHelper();
        helper.attachToRecyclerView(mRvImages);
    }

    private void insertData() {
        ArrayList<File> filesToShow = getCameraPhotoFiles();
        filesToShow.addAll(getPublicImageFiles());

        mRecyclerAdapter.setImages(filesToShow);
        mRecyclerAdapter.notifyDataSetChanged();
    }

}