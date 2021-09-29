package com.spidchenko.week2task.helpers

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.io.File
import java.util.concurrent.ExecutionException

class CameraHelper(
    context: Context?,
    owner: LifecycleOwner,
    private val mPreviewView: PreviewView
) {
    private val mCameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(context!!)
    private var mImageCapture: ImageCapture? = null
    fun takePicture(context: Context?, file: File?, listener: CameraListener) {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            file!!
        ).build()
        mImageCapture!!.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    listener.onPhotoTaken()
                }

                override fun onError(error: ImageCaptureException) {
                    error.printStackTrace()
                }
            })
    }

    private fun bindImageAnalysis(cameraProvider: ProcessCameraProvider, owner: LifecycleOwner) {
        val preview = Preview.Builder().build()
        mImageCapture = ImageCapture.Builder()
            .setTargetRotation(mPreviewView.display.rotation)
            .build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        preview.setSurfaceProvider(mPreviewView.surfaceProvider)
        cameraProvider.bindToLifecycle(owner, cameraSelector, mImageCapture, preview)
    }

    interface CameraListener {
        fun onPhotoTaken()
    }

    init {
        mCameraProviderFuture.addListener({
            try {
                val cameraProvider = mCameraProviderFuture.get()
                bindImageAnalysis(cameraProvider, owner)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }
}