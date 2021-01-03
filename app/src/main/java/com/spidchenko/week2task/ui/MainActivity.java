package com.spidchenko.week2task.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.BatteryLevelReceiver;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.ImageListAdapter;
import com.spidchenko.week2task.network.models.Image;
import com.spidchenko.week2task.viewmodel.MainActivityViewModel;

import static com.spidchenko.week2task.ui.MapsActivity.EXTRA_LATITUDE;
import static com.spidchenko.week2task.ui.MapsActivity.EXTRA_LONGITUDE;

public class MainActivity extends AppCompatActivity implements ImageListAdapter.OnCardListener {
    private static final String TAG = "MainActivity.LOG_TAG";
    public static final String EXTRA_URL = "com.spidchenko.week2task.extras.EXTRA_URL";
    public static final String EXTRA_SEARCH_STRING = "com.spidchenko.week2task.extras.EXTRA_SEARCH_STRING";

    private String mCurrentSearchString;
    private ImageListAdapter mRecyclerAdapter;
    private MainActivityViewModel mViewModel;
    private final BroadcastReceiver mBatteryLevelReceiver = new BatteryLevelReceiver();

    //UI
    private EditText mEtSearchQuery;
    private Button mBtnSearch;
    private RecyclerView mRvImages;
    private ProgressBar mPbLoading;

    ActivityResultLauncher<Intent> mGetCoordinates =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if ((result.getResultCode() == Activity.RESULT_OK) && (result.getData() != null)) {
                    String lat = data.getStringExtra(EXTRA_LATITUDE);
                    String lon = data.getStringExtra(EXTRA_LONGITUDE);
                    Log.d(TAG, "onReceiveGeoIntent: lat= " + lat + ". lon = " + lon);
                    mViewModel.searchImagesByCoordinates(lat, lon);
                    hideKeyboard(this);
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRvImages = findViewById(R.id.rv_images);
        mEtSearchQuery = findViewById(R.id.et_search_query);
        mBtnSearch = findViewById(R.id.btn_search);
        mPbLoading = findViewById(R.id.pbLoading);

        setSupportActionBar(findViewById(R.id.toolbar));

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        startReceivingBatteryLevelUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopReceivingBatteryLevelUpdates();
    }

    public void actionSearch(View view) {
        hideKeyboard(this);
        String searchString = mEtSearchQuery.getText().toString().trim();
        if (searchString.isEmpty()) {
            showSnackBarMessage(R.string.error_empty_search);
        } else {
            mViewModel.searchImages(searchString);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_toggle_night_mode):
                actionToggleNightMode();
                break;
            case (R.id.menu_favourites):
                startFavouritesActivity();
                break;
            case (R.id.menu_gallery):
                startGalleryActivity();
                break;
            case (R.id.menu_search_history):
                startSearchHistoryActivity();
                break;
            case (R.id.menu_maps):
                startMapsActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void startFavouritesActivity() {
        Intent intent = new Intent(this, FavouritesActivity.class);
        startActivity(intent);
    }

    private void startSearchHistoryActivity() {
        Intent intent = new Intent(this, SearchHistoryActivity.class);
        startActivity(intent);
    }

    private void startMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        mGetCoordinates.launch(intent);
    }

    private void startGalleryActivity() {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem nightMode = menu.findItem(R.id.menu_toggle_night_mode);
        Boolean isNightMode = mViewModel.getIsNightMode().getValue();
        if (isNightMode != null) {
            nightMode.setIcon(isNightMode ? R.drawable.ic_moon : R.drawable.ic_sun);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void actionToggleNightMode() {
        mViewModel.toggleNightMode();
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new ImageListAdapter(null, this);
        mRvImages.setAdapter(mRecyclerAdapter);
        mRvImages.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper helper = getSwipeToDismissTouchHelper();
        helper.attachToRecyclerView(mRvImages);
    }

    @Override
    public void onCardClick(int position) {
        Log.d(TAG, "ViewHolder clicked! Position = " + position);

        Image image = mRecyclerAdapter.getImageAtPosition(position);

        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putExtra(EXTRA_URL, image.getUrl(Image.PIC_SIZE_MEDIUM));
        intent.putExtra(EXTRA_SEARCH_STRING, mCurrentSearchString);
        this.startActivity(intent);
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
        Snackbar.make(findViewById(android.R.id.content),
                resourceId,
                BaseTransientBottomBar.LENGTH_LONG).show();
    }

    private static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void startReceivingBatteryLevelUpdates() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(mBatteryLevelReceiver, filter);
    }

    private void stopReceivingBatteryLevelUpdates() {
        this.unregisterReceiver(mBatteryLevelReceiver);
    }

    private void subscribeToModel() {
        mViewModel.getSearchString().observe(this, lastSearch -> {
            mCurrentSearchString = lastSearch;
            mEtSearchQuery.setText(lastSearch);
            mRecyclerAdapter.setSearchString(lastSearch);
            mRecyclerAdapter.notifyDataSetChanged();
        });

        mViewModel.getAllImages().observe(this, images -> {
            mRecyclerAdapter.setImages(images);
            mRecyclerAdapter.notifyDataSetChanged();
        });

        mViewModel.getLoadingState().observe(this, loadingState -> {
            Log.d(TAG, "onCreate: isLoading: " + loadingState);
            mPbLoading.setVisibility(loadingState ? View.VISIBLE : View.GONE);
            mBtnSearch.setClickable(!loadingState);
        });

        mViewModel.getIsNightMode().observe(this, isNightMode -> invalidateOptionsMenu());

        mViewModel.getSnackBarMessage().observe(this, this::showSnackBarMessage);
    }
}