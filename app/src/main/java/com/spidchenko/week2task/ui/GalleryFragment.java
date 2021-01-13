package com.spidchenko.week2task.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.GalleryAdapter;
import com.spidchenko.week2task.helpers.SwipeHelper;
import com.spidchenko.week2task.viewmodel.GalleryViewModel;

import java.io.File;
import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment.LOG_TAG";
    private GalleryViewModel mViewModel;
    OnFragmentInteractionListener mListener;
    // TODO: 12/22/20 Suggestion: Look into ButterKnife library. It is easy to use & integrate and it will simplify view bindings.
    private RecyclerView mRvImages;
    private GalleryAdapter mRecyclerAdapter;

    ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d(TAG, "Permission callback! = " + isGranted);
                if (isGranted) {
                    enableCamera();
                } else {
                    Snackbar.make(requireView(), R.string.need_photo_permission,
                            BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GalleryViewModel.Factory factory =
                new GalleryViewModel.Factory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, factory).get(GalleryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        mRvImages = rootView.findViewById(R.id.rv_gallery_images);
        FloatingActionButton btnMakePhoto = rootView.findViewById(R.id.btn_make_photo);
        subscribeToModel();
        initRecyclerView();
        // Open fragment to make new photo
        btnMakePhoto.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                enableCamera();
            } else {
                Log.d(TAG, "actionTakePhoto: Permission not granted! Trying to ask for...");
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        return rootView;
    }

    private void enableCamera() {
        mListener.onTakePhotosAction();
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new GalleryAdapter(null);
        mRvImages.setAdapter(mRecyclerAdapter);
        mRvImages.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ItemTouchHelper helper = SwipeHelper.getSwipeToDismissTouchHelper(position ->
                mViewModel.deleteFile(requireActivity().getApplicationContext().getContentResolver(),
                        mRecyclerAdapter.getFileAtPosition(position)));

        helper.attachToRecyclerView(mRvImages);
    }

    private void subscribeToModel() {
        mViewModel.getImageFiles().observe(getViewLifecycleOwner(), files -> {
            Log.d(TAG, "Observed LiveData: " + files);
            mRecyclerAdapter.setImages((ArrayList<File>) files);
            mRecyclerAdapter.notifyDataSetChanged();
        });
    }

    interface OnFragmentInteractionListener {
        void onTakePhotosAction();
    }
}