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
import com.spidchenko.week2task.network.models.Image;

import java.util.LinkedList;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private static final String TAG = "ImgListAdapter.LOG_TAG";

    private List<Image> mImages;
    private final OnCardListener mOnCardListener;
    private static String mSearchString;

    public ImageListAdapter(LinkedList<Image> images, OnCardListener onCardListener) {
        this.mOnCardListener = onCardListener;
        this.mImages = images;
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
        Image image = mImages.get(position);
        holder.bindView(image);
    }

    @Override
    public int getItemCount() {
        if (mImages == null){
            return 0;
        }
        return mImages.size();
    }


    public void setImages(List<Image> images) {
        mImages = images;
    }

    public void setSearchString(String searchString){
        mSearchString = searchString;
    }

    public Image getImageAtPosition(int position) {
        return mImages.get(position);
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

        public void bindView(Image image) {
            Glide.with(ivImageSurface.getContext())
                    .load(image.getUrl(Image.PIC_SIZE_MEDIUM))
                    .into(ivImageSurface);
            tvImageSearchString.setText(mSearchString);
            Log.d(TAG, "Binded! " + image.getId());
        }
    }


    public interface OnCardListener {
        void onCardClick(int position);
    }
}
