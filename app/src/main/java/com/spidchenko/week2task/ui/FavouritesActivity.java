package com.spidchenko.week2task.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.FavouritesListAdapter;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.viewmodel.FavouritesActivityViewModel;

import static com.spidchenko.week2task.ui.MainActivity.EXTRA_SEARCH_STRING;
import static com.spidchenko.week2task.ui.MainActivity.EXTRA_URL;

public class FavouritesActivity extends AppCompatActivity implements FavouritesListAdapter.OnCardListener, FavouritesListAdapter.OnDeleteClickListener {

    private static final String TAG = "Favourites.LOG_TAG";

    private FavouritesActivityViewModel mViewModel;

    private RecyclerView mRvFavouriteImages;
    private FavouritesListAdapter mRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        mRvFavouriteImages = findViewById(R.id.rv_favourite_images);
        initRecyclerView();

        mViewModel = new ViewModelProvider(this).get(FavouritesActivityViewModel.class);

        mViewModel.getAllFavourites().observe(this, favourites -> {
            mRecyclerAdapter.setFavourites(favourites);
            mRecyclerAdapter.notifyDataSetChanged();
        });

        mViewModel.getSnackBarMessage().observe(this, this::showSnackBarMessage);

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
        mRecyclerAdapter = new FavouritesListAdapter(null, this, this);
        mRvFavouriteImages.setAdapter(mRecyclerAdapter);
        mRvFavouriteImages.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper helper = getSwipeToDismissTouchHelper();
        helper.attachToRecyclerView(mRvFavouriteImages);
    }

    @Override
    public void onCardClick(int position) {
        Log.d(TAG, "ViewHolder clicked! Position = " + position);
        Favourite favourite = mRecyclerAdapter.getFavouriteAtPosition(position);
        Intent intent = new Intent(this, ImageViewerActivity.class);
        intent.putExtra(EXTRA_URL, favourite.getUrl());
        intent.putExtra(EXTRA_SEARCH_STRING, favourite.getSearchRequest());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        Log.d(TAG, "Activity - onDeleteClick: " + position);
        Favourite favourite = mRecyclerAdapter.getFavouriteAtPosition(position);
        mViewModel.deleteFavourite(favourite);
    }

    ItemTouchHelper getSwipeToDismissTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof FavouritesListAdapter.CategoryViewHolder)
                    return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

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
                Favourite favourite = mRecyclerAdapter.getFavouriteAtPosition(position);
                mViewModel.deleteFavourite(favourite);
            }
        });
    }

    private void showSnackBarMessage(@StringRes int resourceId) {
        Snackbar.make(findViewById(android.R.id.content),
                resourceId,
                BaseTransientBottomBar.LENGTH_LONG).show();
    }

}