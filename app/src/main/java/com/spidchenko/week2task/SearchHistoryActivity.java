package com.spidchenko.week2task;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.adapter.SearchHistoryAdapter;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.DatabaseHelper;
import com.spidchenko.week2task.db.models.SearchRequest;

import java.util.ArrayList;
import java.util.LinkedList;

import static com.spidchenko.week2task.MainActivity.LOG_TAG;

public class SearchHistoryActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private CurrentUser currentUser;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    RecyclerView mRvSearchHistory;
    SearchHistoryAdapter mRecyclerAdapter;
    ArrayList<SearchRequest> mSearches = new ArrayList<>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);
        currentUser = CurrentUser.getInstance();

        initRecyclerView();
        insertFakeSearches();
    }

    //Save parent activity state on up home navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Options item selected");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            Log.d(LOG_TAG, "Pressed Back UP button");
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

    private void insertFakeSearches(){

        new Thread(() -> {
            db = DatabaseHelper.getInstance(SearchHistoryActivity.this);
            mSearches.addAll(db.getAllSearchRequests(currentUser.getUser().getId()));
            Log.d(LOG_TAG, "insertFakeSearches: "+ db.getAllSearchRequests(currentUser.getUser().getId()));
            db.close();

            mUiHandler.post(() -> {
                mRecyclerAdapter.notifyDataSetChanged();
                Log.d(LOG_TAG, "Dataset Changed!");
            });

        }).start();

//        for (int i = 0; i < 200; i++) {
//            SearchRequest element = new SearchRequest();
//            element.setSearchRequest("Search N=" + i);
//            mSearches.add(element);
//        }

    }
}