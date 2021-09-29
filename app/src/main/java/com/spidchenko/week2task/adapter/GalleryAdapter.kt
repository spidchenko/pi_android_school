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
import java.io.File
import java.util.*

class GalleryAdapter(private var mImageFiles: ArrayList<File>?) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_image_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageFile = mImageFiles!![position]
        holder.bindView(imageFile)
    }

    override fun getItemCount(): Int {
        return if (mImageFiles == null) {
            0
        } else mImageFiles!!.size
    }

    fun setImages(imageFiles: ArrayList<File?>?) {
        mImageFiles = imageFiles as ArrayList<File>?
    }

    fun getFileAtPosition(position: Int): File {
        return mImageFiles!![position]
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImageSurface: ImageView = itemView.findViewById(R.id.iv_image_surface)
        private val tvImageSearchString: TextView =
            itemView.findViewById(R.id.tv_image_search_string)

        fun bindView(imageFile: File) {
            Glide.with(ivImageSurface.context)
                .load(imageFile)
                .into(ivImageSurface)
            tvImageSearchString.text = imageFile.name
        }

        init {
            Log.d(TAG, "ViewHolder created!")
        }
    }

    companion object {
        private const val TAG = "GalleryAdapter.LOG_TAG"
    }
}