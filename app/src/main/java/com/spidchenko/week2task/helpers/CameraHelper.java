package com.spidchenko.week2task.helpers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class CameraHelper {

    private final ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private final PreviewView mPreviewView;
    private ImageCapture imageCapture;

    public CameraHelper(Context context, LifecycleOwner owner, PreviewView previewView) {
        mPreviewView = previewView;
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindImageAnalysis(cameraProvider, owner);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    public void takePicture(Context context, File file, CameraListener listener) {

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                listener.onPhotoTaken();
            }

            @Override
            public void onError(@NonNull ImageCaptureException error) {
                error.printStackTrace();
            }
        });

    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider, LifecycleOwner owner) {
        Preview preview = new Preview.Builder().build();
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(mPreviewView.getDisplay().getRotation())
                .build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(owner, cameraSelector, imageCapture, preview);
    }

    public interface CameraListener {
        void onPhotoTaken();
    }

}