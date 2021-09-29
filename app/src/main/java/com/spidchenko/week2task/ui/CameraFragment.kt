package com.spidchenko.week2task.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.spidchenko.week2task.R
import com.spidchenko.week2task.helpers.CameraHelper
import com.spidchenko.week2task.helpers.CameraHelper.CameraListener
import com.spidchenko.week2task.repositories.FileRepository.Companion.getInstance
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_camera, container, false)
        val btnTakeShot: FloatingActionButton = rootView.findViewById(R.id.btn_take_shot)
        val previewView: PreviewView = rootView.findViewById(R.id.previewView)
        val fileRepository = getInstance(requireActivity().application)
        val photosDirectory = fileRepository?.photosDirectory
        val cameraHelper = CameraHelper(requireContext(), viewLifecycleOwner, previewView)
        btnTakeShot.setOnClickListener {
            val mDateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            val file = File(photosDirectory, mDateFormat.format(Date()) + ".jpg")
            cameraHelper.takePicture(requireContext(), file, object : CameraListener {
                override fun onPhotoTaken() {
                    Snackbar.make(
                        requireView(), R.string.image_saved,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                    cropPhoto(Uri.fromFile(file), Uri.fromFile(file))
                }
            })
        }
        return rootView
    }

    private fun cropPhoto(sourceUri: Uri, destinationUri: Uri) {
        UCrop.of(sourceUri, destinationUri).start(requireActivity())
    }
}