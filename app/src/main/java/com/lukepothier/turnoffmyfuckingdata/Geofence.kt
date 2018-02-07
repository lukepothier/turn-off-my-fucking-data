package com.lukepothier.turnoffmyfuckingdata

import com.google.android.gms.maps.model.LatLng

class Geofence {
    lateinit var name: String
    lateinit var location: LatLng
    var enableDataOnLeave: Boolean = false
    var radiusMetres: Int = 50
}