package com.spidchenko.week2task;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.spidchenko.week2task.db.CurrentUser;
import com.spidchenko.week2task.db.DatabaseHelper;
import com.spidchenko.week2task.db.models.Favourite;

import static android.widget.Toast.LENGTH_SHORT;
import static com.spidchenko.week2task.MainActivity.EXTRA_SEARCH_STRING;
import static com.spidchenko.week2task.MainActivity.EXTRA_URL;

public class ImageViewerActivity extends AppCompatActivity {

    private static final String TAG = "ImageViewerAct.LOG_TAG";


    private String mIntentExtraUrl;
    private String mIntentExtraSearchString;
    private int mIntentExtraUserId;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private CurrentUser mCurrentUser;
    private Favourite mFavourite;
    private boolean isFavorite = false;
    private DatabaseHelper mDb;
    private CheckBox cbToggleFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        mCurrentUser = CurrentUser.getInstance();

        Intent intent = getIntent();
        WebView webView = findViewById(R.id.webView);
        cbToggleFavourite = findViewById(R.id.cb_toggle_favourite);

        //Add zoom controls:
        webView.getSettings().setBuiltInZoomControls(true);

        //Resize image to screen width:
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        mIntentExtraUrl = intent.getStringExtra(EXTRA_URL);
        webView.loadUrl(mIntentExtraUrl);

        TextView tvSearchString = findViewById(R.id.tv_search_string);

        mIntentExtraSearchString = intent.getStringExtra(EXTRA_SEARCH_STRING);
        tvSearchString.setText(mIntentExtraSearchString);

        checkInFavourites();

        Log.d(TAG, "Intent received. Image Url: " + intent.getStringExtra(EXTRA_URL));
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
        if (cbToggleFavourite.isChecked()) {
            //cbToggleFavourite.setChecked(false);

            new Thread(() -> {
                mDb = DatabaseHelper.getInstance(this);
                mDb.addFavorite(new Favourite(mCurrentUser.getUser().getId(),
                        mIntentExtraSearchString, "", mIntentExtraUrl));
                mDb.close();

                mUiHandler.post(() -> {
                    cbToggleFavourite.setClickable(true);
                    isFavorite = true;
                    Log.d(TAG, "Added to favourites!");
                    Toast.makeText(this, R.string.added_to_favourites, LENGTH_SHORT).show();
                });

            }).start();


        } else {

            new Thread(() -> {
                mDb = DatabaseHelper.getInstance(this);
                mDb.deleteFavourite(new Favourite(mCurrentUser.getUser().getId(),
                        mIntentExtraSearchString, "", mIntentExtraUrl));
                mDb.close();

                mUiHandler.post(() -> {
                    cbToggleFavourite.setClickable(true);
                    isFavorite = false;
                    Log.d(TAG, "Removed from favourites!");
                    Toast.makeText(this, R.string.removed_from_favourites, LENGTH_SHORT).show();
                });

            }).start();
        }
    }

    private void checkInFavourites() {
        cbToggleFavourite.setClickable(false);

        new Thread(() -> {
            mDb = DatabaseHelper.getInstance(this);
            mFavourite = mDb.getFavourite(mCurrentUser.getUser().getId(), mIntentExtraUrl);
            mDb.close();

            mUiHandler.post(() -> {
                cbToggleFavourite.setClickable(true);
                if (mFavourite != null) {
                    cbToggleFavourite.setChecked(true);
                    isFavorite = true;
                    Log.d(TAG, "checkInFavourites: Already in Favourites!");
                } else {
                    cbToggleFavourite.setChecked(false);
                    isFavorite = false;
                    Log.d(TAG, "checkInFavourites: Not in Favourites!");
                }
            });

        }).start();
    }
}