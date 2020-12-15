package com.spidchenko.week2task.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
import com.spidchenko.week2task.db.DatabaseHelper;
import com.spidchenko.week2task.db.models.Favourite;

import java.util.ArrayList;
import java.util.List;

public class FavouritesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FavListAdapter.LOG_TAG";
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_CATEGORY = 2;


    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private final OnCardListener mOnCardListener;
    private final OnDeleteClickListener mOnDeleteClickListener;
    //    private final OnCardListener mOnCardEmptyListener = new OnCardEmptyListener();
    private DatabaseHelper mDb;
    private final Context mContext;

    private List<Favourite> mImageList;

    public FavouritesListAdapter(Context context,
                                 ArrayList<Favourite> imageList,
                                 OnCardListener onCardListener,
                                 OnDeleteClickListener onDeleteClickListener) {
        this.mOnCardListener = onCardListener;
        this.mOnDeleteClickListener = onDeleteClickListener;
        this.mImageList = imageList;
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (mImageList.get(position).getUrl().trim().isEmpty()) {
            return TYPE_CATEGORY;
        } else {
            return TYPE_IMAGE;
        }
        //return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
            return new FavouriteViewHolder(mItemView, mOnCardListener, mOnDeleteClickListener);
        } else {
            View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(mItemView);
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

//    @Override
//    public void onItemDismiss(int position) {
//
//        Favourite toRemove = mImageList.get(position);
//        if (toRemove.getUrl().trim().isEmpty()) {
//            Toast.makeText(mContext, R.string.forbidden_feature, Toast.LENGTH_SHORT).show();
//            notifyDataSetChanged();
//            return; //Do we need this feature?
//        }
//        Log.d(TAG, "onItemDismiss: Before thread. Favourite toRemove " + toRemove);
//        new Thread(() -> {
//            Log.d(TAG, "onItemDismiss: Delete thread Started");
//            mDb = DatabaseHelper.getInstance(mContext);
//            Log.d(TAG, "onItemDismiss: Inside thread. Favourite toRemove " + toRemove);
//            mDb.deleteFavourite(toRemove);
//            mDb.close();
//            Log.d(TAG, "onItemDismiss: Delete thread ended");
//
//            mUiHandler.post(() -> {
//                mImageList.remove(position);
//                notifyItemRemoved(position);
//                Toast.makeText(mContext, R.string.removed_from_favourites, Toast.LENGTH_SHORT).show();
//                Log.d(TAG, "Dismissed:" + position);
//            });
//
//        }).start();

//    }

    public void setFavourites(List<Favourite> favourites) {
        this.mImageList = favourites;
    }

    public Favourite getFavouriteAtPosition(int position) {
        return mImageList.get(position);
    }

//    private static class OnCardEmptyListener implements OnCardListener {
//        @Override
//        public void onCardClick(int position) {
//            //Do nothing
//        }
//    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
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


    public class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageSurface;
        TextView imageSearchString;
        ImageView ivRemove;
        CardView cvFavouriteCard;
        OnCardListener onCardListener;
        OnDeleteClickListener onDeleteClickListener;


        public FavouriteViewHolder(@NonNull View itemView, OnCardListener onCardListener, OnDeleteClickListener onDeleteClickListener) {
            super(itemView);
            //ImageView mIvRemove = itemView.findViewById(R.id.iv_remove_from_favourites);
            imageSurface = itemView.findViewById(R.id.iv_favourite_image);
            imageSearchString = itemView.findViewById(R.id.tv_favourite_image_search_string);
            ivRemove = itemView.findViewById(R.id.iv_remove_from_favourites);
            cvFavouriteCard = itemView.findViewById(R.id.cv_favourite_card);

            this.onCardListener = onCardListener;
            this.onDeleteClickListener = onDeleteClickListener;

            itemView.setOnClickListener(this);

            ivRemove.setOnClickListener((view) -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onDeleteClickListener.onDeleteClick(getAdapterPosition());
                } else {
                    Log.d(TAG, "Clicked on NO_POSITION!");
                }
                Log.d(TAG, "Clicked on Delete " + getAdapterPosition());
            });

//            ivRemove.setOnClickListener((view) -> {
//                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
//                    onItemDismiss(getAdapterPosition());
//                } else {
//                    Log.d(TAG, "Clicked on NO_POSITION!");
//                }
//            });

            Log.d(TAG, "ViewHolder created!");
        }

        @Override
        public void onClick(View v) {
            onCardListener.onCardClick(getAdapterPosition());
        }

        private void bindView(Favourite favourite) {
            Log.d(TAG, "onBindViewHolder__: " + getAdapterPosition() + ". " + favourite.getUrl());
            //onCardListener = mOnCardListener;
            Glide.with(imageSurface.getContext())
                    .load(favourite.getUrl())
                    .into(imageSurface);
            Log.d(TAG, "Image Binded! " + favourite.getId());
        }
    }

    public interface OnCardListener {
        void onCardClick(int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}
