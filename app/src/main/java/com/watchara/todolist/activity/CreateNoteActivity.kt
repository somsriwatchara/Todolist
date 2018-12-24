package com.watchara.todolist.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.watchara.todolist.R
import com.watchara.todolist.R.id.save_data
import kotlinx.android.synthetic.main.activity_create_note.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateNoteActivity : AppCompatActivity() {


    private val TAG = "LocationProvider"

    private var lat: String? = null
    private var lng: String? = null
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    private var client: FusedLocationProviderClient? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        client = LocationServices.getFusedLocationProviderClient(this)


        setupPermissions()

    }


    private fun setupPermissions() {
        val permissionFine = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val permissionCoarse = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (permissionFine != PackageManager.PERMISSION_GRANTED && permissionCoarse != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to location denied")
            makeRequest()
        }
    }


    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    setupPermissions()

                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun saveData() {

        val title = edt_title.text.toString()
        val notes = edt_notes.text.toString()
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("EEE d , yyyy")
        val formatUpdateTime = DateTimeFormatter.ofPattern("HH:mm a")
        val formatted = current.format(formatter)
        val formattedUpdateTime = current.format(formatUpdateTime)


        if (title.isEmpty() && notes.isEmpty()) {
            finish()
        } else if (lat != null && lng != null) {
            val ref = FirebaseDatabase.getInstance().getReference("Topics")
            val topicsId = ref.push().key
            val data = DataModel(
                topicsId.toString(),
                title,
                notes,
                formatted,
                formattedUpdateTime,
                lat!!,
                lng!!
            )
            ref.child(topicsId.toString()).setValue(data).addOnCompleteListener {
                Toast.makeText(this, "Saving it success ", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "กรุณาเปิดให้เข้าถึงที่อยู่ของคุณ", Toast.LENGTH_LONG).show()

        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == save_data) {


            client!!.lastLocation.addOnSuccessListener { location ->
                lat = location.latitude.toString()
                lng = location.longitude.toString()

                saveData()

            }


        } else if (id == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }


}
