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
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.viewmodel.ImageViewerActivityViewModel;

import static com.spidchenko.week2task.ui.MainActivity.EXTRA_SEARCH_STRING;
import static com.spidchenko.week2task.ui.MainActivity.EXTRA_URL;

public class ImageViewerFragment extends Fragment {

    private static final String TAG = "ImgViewFragment.LOG_TAG";

    private Favourite mFavourite;
    private ImageViewerActivityViewModel mViewModel;
    private CheckBox cbToggleFavourite;

    ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d(TAG, "Permission callback! = " + isGranted);
                if (isGranted) {
                    mViewModel.saveImage(Glide.with(requireContext().getApplicationContext()),
                            requireContext().getApplicationContext().getContentResolver(),
                            mFavourite);
                } else {
                    ((MainActivity) requireActivity()).showSnackBarMessage(R.string.need_storage_permission);
                }
            });


    private String mExtraUrl;
    private String mExtraSearchString;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mExtraUrl = getArguments().getString(EXTRA_URL);
            mExtraSearchString = getArguments().getString(EXTRA_SEARCH_STRING);
            Log.d(TAG, "onCreate: url:" + mExtraUrl + ". string:" + mExtraSearchString);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_viewer, container, false);

        CurrentUser currentUser = CurrentUser.getInstance();

        WebView webView = rootView.findViewById(R.id.webView);
        TextView tvSearchString = rootView.findViewById(R.id.tv_search_string);
        cbToggleFavourite = rootView.findViewById(R.id.cb_toggle_favourite);
        tvSearchString.setText(mExtraSearchString);

        initWebView(webView);


        mFavourite = new Favourite(currentUser.getUser().getId(),
                mExtraSearchString, mExtraUrl);

        mViewModel = new ViewModelProvider(this).get(ImageViewerActivityViewModel.class);

        subscribeToModel();

        Log.d(TAG, "Intent received. Image Url: " + mExtraUrl +
                ". SearchString: " + mExtraSearchString);

        CheckBox cbToggleFavourite = rootView.findViewById(R.id.cb_toggle_favourite);
        // Check/Uncheck current image as favourite and save choice to local Database
        cbToggleFavourite.setOnClickListener(view -> {
            mViewModel.toggleFavourite(mFavourite);
        });

        ImageView ivSaveImage = rootView.findViewById(R.id.img_save);
        // Save current image to Media folder
        ivSaveImage.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(requireContext().getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                mViewModel.saveImage(Glide.with(requireContext().getApplicationContext()),
                        requireContext().getApplicationContext().getContentResolver(),
                        mFavourite);
            } else {
                Log.d(TAG, "actionSaveImage: Permission not granted! Trying to ask for...");
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });


        return rootView;
    }

    private void subscribeToModel() {

        mViewModel.getInFavourites(mFavourite).observe(getViewLifecycleOwner(), inFavourites -> {
            if (inFavourites != null) {
                cbToggleFavourite.setChecked(inFavourites);
                Log.d(TAG, "onCreate: inFavourites = " + inFavourites);
            }
        });

        mViewModel.getSnackBarMessage().observe(this,
                message -> ((MainActivity) requireActivity()).showSnackBarMessage(message));
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