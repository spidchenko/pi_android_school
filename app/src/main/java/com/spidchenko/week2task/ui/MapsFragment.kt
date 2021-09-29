package com.spidchenko.week2task.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.spidchenko.week2task.R;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapsFragment.LOG_TAG";
    public static final String EXTRA_LATITUDE = "com.spidchenko.week2task.extras.EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "com.spidchenko.week2task.extras.EXTRA_LONGITUDE";

    private GoogleMap mGoogleMap;
    private Marker mMarker;

    private OnFragmentInteractionListener mListener;

    private final ActivityResultLauncher<String> mRequestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d(TAG, "Permission callback! = " + isGranted);
                if (isGranted) {
                    getLocation();
                } else {
                    Snackbar.make(requireView(), R.string.need_location_permission,
                            BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + getResources().getString(R.string.exception_message));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        Button btnSearchOnMap = rootView.findViewById(R.id.btn_map_search);

        btnSearchOnMap.setOnClickListener(view -> {
            if (mMarker == null) {
                Snackbar.make(requireView(), R.string.select_point_message,
                        BaseTransientBottomBar.LENGTH_LONG).show();
            } else {
                LatLng coordinates = mMarker.getPosition();
                if ((coordinates.latitude != 0) && (coordinates.longitude != 0)) {
                    mListener.onSearchByCoordinatesAction(
                            Double.toString(coordinates.latitude),
                            Double.toString(coordinates.longitude));
                } else
                    Log.e(TAG, "onCreateView: lat/lon = 0");
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.google_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mGoogleMap.setOnMapClickListener(this::setMarker);

        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            Log.d(TAG, "Permission not granted! Trying to ask for...");
            mRequestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

    }

    private void setMarker(LatLng latLng) {
        if (mMarker != null) {
            mMarker.remove();
        }
        mMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng));
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        mGoogleMap.setMyLocationEnabled(true);
        try {
            LocationManager locationManager =
                    (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

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
                setMarker(mGoogleMap.getCameraPosition().target);
                Snackbar.make(requireView(), R.string.error_undefined_location,
                        BaseTransientBottomBar.LENGTH_LONG).show();
                Log.d(TAG, "getLocation: Error");
            }
        } catch (Exception e) {
            Snackbar.make(requireView(), R.string.error_default_message,
                    BaseTransientBottomBar.LENGTH_LONG).show();
            Log.d(TAG, "getLocation: Exception " + e.getMessage());
        }

    }

    interface OnFragmentInteractionListener {
        void onSearchByCoordinatesAction(String lat, String lon);
    }
}