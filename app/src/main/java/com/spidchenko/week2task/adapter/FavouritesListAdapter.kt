package com.spidchenko.week2task.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.spidchenko.week2task.R
import com.spidchenko.week2task.db.models.Favourite
import java.util.*

class FavouritesListAdapter(
    imageList: ArrayList<Favourite>?,
    private val mListener: OnFavouritesListAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mImageList: List<Favourite>?
    override fun getItemViewType(position: Int): Int {
        return if (mImageList!![position].url!!.trim { it <= ' ' }.isEmpty()) {
            TYPE_CATEGORY
        } else {
            TYPE_IMAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_IMAGE) {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_favourite, parent, false)
            FavouriteViewHolder(itemView, mListener)
        } else {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
            CategoryViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val favourite = mImageList!![position]
        if (getItemViewType(position) == TYPE_IMAGE) {
            (holder as FavouriteViewHolder).bindView(favourite)
        } else {
            (holder as CategoryViewHolder).bindView(favourite)
        }
    }

    override fun getItemCount(): Int {
        return if (mImageList == null) {
            0
        } else mImageList!!.size
    }

    fun setFavourites(favourites: List<Favourite?>?) {
        mImageList = favourites as List<Favourite>?
        notifyDataSetChanged()
    }

    fun getFavouriteAtPosition(position: Int): Favourite {
        return mImageList!![position]
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageSearchString: TextView =
            itemView.findViewById(R.id.tv_favourite_image_search_string)

        fun bindView(favourite: Favourite) {
            val mSearchString = favourite.searchRequest
            imageSearchString.text = mSearchString
            Log.d(TAG, "Category Binded! " + favourite.id)
        }

    }

    interface OnFavouritesListAdapterListener {
        fun onItemClick(action: Int, position: Int)
    }

    class FavouriteViewHolder(
        itemView: View,
        private val listener: OnFavouritesListAdapterListener
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val imageSurface: ImageView = itemView.findViewById(R.id.iv_favourite_image)
        private val ivRemove: ImageView = itemView.findViewById(R.id.iv_remove_from_favourites)
        override fun onClick(v: View) {
            listener.onItemClick(ACTION_OPEN_IMAGE, adapterPosition)
        }

        fun bindView(favourite: Favourite) {
            Log.d(TAG, "onBindViewHolder__: " + adapterPosition + ". " + favourite.url)
            Glide.with(imageSurface.context)
                .load(favourite.url)
                .into(imageSurface)
            Log.d(TAG, "Image Binded! " + favourite.id)
        }

        init {
            itemView.setOnClickListener(this)
            ivRemove.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(ACTION_DELETE, adapterPosition)
                } else {
                    Log.d(TAG, "Clicked on NO_POSITION!")
                }
                Log.d(TAG, "Clicked on Delete $adapterPosition")
            }
            Log.d(TAG, "ViewHolder created!")
        }
    }

    companion object {
        private const val TAG = "FavListAdapter.LOG_TAG"
        private const val TYPE_IMAGE = 1
        private const val TYPE_CATEGORY = 2
        const val ACTION_DELETE = 1
        const val ACTION_OPEN_IMAGE = 2
    }

    init {
        mImageList = imageList
    }
}