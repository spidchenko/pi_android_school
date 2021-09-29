package com.spidchenko.week2task.ui

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.spidchenko.week2task.BatteryLevelReceiver
import com.spidchenko.week2task.R
import com.spidchenko.week2task.db.models.Favourite
import com.spidchenko.week2task.db.models.SyncImage
import com.spidchenko.week2task.helpers.ViewModelsFactory
import com.spidchenko.week2task.network.models.Image
import com.spidchenko.week2task.viewmodel.LoginViewModel
import java.util.*

class MainActivity : AppCompatActivity(), LoginFragment.OnFragmentInteractionListener,
    SearchFragment.OnFragmentInteractionListener, MapsFragment.OnFragmentInteractionListener,
    GalleryFragment.OnFragmentInteractionListener, FavouritesFragment.OnFragmentInteractionListener,
    SyncImagesFragment.OnFragmentInteractionListener {
    private val mBatteryLevelReceiver: BroadcastReceiver = BatteryLevelReceiver()
    private var mDetailView: FragmentContainerView? = null
    private var mNavController: NavController? = null
    private var mLoginViewModel: LoginViewModel? = null
    private val mRequestPermissionLauncher =
        registerForActivityResult(StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Snackbar.make(
                        findViewById(android.R.id.content), R.string.need_alert_permission,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        findViewById(android.R.id.content), R.string.alert_permission_ok,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setNightMode()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        mNavController = Objects.requireNonNull(navHostFragment)?.navController

        // Set up App Bar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val appBarConfiguration = AppBarConfiguration.Builder(mNavController!!.graph)
            .setOpenableLayout(findViewById(R.id.drawer_layout))
            .build()
        NavigationUI.setupWithNavController(toolbar, mNavController!!, appBarConfiguration)

        // Set up Navigation drawer
        val navView = findViewById<NavigationView>(R.id.nav_view)
        NavigationUI.setupWithNavController(navView, mNavController!!)
        if (findViewById<View?>(R.id.detail_content) != null) {
            Log.d(TAG, "onCreate: Now in TABLET mode")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            mDetailView = findViewById(R.id.detail_content)
        } else {
            Log.d(TAG, "onCreate: Now in PHONE mode")
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, R.string.need_alert_permission, Toast.LENGTH_LONG).show()
            requestPermissionToLaunchOnBoot()
        }
        val factory = ViewModelsFactory(application)
        mLoginViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
    }

    private fun setNightMode() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isNightMode = sharedPreferences.getBoolean(SettingsFragment.PREF_NIGHT_MODE, false)
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onResume() {
        super.onResume()
        startReceivingBatteryLevelUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopReceivingBatteryLevelUpdates()
    }

    private fun startReceivingBatteryLevelUpdates() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        this.registerReceiver(mBatteryLevelReceiver, filter)
    }

    private fun stopReceivingBatteryLevelUpdates() {
        unregisterReceiver(mBatteryLevelReceiver)
    }

    override fun hideKeyboard() {
        val view = findViewById<View>(android.R.id.content)
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun requestPermissionToLaunchOnBoot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            mRequestPermissionLauncher.launch(intent)
        }
    }

    // Action from search fragment
    override fun onImageClick(image: Image, searchString: String) {
        val bundle = Bundle()
        bundle.putString(EXTRA_URL, image.getUrl(Image.PIC_SIZE_MEDIUM))
        bundle.putString(EXTRA_SEARCH_STRING, searchString)
        mNavController!!.navigate(R.id.action_searchFragment_to_imageViewerFragment, bundle)
    }

    override fun onSearchByCoordinatesAction(lat: String?, lon: String?) {
        val bundle = Bundle()
        bundle.putString(MapsFragment.EXTRA_LATITUDE, lat)
        bundle.putString(MapsFragment.EXTRA_LONGITUDE, lon)
        mNavController!!.navigate(R.id.action_mapsFragment_to_searchFragment, bundle)
    }

    override fun onTakePhotosAction() {
        mNavController!!.navigate(R.id.action_galleryFragment_to_cameraFragment)
    }

    override fun onOpenFavouriteAction(favourite: Favourite?) {
        val bundle = Bundle()
        bundle.putString(EXTRA_URL, favourite!!.url)
        bundle.putString(EXTRA_SEARCH_STRING, favourite.searchRequest)
        mNavController!!.navigate(R.id.action_favouritesFragment_to_imageViewerFragment, bundle)
    }

    override fun onOpenImageAction(image: SyncImage) {
        val bundle = Bundle()
        bundle.putString(EXTRA_URL, image.url)
        bundle.putString(EXTRA_SEARCH_STRING, image.searchText)
        mNavController!!.navigate(R.id.action_syncImagesFragment_to_imageViewerFragment, bundle)
    }

    companion object {
        private const val TAG = "MainActivity.LOG_TAG"
        const val EXTRA_URL = "com.spidchenko.week2task.extras.EXTRA_URL"
        const val EXTRA_SEARCH_STRING = "com.spidchenko.week2task.extras.EXTRA_SEARCH_STRING"
    }
}