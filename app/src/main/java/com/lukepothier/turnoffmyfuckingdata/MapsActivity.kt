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
import kotlinx.android.synthetic.main.activity_maps.*
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.res.ResourcesCompat
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.EditorInfo
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import com.google.gson.Gson
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var locationManager: LocationManager? = null
    private var locationRequestCode = 1
    private var isAddingLocation = false
    private lateinit var marker: Marker

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

        try {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 1000000f, locationListener)
        } catch (ex: SecurityException) {
            Log.d("LocationManager", "Security Exception, no location available")
        }

        fabAddMapLocation.isEnabled = false

        fabAddMapLocation.setOnClickListener {
            isAddingLocation = true
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

        val prefs = this.getSharedPreferences(resources.getString(R.string.preferences_filename), 0)
        val allGeofences = prefs.all

        for (geofence in allGeofences) {
            val gson = Gson()
            val json = prefs.getString(geofence.key, "")
            val deserializedGeofence = gson.fromJson<Geofence>(json, Geofence::class.java)
            mMap.addMarker(MarkerOptions().position(deserializedGeofence.location).title(deserializedGeofence.name))
            mMap.addCircle(CircleOptions()
                    .center(deserializedGeofence.location)
                    .radius(deserializedGeofence.radiusMetres.toDouble())
                    .strokeColor(ResourcesCompat.getColor(resources, R.color.colorGreen, null))
                    .fillColor(ResourcesCompat.getColor(resources, R.color.colorGreenSemiTransparent, null)))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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

    override fun onBackPressed() {
        if (isAddingLocation)
            cancel()
        else
            super.onBackPressed()
    }

    private fun requestPermission(permissionType: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permissionType), requestCode)
    }

    private fun createLocation(latLng: LatLng) {
        mMap.setOnMapClickListener { null }
        marker = mMap.addMarker(MarkerOptions().position(latLng).title("" + latLng.longitude + ":" + latLng.latitude))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19f))

        fabAddMapLocation.visibility = View.GONE
        fabConfirmLocation.visibility = View.VISIBLE

        editTextLocationName.visibility = View.VISIBLE
        editTextLocationName.requestFocus()

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editTextLocationName, InputMethodManager.SHOW_IMPLICIT)

        editTextLocationName.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    goToConfirmation(latLng, editTextLocationName.text.toString())
                    true
                }
                else -> false
            }
        }

        (editTextLocationName as CancellableEditText)
            .setOnBackButtonListener(object : CancellableEditText.IOnBackButtonListener {
                override fun onEditTextBackButton(): Boolean {
                    cancel()
                    return true
                }
            })

        fabConfirmLocation.setOnClickListener {
            goToConfirmation(latLng, editTextLocationName.text.toString())
        }
    }

    private fun cancel() {
        marker.remove()
        isAddingLocation = false

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromInputMethod(editTextLocationName.windowToken, 0)
        editTextLocationName.visibility = View.GONE
        editTextLocationName.setText("")
        fabAddMapLocation.visibility = View.VISIBLE
        fabConfirmLocation.visibility = View.GONE
    }

    private fun goToConfirmation(latLng: LatLng, locationName: String) {
        val prefs = this.getSharedPreferences(resources.getString(R.string.preferences_filename), 0)
        val editor = prefs.edit()
        val geofence = Geofence()
        val id = UUID.randomUUID()

        geofence.id = id
        geofence.name = locationName
        geofence.location = latLng
        geofence.enableDataOnLeave = true
        geofence.radiusMetres = 50

        val gson = Gson().toJson(geofence)

        editor.putString(id.toString(), gson)
        editor.apply()

        startActivity(Intent(this, MainActivity::class.java))
    }
}
