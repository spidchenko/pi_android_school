package com.spidchenko.week2task.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.spidchenko.week2task.R
import com.spidchenko.week2task.db.models.SearchRequest
import java.util.*

class SearchHistoryAdapter(private val mSearchRequests: ArrayList<SearchRequest>) :
    RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_search_history, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tvSearchString = holder.tvItemSearchRequestValue
        tvSearchString.text = mSearchRequests[position].searchRequest
    }

    override fun getItemCount(): Int {
        return mSearchRequests.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemSearchRequestValue: TextView =
            itemView.findViewById(R.id.tv_item_search_request_value)

        init {
            Log.d(TAG, "SearchHistoryItem ViewHolder created!")
        }
    }

    companion object {
        private const val TAG = "SearchHistAdapt.LOG_TAG"
    }
}