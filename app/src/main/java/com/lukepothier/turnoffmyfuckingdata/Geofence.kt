package com.lukepothier.turnoffmyfuckingdata

import com.google.android.gms.maps.model.LatLng
import java.util.*

class Geofence {
    lateinit var id: UUID
    lateinit var name: String
    lateinit var location: LatLng
    var enableDataOnLeave: Boolean = false
    var radiusMetres: Int = 50
}
