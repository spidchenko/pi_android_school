package com.spidchenko.week2task.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.spidchenko.week2task.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

// TODO: 12/22/20 it is common practice to move work with camera into a separate class (e.g. CameraHelper)
public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity.LOG_TAG";
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    ImageCapture imageCapture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindImageAnalysis(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));

    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        // TODO: 12/22/20 why do you need this imageAnalysis?
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), ImageProxy::close);

        Preview preview = new Preview.Builder().build();

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(previewView.getDisplay().getRotation())
                .build();


        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture,
                imageAnalysis, preview);
    }

    public void actionTakeShot(View view) {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        File file = new File(getAppSpecificAlbumStorageDir(this), mDateFormat.format(new Date()) + ".jpg");

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                new Handler(getMainLooper()).post(() ->
                        Toast.makeText(CameraActivity.this,
                                "Image Saved successfully",
                                Toast.LENGTH_SHORT).show());
                cropPhoto(Uri.fromFile(file), Uri.fromFile(file));
            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            Log.d(TAG, "onActivityResult: RESULT_OK " + resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Log.d(TAG, "onActivityResult: RESULT_ERROR " + cropError);
        }
    }

    private void cropPhoto(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri).start(this);
    }


    //TODO FileRepository
    @Nullable
    private File getAppSpecificAlbumStorageDir(Context context) {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        if (file == null || !file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        Log.d(TAG, "getAppSpecificAlbumStorageDir: " + file);
        return file;
    }

}