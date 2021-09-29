package com.spidchenko.week2task.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.spidchenko.week2task.R
import com.spidchenko.week2task.db.models.Favourite
import com.spidchenko.week2task.helpers.ViewModelsFactory
import com.spidchenko.week2task.viewmodel.ImageViewerViewModel

class ImageViewerFragment : Fragment() {
    private var mViewModel: ImageViewerViewModel? = null
    private var mCbToggleFavourite: CheckBox? = null
    private var mExtraUrl: String? = null
    private var mExtraSearchString: String? = null
    private val mRequestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
            Log.d(TAG, "Permission callback! = $isGranted")
            if (isGranted) {
                mViewModel!!.saveImage(
                    Glide.with(requireContext().applicationContext),
                    requireContext().applicationContext.contentResolver,
                    mExtraSearchString, mExtraUrl
                )
            } else {
                Snackbar.make(
                    requireView(), R.string.need_storage_permission,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mExtraUrl = requireArguments().getString(MainActivity.EXTRA_URL)
            mExtraSearchString = requireArguments().getString(MainActivity.EXTRA_SEARCH_STRING)
            Log.d(TAG, "onCreate: url:$mExtraUrl. string:$mExtraSearchString")
        }
        val factory = ViewModelsFactory(requireActivity().application)
        mViewModel = ViewModelProvider(this, factory).get(ImageViewerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false)
        val webView = rootView.findViewById<WebView>(R.id.webView)
        val tvSearchString = rootView.findViewById<TextView>(R.id.tv_search_string)
        mCbToggleFavourite = rootView.findViewById(R.id.cb_toggle_favourite)
        tvSearchString.text = mExtraSearchString
        val ivSaveImage = rootView.findViewById<ImageView>(R.id.img_save)
        initWebView(webView)
        subscribeToModel()
        Log.d(
            TAG, "Intent received. Image Url: $mExtraUrl. SearchString: $mExtraSearchString"
        )

        // Check/Uncheck current image as favourite and save choice to local Database
        mCbToggleFavourite?.setOnClickListener {
            mViewModel!!.toggleFavourite(
                mExtraSearchString,
                mExtraUrl
            )
        }

        // Save current image to Media folder
        ivSaveImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext().applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mViewModel!!.saveImage(
                    Glide.with(requireContext().applicationContext),
                    requireContext().applicationContext.contentResolver,
                    mExtraSearchString, mExtraUrl
                )
            } else {
                Log.d(TAG, "actionSaveImage: Permission not granted! Trying to ask for...")
                mRequestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        return rootView
    }

    private fun subscribeToModel() {
        mViewModel!!.getInFavourites(mExtraSearchString, mExtraUrl)
            .observe(viewLifecycleOwner, { inFavourites: Favourite? ->
                Log.d(TAG, "onCreate: inFavouritesLiveData = $inFavourites")
                mCbToggleFavourite!!.isChecked =
                    inFavourites != null && inFavourites.url!!.isNotEmpty()
            })
        mViewModel!!.snackBarMessage.observe(this,
            { message: Int? ->
                Snackbar.make(
                    requireView(), message!!,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            })
    }

    private fun initWebView(view: WebView) {
        //Add zoom controls:
        view.settings.builtInZoomControls = true

        //Resize image to screen width:
        view.settings.loadWithOverviewMode = true
        view.settings.useWideViewPort = true

        //This line will prevent random Fatal signal 11 (SIGSEGV) error on emulator:
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        view.loadUrl(mExtraUrl!!)
    }

    companion object {
        private const val TAG = "ImgViewFragment.LOG_TAG"
    }
}