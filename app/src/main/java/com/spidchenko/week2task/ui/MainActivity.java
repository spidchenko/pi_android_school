package com.spidchenko.week2task.ui;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.BatteryLevelReceiver;
import com.spidchenko.week2task.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity.LOG_TAG";
    public static final String EXTRA_URL = "com.spidchenko.week2task.extras.EXTRA_URL";
    public static final String EXTRA_SEARCH_STRING = "com.spidchenko.week2task.extras.EXTRA_SEARCH_STRING";

    private final BroadcastReceiver mBatteryLevelReceiver = new BatteryLevelReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startReceivingBatteryLevelUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopReceivingBatteryLevelUpdates();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.menu_toggle_night_mode):
//                actionToggleNightMode();
                break;
            case (R.id.menu_favourites):
                startFavouritesActivity();
                break;
            case (R.id.menu_gallery):
                startGalleryActivity();
                break;
            case (R.id.menu_search_history):
                startSearchHistoryActivity();
                break;
            case (R.id.menu_maps):
                startMapsActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void startFavouritesActivity() {
//        Intent intent = new Intent(this, FavouritesActivity.class);
//        startActivity(intent);
    }

    private void startSearchHistoryActivity() {
//        Intent intent = new Intent(this, SearchHistoryActivity.class);
//        startActivity(intent);
    }

    private void startMapsActivity() {
//        Intent intent = new Intent(this, MapsActivity.class);
//        mGetCoordinates.launch(intent);
    }

    private void startGalleryActivity() {
//        Intent intent = new Intent(this, GalleryActivity.class);
//        startActivity(intent);
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem nightMode = menu.findItem(R.id.menu_toggle_night_mode);
//        Boolean isNightMode = mViewModel.getIsNightMode().getValue();
//        if (isNightMode != null) {
//            nightMode.setIcon(isNightMode ? R.drawable.ic_moon : R.drawable.ic_sun);
//        }
//        return super.onPrepareOptionsMenu(menu);
//    }

//    private void actionToggleNightMode() {
//        mViewModel.toggleNightMode();
//    }

    public void showSnackBarMessage(@StringRes int resourceId) {
        Snackbar.make(findViewById(android.R.id.content),
                resourceId,
                BaseTransientBottomBar.LENGTH_LONG).show();
    }

    private void startReceivingBatteryLevelUpdates() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(mBatteryLevelReceiver, filter);
    }

    private void stopReceivingBatteryLevelUpdates() {
        this.unregisterReceiver(mBatteryLevelReceiver);
    }


}