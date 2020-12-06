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
import static com.spidchenko.week2task.MainActivity.LOG_TAG;

public class ImageViewerActivity extends AppCompatActivity {

    private static final String TAG = "ImageViewerActivity";


    private String mIntentExtraUrl;
    private String mIntentExtraSearchString;
    private int mIntentExtraUserId;
    private CurrentUser currentUser;
    private Favourite favourite;
    private DatabaseHelper db;
    private boolean isFavorite = false;
    private Handler mUiHandler = new Handler(Looper.getMainLooper());
    private CheckBox cbToggleFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        currentUser = CurrentUser.getInstance();

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

        Log.d(LOG_TAG, "Intent received. Image Url: " + intent.getStringExtra(EXTRA_URL));
    }

    //Save parent activity state on up home navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOG_TAG, "Options item selected");
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            Log.d(LOG_TAG, "Pressed Back UP button");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toggleFavourite(View view) {
        if (cbToggleFavourite.isChecked()) {
            //cbToggleFavourite.setChecked(false);

            new Thread(() -> {
                db = DatabaseHelper.getInstance(this);

                db.addFavorite(new Favourite(currentUser.getUser().getId(),
                        mIntentExtraSearchString,"", mIntentExtraUrl));
                db.close();

                mUiHandler.post(() -> {
                    cbToggleFavourite.setClickable(true);
                    isFavorite = true;
                    Log.d(LOG_TAG, "Added to favourites!");
                    Toast.makeText(this, R.string.added_to_favourites, LENGTH_SHORT).show();
                });

            }).start();


        } else {

            new Thread(() -> {
                db = DatabaseHelper.getInstance(this);
                db.deleteFavourite(new Favourite(currentUser.getUser().getId(),
                        mIntentExtraSearchString,"", mIntentExtraUrl));
                db.close();

                mUiHandler.post(() -> {
                    cbToggleFavourite.setClickable(true);
                    isFavorite = false;
                    Log.d(LOG_TAG, "Removed from favourites!");
                    Toast.makeText(this, R.string.removed_from_favourites, LENGTH_SHORT).show();
                });

            }).start();

        }

    }

    private void checkInFavourites() {
        cbToggleFavourite.setClickable(false);

        new Thread(() -> {
            db = DatabaseHelper.getInstance(this);
            favourite = db.getFavourite(currentUser.getUser().getId(), mIntentExtraUrl);
            db.close();

            mUiHandler.post(() -> {
                cbToggleFavourite.setClickable(true);
                if (favourite != null) {
                    cbToggleFavourite.setChecked(true);
                    isFavorite = true;
                    Log.d(LOG_TAG, "checkInFavourites: Already in Favourites!");
                } else {
                    cbToggleFavourite.setChecked(false);
                    isFavorite = false;
                    Log.d(LOG_TAG, "checkInFavourites: Not in Favourites!");
                }
            });
        }).start();
    }
}