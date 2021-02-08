package com.spidchenko.week2task.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.adapter.SearchHistoryAdapter;
import com.spidchenko.week2task.db.models.SearchRequest;
import com.spidchenko.week2task.helpers.ViewModelsFactory;
import com.spidchenko.week2task.viewmodel.SearchHistoryViewModel;

import java.util.ArrayList;

public class SearchHistoryFragment extends Fragment {

    private final ArrayList<SearchRequest> mSearches = new ArrayList<>();
    private RecyclerView mRvSearchHistory;
    private SearchHistoryAdapter mRecyclerAdapter;
    private SearchHistoryViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelsFactory factory = new ViewModelsFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, factory).get(SearchHistoryViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_history, container, false);
        mRvSearchHistory = rootView.findViewById(R.id.rv_search_history);

        subscribeToModel();
        initRecyclerView();

        return rootView;
    }

    private void initRecyclerView() {

        mRecyclerAdapter = new SearchHistoryAdapter(mSearches);
        mRvSearchHistory.setAdapter(mRecyclerAdapter);
        mRvSearchHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void subscribeToModel() {
        mViewModel.getSearchRequests().observe(getViewLifecycleOwner(), searchRequests -> {
            mSearches.addAll(searchRequests);
            mRecyclerAdapter.notifyDataSetChanged();
        });
    }
}