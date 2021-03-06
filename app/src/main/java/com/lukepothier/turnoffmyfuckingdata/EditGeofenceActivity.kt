package com.lukepothier.turnoffmyfuckingdata

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_edit_geofence.*
import kotlinx.android.synthetic.main.content_edit_geofence.*
import android.text.InputFilter
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import android.support.v7.app.AlertDialog

class EditGeofenceActivity : AppCompatActivity() {

    private lateinit var geofence: Geofence
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_geofence)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        if (extras != null) {
            prefs = this.getSharedPreferences(resources.getString(R.string.preferences_filename), 0)
            val serializedGeofence = prefs.getString(extras.getString("geofenceId"), "")
            val gson = Gson()
            geofence = gson.fromJson<Geofence>(serializedGeofence, Geofence::class.java)

            editTextLocationName.setText(geofence.name)

            editTextLocationLatitude.setText(geofence.location.latitude.toString())
            editTextLocationLatitude.filters = arrayOf<InputFilter>(MinMaxInputFilter(-90, 90))

            editTextLocationLongitude.setText(geofence.location.longitude.toString())
            editTextLocationLongitude.filters = arrayOf<InputFilter>(MinMaxInputFilter(-180, 180))

            editTextLocationRadius.setText((geofence.radiusMetres.toString()))
            editTextLocationRadius.filters = arrayOf<InputFilter>(MinMaxInputFilter(0, 10000))

            checkboxReenableLocationOnLeave.isChecked = geofence.enableDataOnLeave
        }

        fabSave.setOnClickListener {
            val editor = prefs.edit()
            val newGeofence = Geofence()
            val newGeofenceName = editTextLocationName.text.toString()

            newGeofence.id = geofence.id
            newGeofence.name = newGeofenceName
            newGeofence.location = LatLng(editTextLocationLatitude.text.toString().toDouble(), editTextLocationLongitude.text.toString().toDouble())
            newGeofence.enableDataOnLeave = checkboxReenableLocationOnLeave.isChecked
            newGeofence.radiusMetres = editTextLocationRadius.text.toString().toInt()

            val gson = Gson().toJson(newGeofence)

            editor.putString(geofence.id.toString(), gson)
            editor.apply()

            Toast.makeText(this, "Location \"$newGeofenceName\" updated.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.buttonDelete) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Delete \"${geofence.name}\"?")
                    .setMessage("Are you sure you want to delete \"${geofence.name}\"?")
                    .setPositiveButton(android.R.string.yes, { _, _ ->
                        val editor = prefs.edit()
                        editor.remove(geofence.id.toString())
                        editor.apply()

                        Toast.makeText(this, "Location \"${geofence.name}\" removed.", Toast.LENGTH_SHORT).show()
                        finish()
                    })
                    .setNegativeButton(android.R.string.no, { _, _ ->
                    })
                    .show()
        }

        return super.onOptionsItemSelected(item)
    }
}
