package com.lukepothier.turnoffmyfuckingdata

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.IBinder
import android.os.Bundle
import android.util.Log
import android.net.ConnectivityManager
import android.content.ContentValues.TAG
import android.telephony.TelephonyManager
import android.os.Build
import java.lang.reflect.Method


private const val tag = "LocationService"
private const val LOCATION_INTERVAL = 5L
private const val LOCATION_DISTANCE = 10f

class LocationService : Service() {
    private var mLocationManager: LocationManager? = null

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
        try {
            mLocationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1])
        } catch (ex: java.lang.SecurityException) {
            Log.i(tag, "Failed to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(tag, "Network provider does not exist, ${ex.message}")
        }

        try {
            mLocationManager!!.requestLocationUpdates(
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
        if (mLocationManager != null) {
            for (i in mLocationListeners.indices) {
                try {
                    mLocationManager!!.removeUpdates(mLocationListeners[i])
                } catch (ex: Exception) {
                    Log.i(tag, "Failed to remove location listeners, ignore", ex)
                }

            }
        }
    }

    private fun initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    private fun setDataEnabled(location: Location)
    {
        val bv = Build.VERSION.SDK_INT

        try {
            when {
                bv == Build.VERSION_CODES.FROYO -> {
                    val dataConnSwitchMethod: Method
                    val telephonyManagerClass: Class<*>
                    val iTelephonyStub: Any
                    val iTelephonyClass: Class<*>

                    val telephonyManager = applicationContext
                            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                    telephonyManagerClass = Class.forName(telephonyManager.javaClass.name)
                    val getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony")
                    getITelephonyMethod.isAccessible = true
                    iTelephonyStub = getITelephonyMethod.invoke(telephonyManager)
                    iTelephonyClass = Class.forName(iTelephonyStub.javaClass.name)

                    if (false) {
                        dataConnSwitchMethod = iTelephonyClass
                                .getDeclaredMethod("enableDataConnectivity")
                    } else {
                        dataConnSwitchMethod = iTelephonyClass
                                .getDeclaredMethod("disableDataConnectivity")
                    }
                    dataConnSwitchMethod.isAccessible = true
                    dataConnSwitchMethod.invoke(iTelephonyStub)
                }
                bv >= Build.VERSION_CODES.LOLLIPOP -> {
                    val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    val methodSet = Class.forName(tm.javaClass.name).getDeclaredMethod("setDataEnabled", java.lang.Boolean.TYPE)
                    methodSet.invoke(tm, false)
                }
                else -> {
                    val conman = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val conmanClass = Class.forName(conman.javaClass.name)
                    val iConnectivityManagerField = conmanClass.getDeclaredField("mService")
                    iConnectivityManagerField.isAccessible = true
                    val iConnectivityManager = iConnectivityManagerField.get(conman)
                    val iConnectivityManagerClass = Class.forName(iConnectivityManager.javaClass.name)
                    val setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", java.lang.Boolean.TYPE)
                    setMobileDataEnabledMethod.isAccessible = true
                    setMobileDataEnabledMethod.invoke(iConnectivityManager, false)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting data enabled state", e)
        }
    }
}