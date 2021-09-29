package com.spidchenko.week2task.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.spidchenko.week2task.R
import com.spidchenko.week2task.adapter.FavouritesListAdapter
import com.spidchenko.week2task.adapter.FavouritesListAdapter.OnFavouritesListAdapterListener
import com.spidchenko.week2task.db.models.Favourite
import com.spidchenko.week2task.helpers.SwipeHelper.OnSwipeListener
import com.spidchenko.week2task.helpers.SwipeHelper.getSwipeToDismissTouchHelper
import com.spidchenko.week2task.helpers.ViewModelsFactory
import com.spidchenko.week2task.viewmodel.FavouritesViewModel

class FavouritesFragment : Fragment(), OnFavouritesListAdapterListener {
    private var mListener: OnFragmentInteractionListener? = null
    private var mViewModel: FavouritesViewModel? = null
    private var mRvFavouriteImages: RecyclerView? = null
    private var mRecyclerAdapter: FavouritesListAdapter? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener =
            if (context is OnFragmentInteractionListener) {
                context
            } else {
                throw ClassCastException(
                    context.toString()
                            + resources.getString(R.string.exception_message)
                )
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ViewModelsFactory(requireActivity().application)
        mViewModel = ViewModelProvider(this, factory).get(FavouritesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_favourites, container, false)
        mRvFavouriteImages = rootView.findViewById(R.id.rv_favourite_images)
        initRecyclerView()
        subscribeToModel()
        return rootView
    }

    override fun onItemClick(action: Int, position: Int) {
        val favourite = mRecyclerAdapter!!.getFavouriteAtPosition(position)
        when (action) {
            FavouritesListAdapter.ACTION_DELETE -> {
                mViewModel!!.deleteFavourite(favourite)
            }
            FavouritesListAdapter.ACTION_OPEN_IMAGE -> {
                mListener!!.onOpenFavouriteAction(favourite)
            }
        }
    }

    private fun subscribeToModel() {
        Log.d(TAG, "subscribeToModel: ")
        mViewModel!!.favouritesWithCategories.observe(viewLifecycleOwner,
            { favourites: List<Favourite?> ->
                mRecyclerAdapter!!.setFavourites(favourites)
                Log.d(TAG, "subscribeToModel: favourites->$favourites")
            })
        mViewModel!!.snackBarMessage.observe(this,
            { message: Int? ->
                Snackbar.make(
                    requireView(), message!!,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            })
    }

    private fun initRecyclerView() {
        mRecyclerAdapter = FavouritesListAdapter(null, this)
        mRvFavouriteImages!!.adapter = mRecyclerAdapter
        mRvFavouriteImages!!.layoutManager = LinearLayoutManager(requireActivity())
        val helper = getSwipeToDismissTouchHelper(object : OnSwipeListener {
            override fun onSwipeToDismiss(position: Int) {
                val favourite = mRecyclerAdapter!!.getFavouriteAtPosition(position)
                mViewModel!!.deleteFavourite(favourite)
            }
        })
        helper.attachToRecyclerView(mRvFavouriteImages)
    }

    internal interface OnFragmentInteractionListener {
        fun onOpenFavouriteAction(favourite: Favourite?)
    }

    companion object {
        private const val TAG = "FavFragment.LOG_TAG"
    }
}