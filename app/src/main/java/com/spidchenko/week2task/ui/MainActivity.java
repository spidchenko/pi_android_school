package com.spidchenko.week2task.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
    private static final String BACK_STACK_ROOT_TAG = "com.spidchenko.week2task.extras.root_fragment";

    private final BroadcastReceiver mBatteryLevelReceiver = new BatteryLevelReceiver();
    private final Boolean mIsNightMode = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
    private Boolean mIsOnLoginScreen;
    private Boolean mIsTabletMode = false;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private FragmentContainerView mDetailView;
    private FragmentManager mFragmentManager;
    private NavController mNavController;

    ActivityResultLauncher<Intent> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        Snackbar.make(findViewById(android.R.id.content), R.string.need_alert_permission,
                                BaseTransientBottomBar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), R.string.alert_permission_ok,
                                BaseTransientBottomBar.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        if (findViewById(R.id.detail_content) != null) {
            Log.d(TAG, "onCreate: Now in TABLET mode");
            mIsTabletMode = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mDetailView = findViewById(R.id.detail_content);
        } else {
            Log.d(TAG, "onCreate: Now in PHONE mode");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mFragmentManager = getSupportFragmentManager();

//FIXME        mIsOnLoginScreen = (Objects.requireNonNull(mFragmentManager.findFragmentById(R.id.content)).getClass() == LoginFragment.class);

        // ActionBar on login screen disabled
//FIXME        if (!mIsOnLoginScreen) {
//            initActionBar();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, R.string.need_alert_permission, Toast.LENGTH_LONG).show();
            requestPermissionToLaunchOnBoot();
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

//    private void setupDrawerContent(NavigationView navigationView) {
//        navigationView.setNavigationItemSelectedListener(
//                menuItem -> {
//                    selectDrawerItem(menuItem);
//                    return true;
//                });
//    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

//    public void selectDrawerItem(MenuItem menuItem) {
//        //FIXME
////        Class<? extends Fragment> fragmentClass;
////        int itemId = menuItem.getItemId();
////        if (itemId == R.id.nav_search) {
////            fragmentClass = SearchFragment.class;
////        } else if (itemId == R.id.nav_favourites) {
////            fragmentClass = FavouritesFragment.class;
////        } else if (itemId == R.id.nav_gallery) {
////            fragmentClass = GalleryFragment.class;
////        } else if (itemId == R.id.nav_search_history) {
////            fragmentClass = SearchHistoryFragment.class;
////        } else if (itemId == R.id.nav_maps) {
////            fragmentClass = MapsFragment.class;
////        } else {
////            fragmentClass = SearchFragment.class;
////        }
////
////
////        // Pop off everything up to and including the current tab
////        try {
////            Fragment fragment = fragmentClass.newInstance();
////            mFragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
////            mFragmentManager.beginTransaction()
////                    .setReorderingAllowed(true)
////                    .replace(R.id.content, fragment)
////                    .addToBackStack(BACK_STACK_ROOT_TAG)
////                    .commit();
////        } catch (IllegalAccessException | InstantiationException e) {
////            e.printStackTrace();
////        }
//
//        hideDetailView();
//
//        // Insert the fragment by replacing any existing fragment
////        replaceFragment(fragmentClass, null);
//
//        // Highlight the selected item has been done by NavigationView
//        menuItem.setChecked(true);
//        // Set action bar title
//        setTitle(menuItem.getTitle());
//        // Close the navigation drawer
//        mDrawer.closeDrawers();
//    }

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

    private void requestPermissionToLaunchOnBoot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            requestPermissionLauncher.launch(intent);
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
//        setupDrawerContent(nvDrawer);
    }

    // Action from login fragment
    @Override
    public void onLogIn() {
        mNavController.navigate(R.id.searchFragment);
//        replaceFragment(SearchFragment.class, null);
        mIsOnLoginScreen = false;
        initActionBar();
    }

    // Action from search fragment
    @Override
    public void onImageClick(Image image, String searchString) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, image.getUrl(Image.PIC_SIZE_MEDIUM));
        bundle.putString(EXTRA_SEARCH_STRING, searchString);
        replaceFragment(ImageViewerFragment.class, bundle);
    }

    @Override
    public void onSearchByCoordinatesAction(String lat, String lon) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_LATITUDE, lat);
        bundle.putString(EXTRA_LONGITUDE, lon);
        replaceFragment(SearchFragment.class, bundle);
    }

    @Override
    public void onTakePhotosAction() {
        replaceFragment(CameraFragment.class, null);
    }

    @Override
    public void onOpenFavouriteAction(Favourite favourite) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, favourite.getUrl());
        bundle.putString(EXTRA_SEARCH_STRING, favourite.getSearchRequest());
        replaceFragment(ImageViewerFragment.class, bundle);
    }

    private void replaceFragment(@NonNull Class<? extends Fragment> fragmentClass, Bundle bundle) {

        int container;

        if ((fragmentClass == ImageViewerFragment.class) && mIsTabletMode) {
            container = R.id.detail_content;
            showDetailView();
        } else {
            container = R.id.content;
            hideDetailView();
        }

        mFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(container, fragmentClass, bundle)
                .addToBackStack(null)
                .commit();
    }

    private void showDetailView() {
        if (mDetailView != null) {
            mDetailView.setVisibility(View.VISIBLE);
        }
    }

    private void hideDetailView() {
        if (mDetailView != null) {
            mDetailView.setVisibility(View.GONE);
        }
    }

}