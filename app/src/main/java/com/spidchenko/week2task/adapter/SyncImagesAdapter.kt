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
import com.spidchenko.week2task.db.models.SyncImage
import java.util.*

class SyncImagesAdapter(
    images: LinkedList<SyncImage>?,
    private val mOnCardListener: OnCardListener
) : RecyclerView.Adapter<SyncImagesAdapter.ViewHolder>() {
    private var mSyncImages: List<SyncImage>?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_image_list, parent, false)
        return ViewHolder(itemView, mOnCardListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = mSyncImages!![position]
        holder.bindView(image)
    }

    override fun getItemCount(): Int {
        return if (mSyncImages == null) {
            0
        } else mSyncImages!!.size
    }

    fun setImages(images: List<SyncImage>?) {
        mSyncImages = images
        notifyDataSetChanged()
    }

    fun getImageAtPosition(position: Int): SyncImage {
        return mSyncImages!![position]
    }

    interface OnCardListener {
        fun onCardClick(position: Int)
    }

    class ViewHolder(itemView: View, private val onCardListener: OnCardListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val ivImageSurface: ImageView = itemView.findViewById(R.id.iv_image_surface)
        private val tvImageSearchString: TextView =
            itemView.findViewById(R.id.tv_image_search_string)

        override fun onClick(v: View) {
            onCardListener.onCardClick(adapterPosition)
        }

        fun bindView(image: SyncImage) {
            Glide.with(ivImageSurface.context)
                .load(image.url)
                .into(ivImageSurface)
            tvImageSearchString.text = image.searchText
            Log.d(TAG, "Binded! " + image.id)
        }

        init {
            itemView.setOnClickListener(this)
            Log.d(TAG, "ViewHolder created!")
        }
    }

    companion object {
        private const val TAG = "ImgListAdapter.LOG_TAG"
    }

    init {
        mSyncImages = images
    }
}