package com.spidchenko.week2task.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.BatteryLevelReceiver;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.models.Favourite;
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

    private ActionBarDrawerToggle drawerToggle;

    private FragmentManager mFragmentManager;

    Boolean mIsNightMode = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();

        // ActionBar on login screen disabled
        if (Objects.requireNonNull(mFragmentManager.findFragmentById(R.id.content)).getClass() != LoginFragment.class) {
            initActionBar();
        }
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

        if (item.getItemId() == R.id.menu_toggle_night_mode) {
            toggleNightMode();
        }

        return super.onOptionsItemSelected(item);
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
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_search:
                fragmentClass = SearchFragment.class;
                break;
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
                fragmentClass = SearchFragment.class;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem nightMode = menu.findItem(R.id.menu_toggle_night_mode);
        if (mIsNightMode != null) {
            nightMode.setIcon(mIsNightMode ? R.drawable.ic_moon : R.drawable.ic_sun);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void toggleNightMode() {
        // Get the night mode state of the app.
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        //Set the theme mode for the restarted activity
        if (nightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode
                    (AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode
                    (AppCompatDelegate.MODE_NIGHT_YES);
        }
        invalidateOptionsMenu();
    }

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

    private void initActionBar() {
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
        NavigationView nvDrawer = findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
    }

    // Action from login fragment
    @Override
    public void onLogIn() {
        mFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.content, SearchFragment.class, null)
                .commit();

        initActionBar();
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

    @Override
    public void onOpenFavouriteAction(Favourite favourite) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, favourite.getUrl());
        bundle.putString(EXTRA_SEARCH_STRING, favourite.getSearchRequest());

        mFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.content, ImageViewerFragment.class, bundle)
                .addToBackStack(null)
                .commit();
    }
}