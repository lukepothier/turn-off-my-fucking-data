package com.lukepothier.turnoffmyfuckingdata

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fabAddLocation.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        // Start LocationService
        this.startService(Intent(this, LocationService::class.java))
    }
}
