package com.watchara.todolist.activity

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import com.watchara.todolist.R
import kotlinx.android.synthetic.main.activity_edit_note.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class EditNoteActivity : AppCompatActivity() {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null


    private val TAG = "LocationProvider"

    lateinit var lat :String
    lateinit var lng :String
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34


    private lateinit var dataNote: DataModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        setSupportActionBar(toolbar_edt_note)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        dataNote = intent.getParcelableExtra<DataModel>("data")

        txv_title.text = dataNote.title
        txv_notes.text = dataNote.notes
        last_update.text = dataNote.timeUpdate
        edt_title.setText(dataNote.title)
        edt_notes.setText(dataNote.notes)

        txv_title.setOnClickListener {
            txv_title.visibility = View.GONE
            edt_title.visibility = View.VISIBLE
            edt_title.setSelection(edt_title.text.length)

        }

        txv_notes.setOnClickListener {
            onClickNote()
        }

        linear_time_update.setOnClickListener {
            onClickNote()
        }


    }

    private fun onClickNote() {
        txv_notes.visibility = View.GONE
        edt_notes.visibility = View.VISIBLE
        edt_notes.setSelection(edt_notes.text.length)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu_edit, menu)
        return true
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.edit_data) {

            mFusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
                lat = location.latitude.toString()
                lng = location.longitude.toString()

                updateData()

            }
        } else if (id == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateData() {
        val update = FirebaseDatabase.getInstance().getReference("Topics")
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("HH:mm a")
        val formatted = current.format(formatter)
        var dataUpdate = DataModel(
            dataNote.id,
            edt_title.text.toString(),
            edt_notes.text.toString(),
            dataNote.timeCreate,
            formatted,
            lat,
            lng
        )

        update.child(dataNote.id).setValue(dataUpdate).addOnCompleteListener {
            updateTimeModified()
            Toast.makeText(this,"update success ",Toast.LENGTH_LONG).show()

            txv_notes.text = edt_notes.text
            txv_title.text = edt_title.text
            edt_title.visibility = View.GONE
            edt_notes.visibility = View.GONE
            txv_title.visibility = View.VISIBLE
            txv_notes.visibility = View.VISIBLE

        }



    }
    private fun updateTimeModified(){

        val ref = FirebaseDatabase.getInstance().getReference("Topics")

        ref.addValueEventListener(object :EventListener, ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                last_update.text = dataSnapshot.child(dataNote.id).child("timeUpdate").value.toString()

            }

        })

    }


}
