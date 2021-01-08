package com.spidchenko.week2task.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spidchenko.week2task.CameraHelper;
import com.spidchenko.week2task.FileRepository;
import com.spidchenko.week2task.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment.LOG_TAG";


    private File photosDirectory;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CameraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CameraFragment.
     */
    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        FloatingActionButton btnTakeShot = rootView.findViewById(R.id.btn_take_shot);

        PreviewView previewView = rootView.findViewById(R.id.previewView);
        FileRepository fileRepository = new FileRepository(requireContext().getApplicationContext());
        photosDirectory = fileRepository.getPhotosDirectory();
        CameraHelper cameraHelper = new CameraHelper(requireContext(), getViewLifecycleOwner(), previewView);

        btnTakeShot.setOnClickListener(view -> {
            SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            File file = new File(photosDirectory, mDateFormat.format(new Date()) + ".jpg");

            cameraHelper.takePicture(requireContext(), file, () -> {
                ((MainActivity) requireActivity()).showSnackBarMessage(R.string.image_saved);
                cropPhoto(Uri.fromFile(file), Uri.fromFile(file));
            });

        });

        return rootView;
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
        UCrop.of(sourceUri, destinationUri).start(requireActivity());
    }


}