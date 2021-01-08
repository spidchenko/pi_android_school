package com.spidchenko.week2task.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.models.Favourite;

import java.util.ArrayList;
import java.util.List;

public class FavouritesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FavListAdapter.LOG_TAG";
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_CATEGORY = 2;

    public static final int ACTION_DELETE = 1;
    public static final int ACTION_OPEN_IMAGE = 2;

    private final OnFavouritesListAdapterListener mListener;
    private List<Favourite> mImageList;

    public FavouritesListAdapter(ArrayList<Favourite> imageList,
                                 OnFavouritesListAdapterListener listener) {
        this.mListener = listener;
        this.mImageList = imageList;
    }

    @Override
    public int getItemViewType(int position) {
        if (mImageList.get(position).getUrl().trim().isEmpty()) {
            return TYPE_CATEGORY;
        } else {
            return TYPE_IMAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
            return new FavouriteViewHolder(itemView, mListener);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Favourite favourite = mImageList.get(position);

        if (getItemViewType(position) == TYPE_IMAGE) {
            ((FavouriteViewHolder) holder).bindView(favourite);
        } else {
            ((CategoryViewHolder) holder).bindView(favourite);
        }
    }

    @Override
    public int getItemCount() {
        if (mImageList == null) {
            return 0;
        }
        return mImageList.size();
    }


    public void setFavourites(List<Favourite> favourites) {
        this.mImageList = favourites;
        this.notifyDataSetChanged();
    }

    public Favourite getFavouriteAtPosition(int position) {
        return mImageList.get(position);
    }


    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView imageSearchString;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSearchString = itemView.findViewById(R.id.tv_favourite_image_search_string);
        }

        private void bindView(Favourite favourite) {
            String mSearchString = favourite.getSearchRequest();
            imageSearchString.setText(mSearchString);

            Log.d(TAG, "Category Binded! " + favourite.getId());
        }
    }


    public interface OnFavouritesListAdapterListener {
        void onItemClick(int action, int position);
    }

    public static class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageSurface;
        TextView imageSearchString;
        ImageView ivRemove;
        CardView cvFavouriteCard;
        OnFavouritesListAdapterListener listener;


        public FavouriteViewHolder(@NonNull View itemView, OnFavouritesListAdapterListener listener) {
            super(itemView);
            imageSurface = itemView.findViewById(R.id.iv_favourite_image);
            imageSearchString = itemView.findViewById(R.id.tv_favourite_image_search_string);
            ivRemove = itemView.findViewById(R.id.iv_remove_from_favourites);
            cvFavouriteCard = itemView.findViewById(R.id.cv_favourite_card);

            this.listener = listener;

            itemView.setOnClickListener(this);

            ivRemove.setOnClickListener((view) -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(ACTION_DELETE, getAdapterPosition());
                } else {
                    Log.d(TAG, "Clicked on NO_POSITION!");
                }
                Log.d(TAG, "Clicked on Delete " + getAdapterPosition());
            });

            Log.d(TAG, "ViewHolder created!");
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(ACTION_OPEN_IMAGE, getAdapterPosition());
        }

        private void bindView(Favourite favourite) {
            Log.d(TAG, "onBindViewHolder__: " + getAdapterPosition() + ". " + favourite.getUrl());
            Glide.with(imageSurface.getContext())
                    .load(favourite.getUrl())
                    .into(imageSurface);
            Log.d(TAG, "Image Binded! " + favourite.getId());
        }
    }
}
