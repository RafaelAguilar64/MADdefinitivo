package com.ProyectoMAD

import android.content.Intent
import android.widget.Button
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.AlertDialog
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity(), LocationListener {
    private val TAG = "btaMainActivity"
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    var latestLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: Starting main activity.")


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val buttonOsm: Button = findViewById(R.id.osmButton)
        buttonOsm.setOnClickListener {
            Log.d(
                TAG,
                "Sending location to OpenStreetMapsActivity: ${latestLocation?.latitude}, ${latestLocation?.longitude}"
            )
            val intent = Intent(this, OpenStreetMapsActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("location", latestLocation)
            intent.putExtra("locationBundle", bundle)
            startActivity(intent)
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationPermissionCode
            )
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }

        // Set up button click listener
        val navigateButton: Button = findViewById(R.id.navigateButton)
        navigateButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("location", latestLocation)
            intent.putExtra("locationBundle", bundle)
            startActivity(intent)
        }
        val userIdentifierButton: Button = findViewById(R.id.userIdentifierButton)
        userIdentifierButton.setOnClickListener {
            showUserIdentifierDialog()
        }
        val userIdentifier = getUserIdentifier()
        if (userIdentifier == null) {
            // If not, ask for it
            showUserIdentifierDialog()
        } else {
            // If yes, use it or show it
            Toast.makeText(this, "User ID: $userIdentifier", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val textView: TextView = findViewById(R.id.mainTextView)
        val locationText = getString(R.string.location_text, location.latitude, location.longitude)
        textView.text = locationText
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    private fun showUserIdentifierDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter User Identifier")
        val input = EditText(this)
        val userIdentifier = getUserIdentifier()
        if (userIdentifier != null) {
            input.setText(userIdentifier)
        }
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, which ->
            val userInput = input.text.toString()
            if (userInput.isNotBlank()) {
                saveUserIdentifier(userInput)
                Toast.makeText(this, "User ID saved: $userInput", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "User ID cannot be blank", Toast.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            Toast.makeText(this, "Thanks and goodbye!", Toast.LENGTH_LONG).show()
            dialog.cancel()
        }
        builder.show()
    }

    private fun saveUserIdentifier(userIdentifier: String) {
        val sharedPreferences = this.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("userIdentifier", userIdentifier)
            apply()
        }
    }
    private fun getUserIdentifier(): String? {
        val sharedPreferences = this.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userIdentifier", null)
    }

}