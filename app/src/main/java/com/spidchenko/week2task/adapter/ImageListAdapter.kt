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
import com.spidchenko.week2task.network.models.Image
import java.util.*

class ImageListAdapter(images: LinkedList<Image>?, private val mOnCardListener: OnCardListener) :
    RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {
    private var mImages: List<Image>?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_image_list, parent, false)
        return ViewHolder(itemView, mOnCardListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = mImages!![position]
        holder.bindView(image)
    }

    override fun getItemCount(): Int {
        return if (mImages == null) {
            0
        } else mImages!!.size
    }

    fun setImages(images: List<Image>?) {
        mImages = images
    }

    fun setSearchString(searchString: String?) {
        mSearchString = searchString
    }

    fun getImageAtPosition(position: Int): Image {
        return mImages!![position]
    }

    class ViewHolder(itemView: View, private val onCardListener: OnCardListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val ivImageSurface: ImageView = itemView.findViewById(R.id.iv_image_surface)
        private val tvImageSearchString: TextView =
            itemView.findViewById(R.id.tv_image_search_string)

        override fun onClick(v: View) {
            onCardListener.onCardClick(adapterPosition)
        }

        fun bindView(image: Image) {
            Glide.with(ivImageSurface.context)
                .load(image.getUrl(Image.PIC_SIZE_MEDIUM))
                .into(ivImageSurface)
            tvImageSearchString.text = mSearchString
            Log.d(TAG, "Binded! " + image.id)
        }

        init {
            itemView.setOnClickListener(this)
            Log.d(TAG, "ViewHolder created!")
        }
    }

    interface OnCardListener {
        fun onCardClick(position: Int)
    }

    companion object {
        private const val TAG = "ImgListAdapter.LOG_TAG"
        private var mSearchString: String? = null
    }

    init {
        mImages = images
    }
}