package com.ProyectoMAD

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.IOException

class SecondActivity : AppCompatActivity() {
    private val TAG = "btaMainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonToMain: Button = findViewById(R.id.buttonToMain)
        buttonToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val buttonToThird: Button = findViewById(R.id.buttonToThird)
        buttonToThird.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
            finish()
        }
        val bundle = intent.getBundleExtra("locationBundle")
        val location: Location? = bundle?.getParcelable<Location>("location")

        if (location != null) {
            Log.i(TAG, "onCreate: Location["+location.altitude+"]["+location.latitude+"]["+location.longitude+"][")
        }
        val tvFileContents: TextView = findViewById(R.id.tvFileContents)
        tvFileContents.text = readFileContents()

    }
    private fun readFileContents(): String {
        val fileName = "gps_coordinates.csv"
        return try {
            // Open the file from internal storage
            openFileInput(fileName).bufferedReader().useLines { lines ->
                lines.fold("") { some, text ->
                    "$some\n$text"
                }
            }
        } catch (e: IOException) {
            "Error reading file: ${e.message}"
        }
    }
}