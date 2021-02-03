package com.spidchenko.week2task.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.BatteryLevelReceiver;
import com.spidchenko.week2task.R;
import com.spidchenko.week2task.db.models.Favourite;
import com.spidchenko.week2task.helpers.ViewModelsFactory;
import com.spidchenko.week2task.network.models.Image;
import com.spidchenko.week2task.viewmodel.LoginViewModel;

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
    private final Boolean mIsNightMode = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
    private Boolean mIsTabletMode = false;
    private FragmentContainerView mDetailView;
    private FragmentManager mFragmentManager;
    private NavController mNavController;
    private LoginViewModel mLoginViewModel;

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

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        mNavController = Objects.requireNonNull(navHostFragment).getNavController();

        // Set up App Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(mNavController.getGraph())
                        .setOpenableLayout(findViewById(R.id.drawer_layout))
                        .build();
        NavigationUI.setupWithNavController(toolbar, mNavController, appBarConfiguration);

        // Set up Navigation drawer
        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, mNavController);

        if (findViewById(R.id.detail_content) != null) {
            Log.d(TAG, "onCreate: Now in TABLET mode");
            mIsTabletMode = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mDetailView = findViewById(R.id.detail_content);
        } else {
            Log.d(TAG, "onCreate: Now in PHONE mode");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, R.string.need_alert_permission, Toast.LENGTH_LONG).show();
            requestPermissionToLaunchOnBoot();
        }

        ViewModelsFactory factory = new ViewModelsFactory(getApplication());
        mLoginViewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

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

    // Action from search fragment
    @Override
    public void onImageClick(Image image, String searchString) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, image.getUrl(Image.PIC_SIZE_MEDIUM));
        bundle.putString(EXTRA_SEARCH_STRING, searchString);
        mNavController.navigate(R.id.action_searchFragment_to_imageViewerFragment, bundle);
    }

    @Override
    public void onSearchByCoordinatesAction(String lat, String lon) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_LATITUDE, lat);
        bundle.putString(EXTRA_LONGITUDE, lon);
        mNavController.navigate(R.id.action_mapsFragment_to_searchFragment,bundle);
    }

    @Override
    public void onTakePhotosAction() {
        mNavController.navigate(R.id.action_galleryFragment_to_cameraFragment);
    }

    @Override
    public void onOpenFavouriteAction(Favourite favourite) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, favourite.getUrl());
        bundle.putString(EXTRA_SEARCH_STRING, favourite.getSearchRequest());
        mNavController.navigate(R.id.action_favouritesFragment_to_imageViewerFragment, bundle);
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