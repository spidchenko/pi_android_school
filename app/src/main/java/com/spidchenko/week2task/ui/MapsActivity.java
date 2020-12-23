package com.spidchenko.week2task.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity.LOG_TAG";
    private static final int REQUEST_LOCATION_PERMISSION = 5;
    public static final String EXTRA_LATITUDE = "com.spidchenko.week2task.extras.EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "com.spidchenko.week2task.extras.EXTRA_LONGITUDE";

    private GoogleMap mGoogleMap;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.setOnMapClickListener(this::setMarker);
        getLocation();
    }

    private void setMarker(LatLng latLng) {
        if (mMarker != null) {
            mMarker.remove();
        }
        mMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    private void getLocation() {
        // TODO: 12/22/20 you can access lat & lng from camera instance
        //  e.g. mGoogleMap.getCameraPosition().target
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleMap != null) {
                mGoogleMap.setMyLocationEnabled(true);
            }
            try {
                LocationManager locationManager =
                        (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                Location location;
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location == null) {
                        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                }
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    setMarker(latLng);
                } else {
                    showSnackBarMessage(R.string.error_undefined_location);
                    Log.d(TAG, "getLocation: Error");
                }
            } catch (Exception e) {
                showSnackBarMessage(R.string.error_default_message);
                Log.d(TAG, "getLocation: Exception " + e.getMessage());
            }

        } else {
            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: permission denied!");
            }
        }
    }

    public void actionSearchByCoordinates(View view) {
        if (mMarker == null) {
            showSnackBarMessage(R.string.select_point_message);
        } else {
            Log.d(TAG, "actionSearchByCoordinates: " + mMarker.getPosition().toString());
            LatLng coordinates = mMarker.getPosition();
            Intent replyIntent = new Intent();
            replyIntent.putExtra(EXTRA_LATITUDE, Double.toString(coordinates.latitude));
            replyIntent.putExtra(EXTRA_LONGITUDE, Double.toString(coordinates.longitude));
            setResult(RESULT_OK, replyIntent);
            finish();
        }
    }

    private void showSnackBarMessage(@StringRes int resourceId) {
        Snackbar.make(findViewById(android.R.id.content),
                resourceId,
                BaseTransientBottomBar.LENGTH_LONG).show();
    }

}