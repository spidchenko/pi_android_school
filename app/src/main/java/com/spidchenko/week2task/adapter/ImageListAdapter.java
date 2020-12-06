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
import com.spidchenko.week2task.models.Image;
import com.spidchenko.week2task.utils.SwipeHelper;

import java.util.LinkedList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder>
        implements SwipeHelper.ItemTouchHelperAdapter {

    private static final String TAG = "ImgListAdapter.LOG_TAG";

    private final LinkedList<Image> mImageList;
    private final OnCardListener mOnCardListener;
    private String mSearchString;

    public ImageListAdapter(LinkedList<Image> mImageList, OnCardListener onCardListener) {
        this.mOnCardListener = onCardListener;
        this.mImageList = mImageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_imagelist, parent, false);
        return new ViewHolder(itemView, mOnCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image currentImage = mImageList.get(position);

        Glide.with(holder.ivImageSurface.getContext())
                .load(currentImage.getUrl(Image.PIC_SIZE_MEDIUM))
                .into(holder.ivImageSurface);

        holder.tvImageSearchString.setText(mSearchString);
        Log.d(TAG, "Binded! " + currentImage.getId());
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        mImageList.remove(position);
        notifyItemRemoved(position);
    }

    public void setSearchString(String searchString) {
        mSearchString = searchString;
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

    }
}
