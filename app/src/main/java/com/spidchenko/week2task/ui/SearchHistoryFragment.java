package com.spidchenko.week2task.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.SearchHistoryAdapter;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.FlickrRoomDatabase;
import com.spidchenko.week2task.db.dao.SearchRequestDao;
import com.spidchenko.week2task.db.models.SearchRequest;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchHistoryFragment extends Fragment {

    private static final String TAG = "SearchHistFrag.LOG_TAG";

    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private SearchRequestDao mSearchRequestDao;
    private CurrentUser mCurrentUser;

    RecyclerView mRvSearchHistory;
    SearchHistoryAdapter mRecyclerAdapter;
    ArrayList<SearchRequest> mSearches = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchHistoryFragment newInstance(String param1, String param2) {
        SearchHistoryFragment fragment = new SearchHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        View rootView = inflater.inflate(R.layout.fragment_search_history, container, false);
        mRvSearchHistory = rootView.findViewById(R.id.rv_search_history);

        mCurrentUser = CurrentUser.getInstance();
        FlickrRoomDatabase mDb = FlickrRoomDatabase.getDatabase(requireContext());
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