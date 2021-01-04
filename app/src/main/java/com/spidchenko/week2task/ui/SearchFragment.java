package com.spidchenko.week2task.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.ImageListAdapter;
import com.spidchenko.week2task.network.models.Image;
import com.spidchenko.week2task.viewmodel.MainActivityViewModel;

import static com.spidchenko.week2task.ui.MapsActivity.EXTRA_LATITUDE;
import static com.spidchenko.week2task.ui.MapsActivity.EXTRA_LONGITUDE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements ImageListAdapter.OnCardListener {

    private static final String TAG = "SearchFragment.LOG_TAG";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mCurrentSearchString;
    private ImageListAdapter mRecyclerAdapter;
    private MainActivityViewModel mViewModel;
    ActivityResultLauncher<Intent> mGetCoordinates =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if ((result.getResultCode() == Activity.RESULT_OK) && (result.getData() != null)) {
                    String lat = data.getStringExtra(EXTRA_LATITUDE);
                    String lon = data.getStringExtra(EXTRA_LONGITUDE);
                    Log.d(TAG, "onReceiveGeoIntent: lat= " + lat + ". lon = " + lon);
                    mViewModel.searchImagesByCoordinates(lat, lon);
//                    hideKeyboard(this);
                }
            });
    //UI
    private EditText mEtSearchQuery;
    private Button mBtnSearch;
    private RecyclerView mRvImages;
    private ProgressBar mPbLoading;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

//        mRvImages = findViewById(R.id.rv_images);
//        mEtSearchQuery = findViewById(R.id.et_search_query);
//        mBtnSearch = findViewById(R.id.btn_search);
//        mPbLoading = findViewById(R.id.pbLoading);


        initRecyclerView();

        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        subscribeToModel();

        Log.d(TAG, "onCreate: Created");

        mEtSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                actionSearch(null);
                return true;
            }
            return false;
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    public void actionSearch(View view) {
//        hideKeyboard(this);
        String searchString = mEtSearchQuery.getText().toString().trim();
        if (searchString.isEmpty()) {
            showSnackBarMessage(R.string.error_empty_search);
        } else {
            mViewModel.searchImages(searchString);
        }
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new ImageListAdapter(null, this);
        mRvImages.setAdapter(mRecyclerAdapter);
//        mRvImages.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper helper = getSwipeToDismissTouchHelper();
        helper.attachToRecyclerView(mRvImages);
    }

    @Override
    public void onCardClick(int position) {
        Log.d(TAG, "ViewHolder clicked! Position = " + position);

        Image image = mRecyclerAdapter.getImageAtPosition(position);

//        Intent intent = new Intent(this, ImageViewerActivity.class);
//        intent.putExtra(EXTRA_URL, image.getUrl(Image.PIC_SIZE_MEDIUM));
//        intent.putExtra(EXTRA_SEARCH_STRING, mCurrentSearchString);
//        this.startActivity(intent);
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

    private void showSnackBarMessage(@StringRes int resourceId) {
//        Snackbar.make(findViewById(android.R.id.content),
//                resourceId,
//                BaseTransientBottomBar.LENGTH_LONG).show();
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

        mViewModel.getSnackBarMessage().observe(this, this::showSnackBarMessage);
    }


}