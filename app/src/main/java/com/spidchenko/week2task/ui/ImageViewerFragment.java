package com.spidchenko.week2task.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.helpers.ViewModelsFactory;
import com.spidchenko.week2task.viewmodel.ImageViewerViewModel;

import static com.spidchenko.week2task.ui.MainActivity.EXTRA_SEARCH_STRING;
import static com.spidchenko.week2task.ui.MainActivity.EXTRA_URL;

public class ImageViewerFragment extends Fragment {

    private static final String TAG = "ImgViewFragment.LOG_TAG";

    private ImageViewerViewModel mViewModel;
    private CheckBox mCbToggleFavourite;

    private String mExtraUrl;
    private String mExtraSearchString;

    private final ActivityResultLauncher<String> mRequestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d(TAG, "Permission callback! = " + isGranted);
                if (isGranted) {
                    mViewModel.saveImage(Glide.with(requireContext().getApplicationContext()),
                            requireContext().getApplicationContext().getContentResolver(),
                            mExtraSearchString, mExtraUrl);
                } else {
                    Snackbar.make(requireView(), R.string.need_storage_permission,
                            BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mExtraUrl = getArguments().getString(EXTRA_URL);
            mExtraSearchString = getArguments().getString(EXTRA_SEARCH_STRING);
            Log.d(TAG, "onCreate: url:" + mExtraUrl + ". string:" + mExtraSearchString);
        }
        ViewModelsFactory factory = new ViewModelsFactory(requireActivity().getApplication());
        mViewModel = new ViewModelProvider(this, factory).get(ImageViewerViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false);


        WebView webView = rootView.findViewById(R.id.webView);
        TextView tvSearchString = rootView.findViewById(R.id.tv_search_string);
        mCbToggleFavourite = rootView.findViewById(R.id.cb_toggle_favourite);
        tvSearchString.setText(mExtraSearchString);
        ImageView ivSaveImage = rootView.findViewById(R.id.img_save);

        initWebView(webView);
        subscribeToModel();

        Log.d(TAG, "Intent received. Image Url: " + mExtraUrl +
                ". SearchString: " + mExtraSearchString);

        // Check/Uncheck current image as favourite and save choice to local Database
        mCbToggleFavourite.setOnClickListener(view -> mViewModel.toggleFavourite(mExtraSearchString, mExtraUrl));

        // Save current image to Media folder
        ivSaveImage.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireContext().getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                mViewModel.saveImage(Glide.with(requireContext().getApplicationContext()),
                        requireContext().getApplicationContext().getContentResolver(),
                        mExtraSearchString, mExtraUrl);
            } else {
                Log.d(TAG, "actionSaveImage: Permission not granted! Trying to ask for...");
                mRequestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });


        return rootView;
    }

    private void subscribeToModel() {

        mViewModel.getInFavourites(mExtraSearchString, mExtraUrl).observe(getViewLifecycleOwner(), inFavourites -> {
            Log.d(TAG, "onCreate: inFavouritesLiveData = " + inFavourites);
            mCbToggleFavourite.setChecked((inFavourites != null) && (!inFavourites.getUrl().isEmpty()));
        });

        mViewModel.getSnackBarMessage().observe(this,
                message -> Snackbar.make(requireView(), message,
                        BaseTransientBottomBar.LENGTH_LONG).show());
    }

    private void initWebView(WebView view) {
        //Add zoom controls:
        view.getSettings().setBuiltInZoomControls(true);

        //Resize image to screen width:
        view.getSettings().setLoadWithOverviewMode(true);

        view.getSettings().setUseWideViewPort(true);

        //This line will prevent random Fatal signal 11 (SIGSEGV) error on emulator:
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        view.loadUrl(mExtraUrl);
    }
}