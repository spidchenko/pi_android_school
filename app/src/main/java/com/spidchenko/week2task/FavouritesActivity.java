package com.spidchenko.week2task;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.adapter.FavouritesListAdapter;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.DatabaseHelper;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.utils.SwipeHelper;

import java.util.ArrayList;

import static com.spidchenko.week2task.MainActivity.EXTRA_SEARCH_STRING;
import static com.spidchenko.week2task.MainActivity.EXTRA_URL;
import static com.spidchenko.week2task.MainActivity.LOG_TAG;

public class FavouritesActivity extends AppCompatActivity implements FavouritesListAdapter.OnCardListener {

    private static final String TAG = "Favourites.LOG_TAG";

    private RecyclerView mRvFavouriteImages;
    private FavouritesListAdapter mRecyclerAdapter;
    private final ArrayList<Favourite> mImages = new ArrayList<>();
    private CurrentUser currentUser;
    private DatabaseHelper mDb;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        currentUser = CurrentUser.getInstance();

        mRvFavouriteImages = findViewById(R.id.rv_favourite_images);
       // mDb = DatabaseHelper.getInstance(this);
        initRecyclerView();
        insertImages();
    }

    //Save parent activity state on up home navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "Options item selected");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            Log.d(TAG, "Pressed Back UP button");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new FavouritesListAdapter(this, mImages, this);
        mRvFavouriteImages.setAdapter(mRecyclerAdapter);
        mRvFavouriteImages.setLayoutManager(new LinearLayoutManager(this));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeHelper(mRecyclerAdapter));
        itemTouchHelper.attachToRecyclerView(mRvFavouriteImages);
    }

    private void insertImages() {

        new Thread(() -> {
            mDb = DatabaseHelper.getInstance(this);
            mImages.addAll(mDb.getAllFavourites(currentUser.getUser().getId(), null));
            mDb.close();
            Log.d(TAG, "insertImages: " + mDb.getAllFavourites(currentUser.getUser().getId(), null));


            mUiHandler.post(() -> {
                mRecyclerAdapter.notifyDataSetChanged();
                Log.d(LOG_TAG, "Dataset Changed!");
                Log.d(TAG, "insertImages. images: " + mImages);

            });

        }).start();

    }

    @Override
    public void onCardClick(int position) {
        Log.d(TAG, "ViewHolder clicked! Position = " + position);
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putExtra(EXTRA_URL, mImages.get(position).getUrl());
        intent.putExtra(EXTRA_SEARCH_STRING, mImages.get(position).getSearchRequest());
        startActivity(intent);
    }
}