package com.lukepothier.turnoffmyfuckingdata

import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.content.pm.PackageManager
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.support.v4.content.ContextCompat
import android.Manifest
import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_maps.*
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.R.attr.password
import android.view.KeyEvent


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var locationManager: LocationManager? = null
    private var locationRequestCode = 1

    private val locationListener: LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            val currentLocation = LatLng(location.latitude, location.longitude)

            fabAddMapLocation.isEnabled = true
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 19f))
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fabAddMapLocation.isEnabled = false

        fabAddMapLocation.setOnClickListener {
            Toast.makeText(this,
                    "Click on the map to create a location",
                    Toast.LENGTH_LONG).show()
            mMap.setOnMapClickListener { latLng -> createLocation(latLng) }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    locationRequestCode)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 1000000f, locationListener)
        } catch (ex: SecurityException) {
            Log.d("location", "Security Exception, no location available")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            locationRequestCode -> {

                if (grantResults.isEmpty() || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,
                            "Unable to show location - permission required",
                            Toast.LENGTH_LONG).show()
                } else {

                    val mapFragment = supportFragmentManager
                            .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
        }
    }

    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode)
    }

    private fun createLocation(latLng: LatLng) {
        mMap.setOnMapClickListener { null }
        mMap.addMarker(MarkerOptions().position(latLng).title("" + latLng.longitude + ":" + latLng.latitude))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f))

        fabAddMapLocation.visibility = View.VISIBLE
        fabConfirmLocation.visibility = View.VISIBLE

        editTextLocationName.visibility = View.VISIBLE
        editTextLocationName.requestFocus()

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editTextLocationName, InputMethodManager.SHOW_IMPLICIT)

        editTextLocationName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                goToConfirmation(latLng, editTextLocationName.text.toString())
                true
            } else false
        }

        fabConfirmLocation.setOnClickListener {
            goToConfirmation(latLng, editTextLocationName.text.toString())
        }
    }

    private fun goToConfirmation(latLng: LatLng, locationName: String) {
        Log.d("turnoffmyfuckingdata", "" + latLng.longitude + ":" + latLng.latitude + ":" + locationName)
        startActivity(Intent(this, MainActivity::class.java))
    }
}