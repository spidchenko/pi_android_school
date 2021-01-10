package com.spidchenko.week2task.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.SearchHistoryAdapter;
import com.spidchenko.week2task.db.AppDatabase;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.dao.SearchRequestDao;
import com.spidchenko.week2task.db.models.SearchRequest;

import java.util.ArrayList;

public class SearchHistoryFragment extends Fragment {

    private static final String TAG = "SearchHistFrag.LOG_TAG";

    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private SearchRequestDao mSearchRequestDao;
    private CurrentUser mCurrentUser;

    RecyclerView mRvSearchHistory;
    SearchHistoryAdapter mRecyclerAdapter;
    ArrayList<SearchRequest> mSearches = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_history, container, false);
        mRvSearchHistory = rootView.findViewById(R.id.rv_search_history);

        mCurrentUser = CurrentUser.getInstance();
        AppDatabase mDb = AppDatabase.getInstance(requireContext());
        mSearchRequestDao = mDb.searchRequestDao();

        initRecyclerView();
        insertSearches();

        return rootView;
    }

    private void initRecyclerView() {

        mRecyclerAdapter = new SearchHistoryAdapter(mSearches);
        mRvSearchHistory.setAdapter(mRecyclerAdapter);
        mRvSearchHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
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