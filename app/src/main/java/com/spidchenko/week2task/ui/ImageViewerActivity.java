package com.spidchenko.week2task.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.viewmodel.ImageViewerActivityViewModel;

import static com.spidchenko.week2task.ui.MainActivity.EXTRA_SEARCH_STRING;
import static com.spidchenko.week2task.ui.MainActivity.EXTRA_URL;

public class ImageViewerActivity extends AppCompatActivity {

    private static final String TAG = "ImageViewerAct.LOG_TAG";

    private String mIntentExtraUrl;
    private String mIntentExtraSearchString;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private CurrentUser mCurrentUser;
    private Favourite mFavourite;
    private ImageViewerActivityViewModel mViewModel;
    private CheckBox cbToggleFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        mCurrentUser = CurrentUser.getInstance();

        mViewModel = new ViewModelProvider(this).get(ImageViewerActivityViewModel.class);

        Intent intent = getIntent();
        mIntentExtraUrl = intent.getStringExtra(EXTRA_URL);
        mIntentExtraSearchString = intent.getStringExtra(EXTRA_SEARCH_STRING);

        WebView webView = findViewById(R.id.webView);
        TextView tvSearchString = findViewById(R.id.tv_search_string);
        cbToggleFavourite = findViewById(R.id.cb_toggle_favourite);
        tvSearchString.setText(mIntentExtraSearchString);

        initWebView(webView);


        mFavourite = new Favourite(mCurrentUser.getUser().getId(),
                mIntentExtraSearchString, "", mIntentExtraUrl);

        mViewModel.getInFavourites(mFavourite).observe(this, inFavourites -> {
            if (inFavourites != null) {
                cbToggleFavourite.setChecked(inFavourites);
                Log.d(TAG, "onCreate: inFavourites = " + inFavourites);
            }
        });

        mViewModel.getSnackBarMessage().observe(this, this::showSnackBarMessage);

        Log.d(TAG, "Intent received. Image Url: " + mIntentExtraUrl +
                ". SearchString: " + mIntentExtraSearchString);
    }


    //Save parent activity state on up home navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "Options item selected");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            Log.d(TAG, "Pressed Back UP button");
            return true;
        }
        return super.onOptionsItemSelected(item);
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