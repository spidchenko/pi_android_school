package com.spidchenko.week2task.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.spidchenko.week2task.R
import com.spidchenko.week2task.adapter.GalleryAdapter
import com.spidchenko.week2task.helpers.SwipeHelper.OnSwipeListener
import com.spidchenko.week2task.helpers.SwipeHelper.getSwipeToDismissTouchHelper
import com.spidchenko.week2task.helpers.ViewModelsFactory
import com.spidchenko.week2task.viewmodel.GalleryViewModel
import java.io.File
import java.util.*

class GalleryFragment : Fragment() {
    private var mViewModel: GalleryViewModel? = null
    private var mListener: OnFragmentInteractionListener? = null
    private var mRvImages: RecyclerView? = null
    private var mRecyclerAdapter: GalleryAdapter? = null
    private val mRequestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
            Log.d(TAG, "Permission callback! = $isGranted")
            if (isGranted) {
                enableCamera()
            } else {
                Snackbar.make(
                    requireView(), R.string.need_photo_permission,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            }
        }

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
        mViewModel = ViewModelProvider(this, factory).get(GalleryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_gallery, container, false)
        mRvImages = rootView.findViewById(R.id.rv_gallery_images)
        val btnMakePhoto: FloatingActionButton = rootView.findViewById(R.id.btn_make_photo)
        subscribeToModel()
        initRecyclerView()
        // Open fragment to make new photo
        btnMakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                enableCamera()
            } else {
                Log.d(TAG, "actionTakePhoto: Permission not granted! Trying to ask for...")
                mRequestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
        return rootView
    }

    private fun enableCamera() {
        mListener!!.onTakePhotosAction()
    }

    private fun initRecyclerView() {
        mRecyclerAdapter = GalleryAdapter(null)
        mRvImages!!.adapter = mRecyclerAdapter
        mRvImages!!.layoutManager = LinearLayoutManager(requireActivity())
        val helper = getSwipeToDismissTouchHelper(object : OnSwipeListener {
            override fun onSwipeToDismiss(position: Int) {
                mViewModel!!.deleteFile(
                    requireActivity().applicationContext.contentResolver,
                    mRecyclerAdapter!!.getFileAtPosition(position)
                )
            }
        })
        helper.attachToRecyclerView(mRvImages)
    }

    private fun subscribeToModel() {
        mViewModel!!.imageFiles.observe(viewLifecycleOwner, { files: List<File?> ->
            Log.d(TAG, "Observed LiveData: $files")
            mRecyclerAdapter!!.setImages(files as ArrayList<File?>)
            mRecyclerAdapter!!.notifyDataSetChanged()
        })
        mViewModel!!.snackBarMessage.observe(this,
            { message: Int? ->
                Snackbar.make(
                    requireView(), message!!,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            })
    }

    internal interface OnFragmentInteractionListener {
        fun onTakePhotosAction()
    }

    companion object {
        private const val TAG = "GalleryFragment.LOG_TAG"
    }
}