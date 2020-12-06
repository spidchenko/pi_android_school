package com.spidchenko.week2task.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.DatabaseHelper;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.utils.SwipeHelper;

import java.util.ArrayList;

public class FavouritesListAdapter extends RecyclerView.Adapter<FavouritesListAdapter.ViewHolder>
        implements SwipeHelper.ItemTouchHelperAdapter {

    private static final String TAG = "FavListAdapter.LOG_TAG";

    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private final OnCardListener mOnCardListener;
    private final OnCardListener mOnCardEmptyListener = new OnCardEmptyListener();
    private DatabaseHelper mDb;
    private final Context mContext;

    private final ArrayList<Favourite> mImageList;

    public FavouritesListAdapter(Context context, ArrayList<Favourite> mImageList, OnCardListener onCardListener) {
        this.mOnCardListener = onCardListener;
        this.mImageList = mImageList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
        return new ViewHolder(mItemView, mOnCardListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favourite mCurrent = mImageList.get(position);
        String mSearchString = mCurrent.getSearchRequest();
        Log.d(TAG, "onBindViewHolder__: " + position + ". " + mCurrent.getUrl());
        if (mCurrent.getUrl().trim().isEmpty()) {
            Log.d(TAG, "onBindViewHolder: " + position + ". " + mCurrent.getUrl());

            holder.onCardListener = mOnCardEmptyListener;
            holder.imageSurface.setVisibility(View.GONE);           //No image
            holder.ivRemove.setVisibility(View.GONE);               //No cross
            holder.imageSearchString.setVisibility(View.VISIBLE);   //Show text
            holder.imageSearchString.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);

        } else {
            holder.onCardListener = mOnCardListener;
            holder.imageSurface.setVisibility(View.VISIBLE);        //Show image
            holder.ivRemove.setVisibility(View.VISIBLE);            //Show cross
            holder.imageSearchString.setVisibility(View.GONE);      //No text
        }

        Glide.with(holder.imageSurface.getContext())
                .load(mCurrent.getUrl())
                .into(holder.imageSurface);

        holder.imageSearchString.setText(mSearchString);

        Log.d(TAG, "Binded! " + mCurrent.getId());
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    @Override
    public void onItemDismiss(int position) {

        Favourite toRemove = mImageList.get(position);
        if (toRemove.getUrl().trim().isEmpty()) {
            Toast.makeText(mContext, R.string.forbidden_feature, Toast.LENGTH_SHORT).show();
            notifyDataSetChanged();
            return; //Do we need this feature?
        }
        Log.d(TAG, "onItemDismiss: Before thread. Favourite toRemove " + toRemove);
        new Thread(() -> {
            Log.d(TAG, "onItemDismiss: Delete thread Started");
            mDb = DatabaseHelper.getInstance(mContext);
            Log.d(TAG, "onItemDismiss: Inside thread. Favourite toRemove " + toRemove);
            mDb.deleteFavourite(toRemove);
            mDb.close();
            Log.d(TAG, "onItemDismiss: Delete thread ended");

            mUiHandler.post(() -> {
                mImageList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(mContext, R.string.removed_from_favourites, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Dismissed:" + position);
            });

        }).start();


    }

    private static class OnCardEmptyListener implements OnCardListener {
        @Override
        public void onCardClick(int position) {
            //Do nothing
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageSurface;
        TextView imageSearchString;
        ImageView ivRemove;
        CardView cvFavouriteCard;
        OnCardListener onCardListener;


        public ViewHolder(@NonNull View itemView, OnCardListener onCardListener) {
            super(itemView);
            //ImageView mIvRemove = itemView.findViewById(R.id.iv_remove_from_favourites);
            imageSurface = itemView.findViewById(R.id.iv_favourite_image);
            imageSearchString = itemView.findViewById(R.id.tv_favourite_image_search_string);
            ivRemove = itemView.findViewById(R.id.iv_remove_from_favourites);
            cvFavouriteCard = itemView.findViewById(R.id.cv_favourite_card);

            this.onCardListener = onCardListener;

            itemView.setOnClickListener(this);

            ivRemove.setOnClickListener((view) -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemDismiss(getAdapterPosition());
                } else {
                    Log.d(TAG, "Clicked on NO_POSITION!");
                }
            });

            Log.d(TAG, "ViewHolder created!");
        }

        @Override
        public void onClick(View v) {
            onCardListener.onCardClick(getAdapterPosition());
        }

    }

    public interface OnCardListener {
        void onCardClick(int position);
    }
}
