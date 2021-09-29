package com.spidchenko.week2task.helpers

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.spidchenko.week2task.adapter.FavouritesListAdapter.CategoryViewHolder

object SwipeHelper {
    @JvmStatic
    fun getSwipeToDismissTouchHelper(listener: OnSwipeListener): ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            // Category view from Favourites list will not move
            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if (viewHolder is CategoryViewHolder) 0 else super.getSwipeDirs(
                    recyclerView,
                    viewHolder
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                listener.onSwipeToDismiss(position)
            }
        })
    }

    interface OnSwipeListener {
        fun onSwipeToDismiss(position: Int)
    }
}