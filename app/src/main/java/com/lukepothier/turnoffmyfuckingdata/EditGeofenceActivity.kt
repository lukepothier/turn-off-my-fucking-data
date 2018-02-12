package com.lukepothier.turnoffmyfuckingdata

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_edit_geofence.*
import kotlinx.android.synthetic.main.content_edit_geofence.*
import android.text.InputFilter

class EditGeofenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_geofence)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        if (extras != null) {
            val prefs = this.getSharedPreferences(resources.getString(R.string.preferences_filename), 0)
            val geofence = prefs.getString(extras.getString("geofenceId"), "")
            val gson = Gson()
            val deserializedGeofence = gson.fromJson<Geofence>(geofence, Geofence::class.java)

            editTextLocationName.setText(deserializedGeofence.name)

            editTextLocationLatitude.setText(deserializedGeofence.location.latitude.toString())
            editTextLocationLatitude.filters = arrayOf<InputFilter>(MinMaxInputFilter(-90, 90))

            editTextLocationLongitude.setText(deserializedGeofence.location.longitude.toString())
            editTextLocationLongitude.filters = arrayOf<InputFilter>(MinMaxInputFilter(-180, 180))

            editTextLocationRadius.setText((deserializedGeofence.radiusMetres.toString()))
            editTextLocationRadius.filters = arrayOf<InputFilter>(MinMaxInputFilter(0, 10000))
        }

        fabSave.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}
