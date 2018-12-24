package com.watchara.todolist.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.firebase.database.*
import com.watchara.todolist.R
import com.watchara.todolist.adapter.AdapterRecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mMessageReference: Query
    var myList: ArrayList<DataModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar_main)

        recyclerView()
        mMessageReference = FirebaseDatabase.getInstance().getReference("Topics").orderByChild("timeUpdate")
        mMessageReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                println("loadPost:onCancelled ${databaseError.toException()}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

//                test_text.text = dataSnapshot.value.toString()
                if (dataSnapshot.exists()) {
                    mRecyclerView.visibility = View.VISIBLE
                    text_show.visibility = View.GONE
                    myList.clear()
                    for (i in dataSnapshot.children) {

                        val getData = i.getValue(DataModel::class.java)


                        myList.add(getData!!)
                    }

                    mRecyclerView.adapter.notifyDataSetChanged()

                } else {

                    mRecyclerView.visibility = View.GONE
                    text_show.visibility = View.VISIBLE
                    Toast.makeText(this@MainActivity, "เพิ่มบันทึกได้ที่ มุมล่างขวาครับ ", Toast.LENGTH_LONG).show()
                }
            }

        })




        fab.setOnClickListener {
            val i = Intent(this@MainActivity, CreateNoteActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(i)
        }


    }

    private fun recyclerView() {

        mRecyclerView.adapter = AdapterRecyclerView(this, myList)
        mRecyclerView.layoutManager = LinearLayoutManager(this)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.logout_facebook) {
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut()
                var intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }

        }

        return super.onOptionsItemSelected(item)
    }





}
