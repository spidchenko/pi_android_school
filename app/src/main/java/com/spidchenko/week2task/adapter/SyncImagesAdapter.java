package com.spidchenko.week2task.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.models.SyncImage;

import java.util.LinkedList;
import java.util.List;

public class SyncImagesAdapter extends RecyclerView.Adapter<SyncImagesAdapter.ViewHolder> {

    private static final String TAG = "ImgListAdapter.LOG_TAG";
    private static String mSearchString;
    private final OnCardListener mOnCardListener;
    private List<SyncImage> mSyncImages;

    public SyncImagesAdapter(LinkedList<SyncImage> images, OnCardListener onCardListener) {
        this.mOnCardListener = onCardListener;
        this.mSyncImages = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_image_list, parent, false);
        return new ViewHolder(itemView, mOnCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SyncImage image = mSyncImages.get(position);
        holder.bindView(image);
    }

    @Override
    public int getItemCount() {
        if (mSyncImages == null) {
            return 0;
        }
        return mSyncImages.size();
    }


    public void setImages(List<SyncImage> images) {
        mSyncImages = images;
    }

    public void setSearchString(String searchString) {
        mSearchString = searchString;
    }

    public SyncImage getImageAtPosition(int position) {
        return mSyncImages.get(position);
    }


    public interface OnCardListener {
        void onCardClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivImageSurface;
        TextView tvImageSearchString;
        OnCardListener onCardListener;

        public ViewHolder(@NonNull View itemView, OnCardListener onCardListener) {
            super(itemView);
            ivImageSurface = itemView.findViewById(R.id.iv_image_surface);
            tvImageSearchString = itemView.findViewById(R.id.tv_image_search_string);
            this.onCardListener = onCardListener;
            itemView.setOnClickListener(this);
            Log.d(TAG, "ViewHolder created!");
        }

        @Override
        public void onClick(View v) {
            onCardListener.onCardClick(getAdapterPosition());
        }

        public void bindView(SyncImage image) {
            Glide.with(ivImageSurface.getContext())
                    .load(image.getUrl())
                    .into(ivImageSurface);
            tvImageSearchString.setText(mSearchString);
            Log.d(TAG, "Binded! " + image.getId());
        }
    }
}
