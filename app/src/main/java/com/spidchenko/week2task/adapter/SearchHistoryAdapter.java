package com.spidchenko.week2task.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.models.SearchRequest;

import java.util.ArrayList;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private static final String TAG = "SearchHistAdapt.LOG_TAG";

    private final ArrayList<SearchRequest> mSearchRequests;

    public SearchHistoryAdapter(ArrayList<SearchRequest> searchRequests) {
        this.mSearchRequests = searchRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_search_histrory, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView tvSearchString = holder.tvItemSearchRequestValue;
        tvSearchString.setText(mSearchRequests.get(position).getSearchRequest());
    }

    @Override
    public int getItemCount() {
        return mSearchRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemSearchRequestValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemSearchRequestValue = itemView.findViewById(R.id.tv_item_search_request_value);
            Log.d(TAG, "SearchHistoryItem ViewHolder created!");
        }
    }
}
