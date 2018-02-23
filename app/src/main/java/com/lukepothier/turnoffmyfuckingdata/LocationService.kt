package com.lukepothier.turnoffmyfuckingdata

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.Bundle
import android.util.Log
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

private const val tag = "TOMFD::LocationService"
private const val LOCATION_INTERVAL = 5L
private const val LOCATION_DISTANCE = 10f

class LocationService : Service() {
    private var locationManager: LocationManager? = null
    private var notificationManager: NotificationManager? = null

    inner class LocationListener(provider: String) : android.location.LocationListener {

        override fun onLocationChanged(location: Location) {
            setDataEnabled(location)
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    private var mLocationListeners = arrayOf(LocationListener(LocationManager.GPS_PROVIDER), LocationListener(LocationManager.NETWORK_PROVIDER))

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onCreate() {
        initializeLocationManager()
        initializeNotificationManager()
        try {
            locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1])
        } catch (ex: java.lang.SecurityException) {
            Log.i(tag, "Failed to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(tag, "Network provider does not exist, ${ex.message}")
        }

        try {
            locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0])
        } catch (ex: java.lang.SecurityException) {
            Log.i(tag, "Failed to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(tag, "GPS provider does not exist, ${ex.message}")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) {
            for (i in mLocationListeners.indices) {
                try {
                    locationManager!!.removeUpdates(mLocationListeners[i])
                } catch (ex: Exception) {
                    Log.i(tag, "Failed to remove location listeners, ignore", ex)
                }

            }
        }
    }

    private fun initializeLocationManager() {
        if (locationManager == null) {
            locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    private fun initializeNotificationManager() {
        if (notificationManager == null) {
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }

    private fun setDataEnabled(location: Location)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val mBuilder = NotificationCompat.Builder(this, "TURN_OFF_MY_FUCKING_DATA")
                .setChannelId(getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.ic_place_primary_24dp)
                .setContentTitle("Test")
                .setContentText("${location.latitude} - ${location.longitude}")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setAutoCancel(false)

        val notificationManager = NotificationManagerCompat.from(this)

        notificationManager.notify(1, mBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val id = getString(R.string.notification_channel_id)
        val name = getString(R.string.notification_channel_name)
        val description = getString(R.string.notification_channel_description)

        val importance = NotificationManager.IMPORTANCE_HIGH

        val mChannel = NotificationChannel(id, name, importance)

        mChannel.description = description
        mChannel.enableLights(true)
        mChannel.lightColor = Color.WHITE
        mChannel.enableVibration(true)
        mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

        mNotificationManager.createNotificationChannel(mChannel)
    }
}
