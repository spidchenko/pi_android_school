package com.spidchenko.week2task.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.BatteryLevelReceiver;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.network.models.Image;

import java.util.Objects;

import static com.spidchenko.week2task.ui.MapsFragment.EXTRA_LATITUDE;
import static com.spidchenko.week2task.ui.MapsFragment.EXTRA_LONGITUDE;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener,
        MapsFragment.OnFragmentInteractionListener,
        GalleryFragment.OnFragmentInteractionListener,
        FavouritesFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity.LOG_TAG";
    public static final String EXTRA_URL = "com.spidchenko.week2task.extras.EXTRA_URL";
    public static final String EXTRA_SEARCH_STRING = "com.spidchenko.week2task.extras.EXTRA_SEARCH_STRING";

    private final BroadcastReceiver mBatteryLevelReceiver = new BatteryLevelReceiver();

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;

    private ActionBarDrawerToggle drawerToggle;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setSupportActionBar(findViewById(R.id.toolbar));

        mFragmentManager = getSupportFragmentManager();

        // Set a Toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Find our drawer view
        nvDrawer = findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE 1: Make sure to override the method with only a single `Bundle` argument
    // Note 2: Make sure you implement the correct `onPostCreate(Bundle savedInstanceState)` method.
    // There are 2 signatures and only `onPostCreate(Bundle state)` shows the hamburger icon.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    selectDrawerItem(menuItem);
                    return true;
                });
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_favourites:
                fragmentClass = FavouritesFragment.class;
                break;
            case R.id.nav_gallery:
                fragmentClass = GalleryFragment.class;
                break;
            case R.id.nav_search_history:
                fragmentClass = SearchHistoryFragment.class;
                break;
            case R.id.nav_maps:
                fragmentClass = MapsFragment.class;
                break;

            default:
                fragmentClass = CameraFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
        } else {
            Log.e(TAG, "selectDrawerItem: fragment is NULL");
        }


        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.action_bar_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case (R.id.menu_toggle_night_mode):
////                actionToggleNightMode();
//                break;
//            case (R.id.menu_favourites):
//                startFavouritesActivity();
//                break;
//            case (R.id.menu_gallery):
//                startGalleryActivity();
//                break;
//            case (R.id.menu_search_history):
//                startSearchHistoryActivity();
//                break;
//            case (R.id.menu_maps):
//                startMapsActivity();
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


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

    public void hideKeyboard() {
        View view = findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Action from login fragment
    @Override
    public void onLogIn() {
        mFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.content, SearchFragment.class, null)
                .commit();
    }

    // Action from search fragment
    @Override
    public void onImageClick(Image image, String searchString) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, image.getUrl(Image.PIC_SIZE_MEDIUM));
        bundle.putString(EXTRA_SEARCH_STRING, searchString);

        mFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.content, ImageViewerFragment.class, bundle)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSearchByCoordinatesAction(String lat, String lon) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_LATITUDE, lat);
        bundle.putString(EXTRA_LONGITUDE, lon);

        mFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.content, SearchFragment.class, bundle)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onTakePhotosAction() {
        mFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.content, CameraFragment.class, null)
                .addToBackStack(null)
                .commit();
    }
}