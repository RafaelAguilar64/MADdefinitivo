package com.ProyectoMAD

import android.location.Location
import android.util.Log
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import org.osmdroid.views.overlay.Polyline
import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.ProyectoMAD.network.WeatherApiService
import com.ProyectoMAD.network.WeatherResponse
import com.ProyectoMAD.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.android.gms.location.LocationServices
import android.Manifest
import android.content.pm.PackageManager
import kotlin.collections.get
import kotlin.compareTo


class OpenStreetMapsActivity : AppCompatActivity() {
    private val TAG = "btaOpenStreetMapActivity"
    private lateinit var map: org.osmdroid.views.MapView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate: Starting activity...");
        Toast.makeText(this, "Welcome to the map", Toast.LENGTH_LONG).show()


        enableEdgeToEdge()
        setContentView(R.layout.activity_open_street_maps)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Configuration.getInstance().userAgentValue = "com.ProyectoMAD"
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

        val bundle = intent.getBundleExtra("locationBundle")
        val location: Location? = bundle?.getParcelable("location")
        var map: MapView = findViewById(R.id.map)

        map.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)

        val startPoint = if (location != null) {
            Log.d(TAG, "onCreate: Location[${location.altitude}][${location.latitude}][${location.longitude}]")
            GeoPoint(location.latitude, location.longitude)
        } else {
            Log.d(TAG, "onCreate: Location is null, using default coordinates")
            GeoPoint(40.389683644051864, -3.627825356970311)
        }
        val userMarker = Marker(map)
        userMarker.position = startPoint
        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        userMarker.icon = ContextCompat.getDrawable(this, android.R.drawable.ic_menu_compass) as BitmapDrawable
        userMarker.title = "My current location"
        map.overlays.add(userMarker)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(15.0)
        map.controller.setCenter(startPoint)

        val gymkhanaMarker = Marker(map)
        gymkhanaMarker.position = startPoint
        gymkhanaMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        gymkhanaMarker.icon = ContextCompat.getDrawable(this, android.R.drawable.ic_delete) as BitmapDrawable
        gymkhanaMarker.title = "My current location"
        map.overlays.add(gymkhanaMarker)


        val routeMarker = Marker(map)
        routeMarker.position = startPoint
        routeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        routeMarker.icon = ContextCompat.getDrawable(this, android.R.drawable.ic_delete) as BitmapDrawable
        routeMarker.title = "My current location"
        map.overlays.add(routeMarker)

        loadDatabaseMarkers()

        val buttonComprobar: Button = findViewById(R.id.comprobarCoordenadas)
        buttonComprobar.setOnClickListener {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Obtener la ubicación actual
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.d(TAG, "Ubicación actual: Lat=$latitude, Lon=$longitude")

                        // Llamar a la API con las coordenadas obtenidas
                        val apiKey = "04368c208661530d8b90a96114b2487b"
                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://api.openweathermap.org/data/2.5/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                        val service = retrofit.create(WeatherApiService::class.java)

                        val call = service.getWeatherForecast(latitude, longitude, 1, apiKey)
                        call.enqueue(object : Callback<WeatherResponse> {
                            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                                if (response.isSuccessful) {
                                    val weatherResponse = response.body()
                                    weatherResponse?.let {
                                        val tempCelsius = it.list[0].main.temp - 273.15 // Convertir de Kelvin a Celsius
                                        val humidity = it.list[0].main.humidity

                                        // Verificar las condiciones
                                        if (tempCelsius in 12.0..18.0 && humidity >= 60) {
                                            Toast.makeText(this@OpenStreetMapsActivity, "Se dan las condiciones necesarias", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this@OpenStreetMapsActivity, "No se dan las condiciones necesarias", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Log.e(TAG, "Error en la respuesta: ${response.code()}")
                                    Toast.makeText(this@OpenStreetMapsActivity, "Error al obtener datos del clima", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                                Log.e(TAG, "Error en la petición: ${t.message}")
                                Toast.makeText(this@OpenStreetMapsActivity, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Log.e(TAG, "Error al obtener la ubicación: ${it.message}")
                    Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Solicitar permisos si no están concedidos
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1001)
            }
        }



        map.controller.setZoom(18.0)
    }

    private fun getApiData(lat: Double, lon: Double) {
        val apiKey = "04368c208661530d8b90a96114b2487b"
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WeatherApiService::class.java)

        val call = service.getWeatherForecast(lat, lon, 1, apiKey)
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    weatherResponse?.let {
                        Log.d(TAG, "Datos del clima: $it")
                        Toast.makeText(this@OpenStreetMapsActivity, "Clima obtenido: ${it.list[0].main.temp}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta: ${response.code()}")
                    Toast.makeText(this@OpenStreetMapsActivity, "Error al obtener datos del clima", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e(TAG, "Error en la petición: ${t.message}")
                Toast.makeText(this@OpenStreetMapsActivity, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadDatabaseMarkers() {
        val db = AppDatabase.getDatabase(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val dbCoordinates = db.coordinatesDao().getAll()
            val roomGeoPoints = dbCoordinates.map {
                GeoPoint(it.latitude, it.longitude)
            }
            Log.d(TAG, "Coordenadas obtenidas de Room: $roomGeoPoints")
            withContext(Dispatchers.Main) {
                //addDatabaseMarkers(map, roomGeoPoints, this@OpenStreetMapsActivity)
            }
        }
    }

    private fun addDatabaseMarkers(map: MapView, coords: List<GeoPoint>, context: Context) {
        for (geoPoint in coords) {
            val marker = Marker(map)
            marker.position = geoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_delete) as BitmapDrawable
            marker.title = "Saved Coordinate"
            map.overlays.add(marker)
        }
    }

    fun addRouteMarkers(map: MapView, coords: List<GeoPoint>, names: List<String>, context: Context) {
        val polyline = Polyline()
        polyline.setPoints(coords)
        for (i in coords.indices) {
            val marker = Marker(map)
            marker.position = coords[i]
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_compass) as BitmapDrawable
            marker.title = names[i]
            map.overlays.add(marker)
        }
        map.overlays.add(polyline) //test
    }
    override fun onBackPressed() {
        super.onBackPressed()
        onBackPressedDispatcher.onBackPressed()
        finish()
    }

}