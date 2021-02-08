package com.spidchenko.week2task.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.helpers.CameraHelper;
import com.spidchenko.week2task.repositories.FileRepository;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraFragment extends Fragment {
    private File photosDirectory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        FloatingActionButton btnTakeShot = rootView.findViewById(R.id.btn_take_shot);

        PreviewView previewView = rootView.findViewById(R.id.previewView);

        FileRepository fileRepository = FileRepository.getInstance(requireActivity().getApplication());
        photosDirectory = fileRepository.getPhotosDirectory();

        CameraHelper cameraHelper = new CameraHelper(requireContext(), getViewLifecycleOwner(), previewView);

        btnTakeShot.setOnClickListener(view -> {
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            File file = new File(photosDirectory, mDateFormat.format(new Date()) + ".jpg");
            cameraHelper.takePicture(requireContext(), file, () -> {
                Snackbar.make(requireView(), R.string.image_saved,
                        BaseTransientBottomBar.LENGTH_LONG).show();
                cropPhoto(Uri.fromFile(file), Uri.fromFile(file));
            });
        });

        return rootView;
    }

    private void cropPhoto(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri).start(requireActivity());
    }
}