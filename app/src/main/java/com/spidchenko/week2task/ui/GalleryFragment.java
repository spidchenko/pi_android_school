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
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.GalleryAdapter;
import com.spidchenko.week2task.viewmodel.GalleryActivityViewModel;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {

    private static final String TAG = "GalleryFragment.LOG_TAG";
    private GalleryActivityViewModel mViewModel;

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
                    ((MainActivity)requireActivity()).showSnackBarMessage(R.string.need_photo_permission);
                }
            });

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String param1, String param2) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        mRvImages = rootView.findViewById(R.id.rv_gallery_images);
        FloatingActionButton btnMakePhoto = rootView.findViewById(R.id.btn_make_photo);

        mViewModel = new ViewModelProvider(this).get(GalleryActivityViewModel.class);
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
                mViewModel.deleteFile(requireActivity().getApplicationContext().getContentResolver(),
                        mRecyclerAdapter.getFileAtPosition(position));
            }
        });
    }

    private void enableCamera() {
        mListener.onTakePhotosAction();
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new GalleryAdapter(null);
        mRvImages.setAdapter(mRecyclerAdapter);
        mRvImages.setLayoutManager(new LinearLayoutManager(requireActivity()));
        ItemTouchHelper helper = getSwipeToDismissTouchHelper();
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