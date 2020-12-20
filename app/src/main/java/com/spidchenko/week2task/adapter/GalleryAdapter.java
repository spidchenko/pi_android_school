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

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private static final String TAG = "GalleryAdapter.LOG_TAG";

    private ArrayList<File> mImageFiles;

    public GalleryAdapter(ArrayList<File> imageFiles) {
        this.mImageFiles = imageFiles;
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_imagelist, parent, false);
        return new GalleryAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {
        File imageFile = mImageFiles.get(position);
        holder.bindView(imageFile);
    }

    @Override
    public int getItemCount() {
        if (mImageFiles == null){
            return 0;
        }
        return mImageFiles.size();
    }

    public void setImages(ArrayList<File> imageFiles) {
        mImageFiles = imageFiles;
    }

    public File getFileAtPosition(int position) {
        return mImageFiles.get(position);
    }

    public void dismiss(int position) {

        Log.d(TAG, "dismiss:" +mImageFiles.get(position).getName());
        mImageFiles.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImageSurface;
        TextView tvImageSearchString;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImageSurface = itemView.findViewById(R.id.iv_image_surface);
            tvImageSearchString = itemView.findViewById(R.id.tv_image_search_string);
            Log.d(TAG, "ViewHolder created!");
        }

        public void bindView(File imageFile) {
            Glide.with(ivImageSurface.getContext())
                    .load(imageFile)
                    .into(ivImageSurface);
            tvImageSearchString.setText(imageFile.getName());
        }
    }
}
