package com.spidchenko.week2task.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.spidchenko.week2task.R

class MapsFragment : Fragment(), OnMapReadyCallback {
    private var mGoogleMap: GoogleMap? = null
    private var mMarker: Marker? = null
    private var mListener: OnFragmentInteractionListener? = null
    private val mRequestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
            Log.d(TAG, "Permission callback! = $isGranted")
            if (isGranted) {
                location
            } else {
                Snackbar.make(
                    requireView(), R.string.need_location_permission,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener =
            if (context is OnFragmentInteractionListener) {
                context
            } else {
                throw ClassCastException(
                    context.toString()
                            + resources.getString(R.string.exception_message)
                )
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_maps, container, false)
        val btnSearchOnMap = rootView.findViewById<Button>(R.id.btn_map_search)
        btnSearchOnMap.setOnClickListener {
            if (mMarker == null) {
                Snackbar.make(
                    requireView(), R.string.select_point_message,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            } else {
                val coordinates = mMarker!!.position
                if (coordinates.latitude != 0.0 && coordinates.longitude != 0.0) {
                    mListener!!.onSearchByCoordinatesAction(
                        coordinates.latitude.toString(),
                        coordinates.longitude.toString()
                    )
                } else Log.e(TAG, "onCreateView: lat/lon = 0")
            }
        }
        val mapFragment = this.childFragmentManager
            .findFragmentById(R.id.google_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return rootView
    }

    override fun onMapReady(map: GoogleMap) {
        mGoogleMap = map
        mGoogleMap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
        mGoogleMap!!.setOnMapClickListener { latLng: LatLng -> setMarker(latLng) }
        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            location
        } else {
            Log.d(TAG, "Permission not granted! Trying to ask for...")
            mRequestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMarker(latLng: LatLng) {
        if (mMarker != null) {
            mMarker!!.remove()
        }
        mMarker = mGoogleMap!!.addMarker(
            MarkerOptions()
                .position(latLng)
        )
    }

    @get:SuppressLint("MissingPermission")
    private val location: Unit
        get() {
            mGoogleMap!!.isMyLocationEnabled = true
            try {
                val locationManager =
                    requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                var location: Location?
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location == null) {
                    location =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if (location == null) {
                        location =
                            locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                    }
                }
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    setMarker(latLng)
                } else {
                    setMarker(mGoogleMap!!.cameraPosition.target)
                    Snackbar.make(
                        requireView(), R.string.error_undefined_location,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                    Log.d(TAG, "getLocation: Error")
                }
            } catch (e: Exception) {
                Snackbar.make(
                    requireView(), R.string.error_default_message,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
                Log.d(TAG, "getLocation: Exception " + e.message)
            }
        }

    internal interface OnFragmentInteractionListener {
        fun onSearchByCoordinatesAction(lat: String?, lon: String?)
    }

    companion object {
        private const val TAG = "MapsFragment.LOG_TAG"
        const val EXTRA_LATITUDE = "com.spidchenko.week2task.extras.EXTRA_LATITUDE"
        const val EXTRA_LONGITUDE = "com.spidchenko.week2task.extras.EXTRA_LONGITUDE"
    }
}