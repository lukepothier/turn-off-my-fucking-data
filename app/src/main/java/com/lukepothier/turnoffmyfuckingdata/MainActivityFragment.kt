package com.lukepothier.turnoffmyfuckingdata

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_main.view.*

class MainActivityFragment : Fragment() {

    private val locations: ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        addAnimals()
        view.recyclerViewLocations.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        view.recyclerViewLocations.adapter = LocationsAdapter(locations)
        return view
    }

    private fun addAnimals() {
        locations.add("place 1")
        locations.add("place 2")
        locations.add("place 3")
        locations.add("place 4")
        locations.add("place 5")
    }
}
