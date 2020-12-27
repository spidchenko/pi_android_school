package com.spidchenko.week2task.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.viewmodel.ImageViewerActivityViewModel;

import java.util.Objects;

import static com.spidchenko.week2task.ui.MainActivity.EXTRA_SEARCH_STRING;
import static com.spidchenko.week2task.ui.MainActivity.EXTRA_URL;

public class ImageViewerActivity extends AppCompatActivity {

    private static final String TAG = "ImageViewerAct.LOG_TAG";

    private String mIntentExtraUrl;
    private Favourite mFavourite;
    private ImageViewerActivityViewModel mViewModel;
    private CheckBox cbToggleFavourite;

    ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d(TAG, "Permission callback! = " + isGranted);
                if (isGranted) {
                    mViewModel.saveImage(Glide.with(getApplicationContext()),
                            getApplicationContext().getContentResolver(),
                            mFavourite);
                } else {
                    Toast.makeText(this, getString(R.string.need_storage_permission), Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        CurrentUser currentUser = CurrentUser.getInstance();
        initAppBar();
        mViewModel = new ViewModelProvider(this).get(ImageViewerActivityViewModel.class);
        Intent intent = getIntent();
        mIntentExtraUrl = intent.getStringExtra(EXTRA_URL);
        String intentExtraSearchString = intent.getStringExtra(EXTRA_SEARCH_STRING);

        WebView webView = findViewById(R.id.webView);
        TextView tvSearchString = findViewById(R.id.tv_search_string);
        cbToggleFavourite = findViewById(R.id.cb_toggle_favourite);
        tvSearchString.setText(intentExtraSearchString);

        initWebView(webView);


        mFavourite = new Favourite(currentUser.getUser().getId(),
                intentExtraSearchString, mIntentExtraUrl);

        mViewModel.getInFavourites(mFavourite).observe(this, inFavourites -> {
            if (inFavourites != null) {
                cbToggleFavourite.setChecked(inFavourites);
                Log.d(TAG, "onCreate: inFavourites = " + inFavourites);
            }
        });

        mViewModel.getSnackBarMessage().observe(this, this::showSnackBarMessage);

        Log.d(TAG, "Intent received. Image Url: " + mIntentExtraUrl +
                ". SearchString: " + intentExtraSearchString);
    }

    private void initAppBar() {
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void actionSaveImage(View view) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mViewModel.saveImage(Glide.with(getApplicationContext()),
                    getApplicationContext().getContentResolver(),
                    mFavourite);
        } else {
            Log.d(TAG, "actionSaveImage: Permission not granted! Trying to ask for...");
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    public void toggleFavourite(View view) {
        mViewModel.toggleFavourite(mFavourite);
    }

    private void initWebView(WebView view) {
        //Add zoom controls:
        view.getSettings().setBuiltInZoomControls(true);
        //Resize image to screen width:
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUseWideViewPort(true);
        //This line will prevent random Fatal signal 11 (SIGSEGV) error on emulator:
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        view.loadUrl(mIntentExtraUrl);
    }

    private void showSnackBarMessage(@StringRes int resourceId) {
        Snackbar.make(findViewById(android.R.id.content),
                resourceId,
                BaseTransientBottomBar.LENGTH_LONG).show();
    }

}