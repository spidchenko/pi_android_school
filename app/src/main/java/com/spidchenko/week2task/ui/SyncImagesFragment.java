package com.spidchenko.week2task.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.SyncImagesAdapter;
import com.spidchenko.week2task.db.models.SyncImage;
import com.spidchenko.week2task.helpers.SwipeHelper;
import com.spidchenko.week2task.viewmodel.SyncImagesViewModel;

public class SyncImagesFragment extends Fragment implements SyncImagesAdapter.OnCardListener {

    OnFragmentInteractionListener mListener;
    private SyncImagesViewModel mViewModel;
    private RecyclerView mRvImages;
    private SyncImagesAdapter mRecyclerAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GalleryFragment.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + getResources().getString(R.string.exception_message));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.sync_images_fragment, container, false);
        mRvImages = rootView.findViewById(R.id.rv_sync_images);

        subscribeToModel();
        initRecyclerView();

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO mb ViewModel Factory here
        mViewModel = new ViewModelProvider(this).get(SyncImagesViewModel.class);
    }


    private void initRecyclerView() {
        mRecyclerAdapter = new SyncImagesAdapter(null, this);
        mRvImages.setAdapter(mRecyclerAdapter);
        mRvImages.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ItemTouchHelper helper = SwipeHelper.getSwipeToDismissTouchHelper(position -> {
            SyncImage image = mRecyclerAdapter.getImageAtPosition(position);
            mViewModel.deleteImage(image);
        });

        helper.attachToRecyclerView(mRvImages);
    }

    private void subscribeToModel() {
    }

    @Override
    public void onCardClick(int position) {
        mListener.onOpenImageAction(mRecyclerAdapter.getImageAtPosition(position));
    }

    interface OnFragmentInteractionListener {
        void onOpenImageAction(SyncImage image);
    }

}