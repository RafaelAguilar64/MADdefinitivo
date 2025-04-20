package com.ProyectoMAD

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class reportInfo : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 101
    private var latestLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_info)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar LocationManager
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Verificar permisos y obtener ubicación
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

        // Configurar el botón para subir datos a Firebase
        val reportButton = findViewById<Button>(R.id.reportButton)
        reportButton.setOnClickListener {
            uploadDataToFirebase()
        }
    }

    override fun onLocationChanged(location: Location) {
        latestLocation = location

        // Mostrar la ubicación en los TextViews
        val latitudeTextView: TextView = findViewById(R.id.latitudeText)
        val longitudeTextView: TextView = findViewById(R.id.longitudeText)

        latitudeTextView.text = "Latitude: ${location.latitude}"
        longitudeTextView.text = "Longitude: ${location.longitude}"

        Toast.makeText(this, "New location: ${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT).show()
    }

    private fun uploadDataToFirebase() {
        val latitude = latestLocation?.latitude
        val longitude = latestLocation?.longitude
        val information = findViewById<EditText>(R.id.Information).text.toString()

        if (latitude != null && longitude != null && information.isNotEmpty()) {
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("reports")

            val report = mapOf(
                "latitude" to latitude,
                "longitude" to longitude,
                "information" to information
            )

            ref.push().setValue(report).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Report uploaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to upload report", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Please wait for location or fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}