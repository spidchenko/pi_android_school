package com.spidchenko.week2task.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.FavouritesListAdapter;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.helpers.SwipeHelper;
import com.spidchenko.week2task.helpers.ViewModelsFactory;
import com.spidchenko.week2task.viewmodel.FavouritesViewModel;

import static com.spidchenko.week2task.adapter.FavouritesListAdapter.ACTION_DELETE;
import static com.spidchenko.week2task.adapter.FavouritesListAdapter.ACTION_OPEN_IMAGE;

public class FavouritesFragment extends Fragment implements FavouritesListAdapter.OnFavouritesListAdapterListener {

    private static final String TAG = "FavFragment.LOG_TAG";

    private OnFragmentInteractionListener mListener;
    private FavouritesViewModel mViewModel;
    private RecyclerView mRvFavouriteImages;
    private FavouritesListAdapter mRecyclerAdapter;

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
        ViewModelsFactory factory = new ViewModelsFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, factory).get(FavouritesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        mRvFavouriteImages = rootView.findViewById(R.id.rv_favourite_images);
        initRecyclerView();
        subscribeToModel();
        return rootView;
    }

    @Override
    public void onItemClick(int action, int position) {
        Favourite favourite = mRecyclerAdapter.getFavouriteAtPosition(position);
        switch (action) {
            case ACTION_DELETE: {
                mViewModel.deleteFavourite(favourite);
                break;
            }
            case ACTION_OPEN_IMAGE: {
                mListener.onOpenFavouriteAction(favourite);
                break;
            }
        }
    }

    private void subscribeToModel() {
        Log.d(TAG, "subscribeToModel: ");
        mViewModel.getFavouritesWithCategories().observe(getViewLifecycleOwner(),
                favourites -> {
                    mRecyclerAdapter.setFavourites(favourites);
                    Log.d(TAG, "subscribeToModel: favourites->" + favourites);
                });

        mViewModel.getSnackBarMessage().observe(this,
                message -> Snackbar.make(requireView(), message,
                        BaseTransientBottomBar.LENGTH_LONG).show());
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new FavouritesListAdapter(null, this);
        mRvFavouriteImages.setAdapter(mRecyclerAdapter);
        mRvFavouriteImages.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ItemTouchHelper helper = SwipeHelper.getSwipeToDismissTouchHelper(position -> {
            Favourite favourite = mRecyclerAdapter.getFavouriteAtPosition(position);
            mViewModel.deleteFavourite(favourite);
        });

        helper.attachToRecyclerView(mRvFavouriteImages);
    }

    interface OnFragmentInteractionListener {
        void onOpenFavouriteAction(Favourite favourite);
    }
}