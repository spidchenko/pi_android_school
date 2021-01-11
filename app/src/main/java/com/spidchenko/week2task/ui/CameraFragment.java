package com.spidchenko.week2task.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + getResources().getString(R.string.exception_message));
        }
    }

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
                mListener.showMessage(R.string.image_saved);
                cropPhoto(Uri.fromFile(file), Uri.fromFile(file));
            });
        });

        return rootView;
    }

    private void cropPhoto(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri).start(requireActivity());
    }

    interface OnFragmentInteractionListener {
        void showMessage(@StringRes int resourceId);
    }
}