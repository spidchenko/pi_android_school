package com.spidchenko.week2task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.adapter.ImageListAdapter;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.DatabaseHelper;
import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.models.Image;
import com.spidchenko.week2task.models.ImgSearchResult;
import com.spidchenko.week2task.network.FlickrApi;
import com.spidchenko.week2task.network.ServiceGenerator;
import com.spidchenko.week2task.utils.SharedPreferencesHelper;
import com.spidchenko.week2task.utils.SwipeHelper;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ImageListAdapter.OnCardListener {
    public static final String LOG_TAG = MainActivity.class.getSimpleName() + ".LOG_TAG";
    public static final String EXTRA_URL = "com.spidchenko.week2task.extras.EXTRA_URL";
    public static final String EXTRA_SEARCH_STRING = "com.spidchenko.week2task.extras.EXTRA_SEARCH_STRING";

   // private static final String API_KEY = "02692fb0a64b6b1b77f7f689c7f050c7";

    private DatabaseHelper mDb;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private CurrentUser currentUser;
    private FlickrApi mDataService;
    private Context mContext;
    private String mCurrentSearchString;
    private final LinkedList<Image> mImages = new LinkedList<>();
    private ImageListAdapter mRecyclerAdapter;
    private SharedPreferencesHelper mSharedPreferencesHelper;

    //UI
    private EditText mEtSearchQuery;
    private Button mBtnSearch;
    private RecyclerView mRvImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        currentUser = CurrentUser.getInstance();
        Log.d(LOG_TAG, "onCreate: CurrentUser: " + currentUser);

        mRvImages = findViewById(R.id.rv_images);
        mEtSearchQuery = findViewById(R.id.et_search_query);
        mBtnSearch = findViewById(R.id.btn_search);

        initRecyclerView();

        mSharedPreferencesHelper = SharedPreferencesHelper.init(this);
        mEtSearchQuery.setText(mSharedPreferencesHelper.getLastSearch());

        //Restore text on recreate
        if (savedInstanceState != null) {
//            mTvImageLinks.setText(savedInstanceState.getString(BUNDLE_IMAGE_LINKS));
        }
    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(BUNDLE_IMAGE_LINKS, mTvImageLinks.getText().toString());
//    }

    public void searchImages(View view) {

        //Close soft keyboard
        mEtSearchQuery.setEnabled(false);
        mEtSearchQuery.setEnabled(true);

        mCurrentSearchString = mEtSearchQuery.getText().toString().trim();
        if (mCurrentSearchString.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_search, Toast.LENGTH_LONG).show();
        } else {

            //TODO set spinning wheel here

            mBtnSearch.setClickable(false);
            saveCurrentSearch(mCurrentSearchString);
            mDataService = ServiceGenerator.getFlickrApi();

            Call<ImgSearchResult> call = mDataService.searchImages( mCurrentSearchString);

            call.enqueue(new Callback<ImgSearchResult>() {
                @Override
                public void onResponse(Call<ImgSearchResult> call, Response<ImgSearchResult> response) {

                    if (!response.isSuccessful()) {
                        Log.d(LOG_TAG, String.format("%s: %s", getString(R.string.error_text), response.code()));
                        return;
                    }

                    if (response.body() != null) {
                        Log.d(LOG_TAG, "Received flikr response" + response.body().getImageContainer().getImage());
                        List<Image> images = response.body().getImageContainer().getImage();

                        if (!images.isEmpty()) {
                            updateImages(images);
                            //TODO stop spinning wheel here
                            mBtnSearch.setClickable(true);
                        } else {
                            Toast.makeText(mContext, R.string.error_nothing_found, Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ImgSearchResult> call, Throwable t) {
                    Log.d(LOG_TAG, t.getMessage());
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void startFavouritesActivity(MenuItem item) {
        Intent intent = new Intent(this, FavouritesActivity.class);
        startActivity(intent);
    }

    public void startSearchHistoryActivity(MenuItem item) {
        Intent intent = new Intent(this, SearchHistoryActivity.class);
        startActivity(intent);
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new ImageListAdapter(mImages, this);
        mRvImages.setAdapter(mRecyclerAdapter);
        mRvImages.setLayoutManager(new LinearLayoutManager(mContext));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeHelper(mRecyclerAdapter));
        itemTouchHelper.attachToRecyclerView(mRvImages);
    }

    private void updateImages(List<Image> images) {
        mImages.clear();
        mImages.addAll(images);
        mRecyclerAdapter.setSearchString(mCurrentSearchString);
        mRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCardClick(int position) {
        Log.d(LOG_TAG, "ViewHolder clicked! Position = " + position);
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putExtra(EXTRA_URL, mImages.get(position).getUrl(Image.PIC_SIZE_MEDIUM));
        intent.putExtra(EXTRA_SEARCH_STRING, mCurrentSearchString);
        mContext.startActivity(intent);
    }

    private void saveCurrentSearch(String searchString) {
        mSharedPreferencesHelper.saveLastSearch(mCurrentSearchString);
        new Thread(() -> {
            mDb = DatabaseHelper.getInstance(MainActivity.this);
            mDb.addSearchRequest(new SearchRequest(currentUser.getUser().getId(), searchString));
            mDb.close();
            Log.d(LOG_TAG, "saveCurrentSearch: Worker thread finished saving. String: " + searchString);
        }).start();
    }
}