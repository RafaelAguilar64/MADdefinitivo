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
import android.widget.Switch
import android.widget.Toast
import java.io.File
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.core.view.GravityCompat


class MainActivity : AppCompatActivity(), LocationListener, NavigationView.OnNavigationItemSelectedListener {
    private val TAG = "btaMainActivity"
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private lateinit var locationSwitch: Switch

    private lateinit var drawerLayout: DrawerLayout

    var latestLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate: Starting main activity.")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        locationSwitch = findViewById(R.id.locationSwitch)
        locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                locationSwitch.text = "Disable location"
                startLocationUpdates()
            } else {
                locationSwitch.text = "Enable location"
                stopLocationUpdates()
            }
        }


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
            finish()
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
    private fun startLocationUpdates() {
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
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionCode
            )
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
    }
    private fun stopLocationUpdates() {
        locationManager.removeUpdates(this)
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
        latestLocation = location
        val textView: TextView = findViewById(R.id.mainTextView)
        val locationText = getString(R.string.location_text, location.latitude, location.longitude)
        textView.text = locationText
        saveCoordinatesToFile(location.latitude, location.longitude, location.altitude, System.currentTimeMillis())
        val toastText = "New location: ${location.latitude}, ${location.longitude}, ${location.altitude}"
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle home action
            }
            R.id.nav_second_activity -> {
                val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_open_street_map -> {
                val intent = Intent(this, OpenStreetMapsActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("location", latestLocation)
                intent.putExtra("locationBundle", bundle)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    private fun saveCoordinatesToFile(latitude: Double, longitude: Double, altitude: Double, timestamp: Long) {
        val fileName = "gps_coordinates.csv"
        val file = File(filesDir, fileName)
        val formattedLatitude = String.format("%.4f", latitude)
        val formattedLongitude = String.format("%.4f", longitude)
        val formattedAltitude = String.format("%.2f", altitude)
        file.appendText("$timestamp;$formattedLatitude;$formattedLongitude;$formattedAltitude\n")
    }

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




    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}


}