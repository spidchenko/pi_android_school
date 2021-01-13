package com.spidchenko.week2task.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.ImageListAdapter;
import com.spidchenko.week2task.helpers.SwipeHelper;
import com.spidchenko.week2task.helpers.ViewModelsFactory;
import com.spidchenko.week2task.network.models.Image;
import com.spidchenko.week2task.viewmodel.SearchViewModel;

import static com.spidchenko.week2task.ui.MapsFragment.EXTRA_LATITUDE;
import static com.spidchenko.week2task.ui.MapsFragment.EXTRA_LONGITUDE;

public class SearchFragment extends Fragment implements ImageListAdapter.OnCardListener {

    private static final String TAG = "SearchFragment.LOG_TAG";


    private String mCurrentSearchString;
    private ImageListAdapter mRecyclerAdapter;
    private SearchViewModel mViewModel;

    OnFragmentInteractionListener mListener;

    //UI
    private EditText mEtSearchQuery;
    private Button mBtnSearch;
    private RecyclerView mRvImages;
    private ProgressBar mPbLoading;

    private String mSearchLatitude;
    private String mSearchLongitude;

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
        if (getArguments() != null) {
            mSearchLatitude = getArguments().getString(EXTRA_LATITUDE);
            mSearchLongitude = getArguments().getString(EXTRA_LONGITUDE);
            Log.d(TAG, "onCreate: lat:" + mSearchLatitude + ". lon:" + mSearchLongitude);
        }

        ViewModelsFactory factory = new ViewModelsFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);

        // Perform Flickr search by coordinates
        if ((getArguments() != null) && (savedInstanceState == null)) {
            mViewModel.searchImagesByCoordinates(mSearchLatitude, mSearchLongitude);
            mListener.hideKeyboard();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        mRvImages = rootView.findViewById(R.id.rv_images);
        mEtSearchQuery = rootView.findViewById(R.id.et_search_query);
        mBtnSearch = rootView.findViewById(R.id.btn_search);
        mPbLoading = rootView.findViewById(R.id.pbLoading);

        initRecyclerView();
        subscribeToModel();

        // Perform Flickr search on soft keyboard event
        mEtSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mBtnSearch.callOnClick();
                return true;
            }
            return false;
        });

        // Perform Flickr search
        mBtnSearch.setOnClickListener(view -> {
            mListener.hideKeyboard();
            String searchString = mEtSearchQuery.getText().toString().trim();
            if (searchString.isEmpty()) {
                Snackbar.make(requireView(), R.string.error_empty_search,
                        BaseTransientBottomBar.LENGTH_LONG).show();
            } else {
                mViewModel.searchImages(searchString);
            }
        });

        return rootView;
    }


    private void initRecyclerView() {
        mRecyclerAdapter = new ImageListAdapter(null, this);
        mRvImages.setAdapter(mRecyclerAdapter);
        mRvImages.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ItemTouchHelper helper = SwipeHelper.getSwipeToDismissTouchHelper(position ->
                mViewModel.deleteImageAtPosition(position));

        helper.attachToRecyclerView(mRvImages);
    }

    @Override
    public void onCardClick(int position) {
        Log.d(TAG, "ViewHolder clicked! Position = " + position);
        Image image = mRecyclerAdapter.getImageAtPosition(position);
        mListener.onImageClick(image, mCurrentSearchString);
    }

    private void subscribeToModel() {
        mViewModel.getSearchString().observe(getViewLifecycleOwner(), lastSearch -> {
            mCurrentSearchString = lastSearch;
            mEtSearchQuery.setText(lastSearch);
            mRecyclerAdapter.setSearchString(lastSearch);
            mRecyclerAdapter.notifyDataSetChanged();
        });

        mViewModel.getAllImages().observe(getViewLifecycleOwner(), images -> {
            mRecyclerAdapter.setImages(images);
            mRecyclerAdapter.notifyDataSetChanged();
        });

        mViewModel.getLoadingState().observe(getViewLifecycleOwner(), loadingState -> {
            Log.d(TAG, "onCreate: isLoading: " + loadingState);
            mPbLoading.setVisibility(loadingState ? View.VISIBLE : View.GONE);
            mBtnSearch.setClickable(!loadingState);
        });

        mViewModel.getSnackBarMessage().observe(this,
                message -> Snackbar.make(requireView(), message,
                        BaseTransientBottomBar.LENGTH_LONG).show());
    }

    interface OnFragmentInteractionListener {
        void onImageClick(Image image, String searchString);

        void hideKeyboard();
    }
}