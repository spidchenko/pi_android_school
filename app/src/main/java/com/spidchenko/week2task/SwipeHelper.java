package com.spidchenko.week2task;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.spidchenko.week2task.adapter.FavouritesListAdapter;

public class SwipeHelper {

    private static final String TAG = "SwipeHelper.LOG_TAG";

    public static ItemTouchHelper getSwipeToDismissTouchHelper(onSwipeListener listener) {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // Category view from Favourites list will not move
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
                listener.onSwipeToDismiss(position);
            }
        });
    }

    public interface onSwipeListener {
        void onSwipeToDismiss(int position);
    }

}
