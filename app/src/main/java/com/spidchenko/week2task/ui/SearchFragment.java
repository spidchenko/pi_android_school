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

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.ImageListAdapter;
import com.spidchenko.week2task.network.models.Image;
import com.spidchenko.week2task.viewmodel.MainActivityViewModel;

import static com.spidchenko.week2task.ui.MapsFragment.EXTRA_LATITUDE;
import static com.spidchenko.week2task.ui.MapsFragment.EXTRA_LONGITUDE;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements ImageListAdapter.OnCardListener {

    private static final String TAG = "SearchFragment.LOG_TAG";


    private String mCurrentSearchString;
    private ImageListAdapter mRecyclerAdapter;
    private MainActivityViewModel mViewModel;

    OnFragmentInteractionListener mListener;

    //    ActivityResultLauncher<Intent> mGetCoordinates =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//                Intent data = result.getData();
//                if ((result.getResultCode() == Activity.RESULT_OK) && (result.getData() != null)) {
//                    String lat = data.getStringExtra(EXTRA_LATITUDE);
//                    String lon = data.getStringExtra(EXTRA_LONGITUDE);
//                    Log.d(TAG, "onReceiveGeoIntent: lat= " + lat + ". lon = " + lon);
//                    mViewModel.searchImagesByCoordinates(lat, lon);
//                    ((MainActivity) requireActivity()).hideKeyboard();
//                }
//            });
    //UI
    private EditText mEtSearchQuery;
    private Button mBtnSearch;
    private RecyclerView mRvImages;
    private ProgressBar mPbLoading;

    private String mSearchLatitude;
    private String mSearchLongitude;

    public SearchFragment() {
        // Required empty public constructor
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
            mSearchLatitude = getArguments().getString(EXTRA_LATITUDE);
            mSearchLongitude = getArguments().getString(EXTRA_LONGITUDE);
            Log.d(TAG, "onCreate: lat:" + mSearchLatitude + ". lon:" + mSearchLongitude);
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

        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        subscribeToModel();

        Log.d(TAG, "onCreate: Created");

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
            ((MainActivity) requireActivity()).hideKeyboard();
            String searchString = mEtSearchQuery.getText().toString().trim();
            if (searchString.isEmpty()) {
                ((MainActivity) requireActivity()).showSnackBarMessage(R.string.error_empty_search);
            } else {
                mViewModel.searchImages(searchString);
            }
        });

        // Perform Flickr search by coordinates
        if (getArguments() != null) {
            mViewModel.searchImagesByCoordinates(mSearchLatitude, mSearchLongitude);
            ((MainActivity) requireActivity()).hideKeyboard();
        }

        return rootView;
    }


    private void initRecyclerView() {
        mRecyclerAdapter = new ImageListAdapter(null, this);
        mRvImages.setAdapter(mRecyclerAdapter);
        mRvImages.setLayoutManager(new LinearLayoutManager(requireActivity()));
        ItemTouchHelper helper = getSwipeToDismissTouchHelper();
        helper.attachToRecyclerView(mRvImages);
    }

    @Override
    public void onCardClick(int position) {
        Log.d(TAG, "ViewHolder clicked! Position = " + position);
        Image image = mRecyclerAdapter.getImageAtPosition(position);
        mListener.onImageClick(image, mCurrentSearchString);
    }

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
                mViewModel.deleteImageAtPosition(position);
            }
        });
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

//        mViewModel.getIsNightMode().observe(getViewLifecycleOwner(), isNightMode -> invalidateOptionsMenu());

        mViewModel.getSnackBarMessage().observe(this,
                message -> ((MainActivity) requireActivity()).showSnackBarMessage(message));
    }

    interface OnFragmentInteractionListener {
        void onImageClick(Image image, String searchString);
    }

}