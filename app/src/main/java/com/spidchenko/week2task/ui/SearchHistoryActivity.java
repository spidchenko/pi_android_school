package com.spidchenko.week2task.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.SearchHistoryAdapter;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.FlickrRoomDatabase;
import com.spidchenko.week2task.db.dao.SearchRequestDao;
import com.spidchenko.week2task.db.models.SearchRequest;

import java.util.ArrayList;

public class SearchHistoryActivity extends AppCompatActivity {

    private static final String TAG = "SearchHistAct.LOG_TAG";
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private SearchRequestDao mSearchRequestDao;
    private CurrentUser mCurrentUser;

    RecyclerView mRvSearchHistory;
    SearchHistoryAdapter mRecyclerAdapter;
    ArrayList<SearchRequest> mSearches = new ArrayList<>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);
        mCurrentUser = CurrentUser.getInstance();
        FlickrRoomDatabase mDb = FlickrRoomDatabase.getDatabase(this);
        mSearchRequestDao = mDb.searchRequestDao();
        initRecyclerView();
        insertSearches();
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
        mRvSearchHistory = findViewById(R.id.rv_search_history);
        mRecyclerAdapter = new SearchHistoryAdapter(mSearches);
        mRvSearchHistory.setAdapter(mRecyclerAdapter);
        mRvSearchHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void insertSearches() {
        new Thread(() -> {
            mSearches.addAll(mSearchRequestDao.getAllSearchRequests(mCurrentUser.getUser().getId()));
            Log.d(TAG, "insertSearches: " + mSearchRequestDao.getAllSearchRequests(mCurrentUser.getUser().getId()));
            mUiHandler.post(() -> {
                mRecyclerAdapter.notifyDataSetChanged();
                Log.d(TAG, "Data set Changed!");
            });
        }).start();
    }
}