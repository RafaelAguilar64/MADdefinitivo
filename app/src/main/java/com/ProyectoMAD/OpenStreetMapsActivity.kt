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



class OpenStreetMapsActivity : AppCompatActivity() {
    private val TAG = "btaOpenStreetMapActivity"
    private lateinit var map: org.osmdroid.views.MapView
    val gymkhanaCoords = listOf(
        GeoPoint(40.38779608214728, -3.627687914352839), // Tennis
        GeoPoint(40.38788595319803, -3.627048250272035), // Futsal outdoors
        GeoPoint(40.3887315224542, -3.628643539758645), // Fashion and design
        GeoPoint(40.38926842612264, -3.630067893975619), // Topos
        GeoPoint(40.38956358584258, -3.629046081389352), // Teleco
        GeoPoint(40.38992125672989, -3.6281366497769714), // ETSISI
        GeoPoint(40.39037466191718, -3.6270256763598447), // Library
        GeoPoint(40.389855884803005, -3.626782180787362) // CITSEM
    )
    val retiroMarkers = listOf(
        GeoPoint(40.42117535604317, -3.680401468693263), //Montaña artificial
        GeoPoint(40.42061417242487, -3.6810108904829124), //Casita del Pescador
        GeoPoint(40.418760474710574, -3.685308337528424), //Fuente de los Galápagos
        GeoPoint(40.41948863633951, -3.6872932972242913), //Paseo de México
        GeoPoint(40.41525783875198, -3.687802338144712), //Jardín del Parterre
        GeoPoint(40.4171343534008, -3.683909791107988), //Estanque Grande del Retiro
        GeoPoint(40.41209021987538, -3.6871366611675906), //Bosque del Recuerdo
        GeoPoint(40.40838225487468, -3.678519994560459), //Puerta de Mariano de Cavia
        GeoPoint(40.41079474417153, -3.680315379632557), //La Rosaleda (Cecilio Rodríguez)
        GeoPoint(40.41359592139659, -3.6819739132752844), //Palacio de Cristal
        GeoPoint(40.41330744341854, -3.678005171232507), //Jardines de Cecilio Rodríguez
        GeoPoint(40.415571513393296, -3.6787337586579363) //Jardines de Herrero de Palacios
    )
    val retiroNames = listOf(
        "Montaña artificial",
        "Casita del Pescador",
        "Fuente de los Galápagos",
        "Paseo de México",
        "Jardín del Parterre",
        "Estanque Grande del Retiro",
        "Bosque del Recuerdo",
        "Puerta de Mariano de Cavia",
        "La Rosaleda (Cecilio Rodríguez)",
        "Palacio de Cristal",
        "Jardines de Cecilio Rodríguez",
        "Jardines de Herrero de Palacios"
    )
    val berroMarkers = listOf(
        GeoPoint(40.422598290025114, -3.6627296111863354), //Centro cultural Quinta del Berro
        GeoPoint(40.42207086085666, -3.6602394414882493) //Ginko Biloba
    )
    val berroNames = listOf(
        "Centro cultural Quinta del Berro",
        "Ginko Biloba"
    )
    val gymkhanaNames = listOf(
        "Tennis",
        "Futsal outdoors",
        "Fashion and design school",
        "Topography school",
        "Telecommunications school",
        "ETSISI",
        "Library",
        "CITSEM"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate: Starting activity...");

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

        addGymkhanaMarkers(map, gymkhanaCoords, gymkhanaNames, this)
        addGymkhanaMarkers(map, retiroMarkers, retiroNames, this)
        addGymkhanaMarkers(map, berroMarkers, berroNames, this)

        val routeMarker = Marker(map)
        routeMarker.position = startPoint
        routeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        routeMarker.icon = ContextCompat.getDrawable(this, android.R.drawable.ic_delete) as BitmapDrawable
        routeMarker.title = "My current location"
        map.overlays.add(routeMarker)

        addRouteMarkers(map, gymkhanaCoords, gymkhanaNames, this)
        addRouteMarkers(map, berroMarkers, berroNames, this)

        map.controller.setZoom(18.0)
    }
    fun addGymkhanaMarkers(map: MapView, coords: List<GeoPoint>, names: List<String>, context: Context) {
        for (i in coords.indices) {
            val marker = Marker(map)
            marker.position = coords[i]
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_compass) as BitmapDrawable
            marker.title = names[i]
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


}