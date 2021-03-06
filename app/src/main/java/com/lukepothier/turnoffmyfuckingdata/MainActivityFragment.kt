package com.lukepothier.turnoffmyfuckingdata

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainActivityFragment : Fragment() {

    private val locations: ArrayList<Geofence> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        populateLocationsList()
        view?.recyclerViewLocations?.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        view?.recyclerViewLocations?.adapter = LocationsAdapter(locations)
    }

    private fun populateLocationsList() {

        locations.clear()

        val prefs = context!!.getSharedPreferences(resources.getString(R.string.preferences_filename), 0)
        val allGeofences = prefs.all

        for (geofence in allGeofences) {
            val gson = Gson()
            val json = prefs.getString(geofence.key, "")
            val deserializedGeofence = gson.fromJson<Geofence>(json, Geofence::class.java)
            locations.add(deserializedGeofence)
        }
    }
}
