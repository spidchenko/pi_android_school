package com.spidchenko.week2task.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeHelper extends ItemTouchHelper.SimpleCallback {
    private final ItemTouchHelperAdapter mAdapter;

    private static final String TAG = "SwipeHelper.LOG_TAG";

    public SwipeHelper(ItemTouchHelperAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d(TAG, "ViewHolder Swiped! Position= " + viewHolder.getAdapterPosition());
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    public interface ItemTouchHelperAdapter {
        //void onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }
}
