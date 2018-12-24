package com.watchara.todolist.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.watchara.todolist.activity.DataModel
import com.watchara.todolist.activity.EditNoteActivity
import com.watchara.todolist.R
import java.util.*

class AdapterRecyclerView(private val mContext : Context, val data:ArrayList<DataModel>) : RecyclerView.Adapter<AdapterRecyclerView.AdapterHolder>( ) {

    var mCheckedIds = SparseBooleanArray()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.item_list,parent,false)
        return AdapterHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {

        holder.cardView.setOnClickListener {


            Toast.makeText(mContext,"position is : $position",Toast.LENGTH_LONG).show()


            val i = Intent(mContext, EditNoteActivity::class.java)
            val dataNote = DataModel(
                data[position].id,
                data[position].title,
                data[position].notes,
                data[position].timeCreate,
                data[position].timeUpdate,
                data[position].lat,
                data[position].lng
            )
            i.putExtra("data",dataNote)

            mContext.startActivity(i)
        }


        holder.cardView.setOnLongClickListener {

            val builder = AlertDialog.Builder(mContext)

            builder.setTitle("Are you sure Delete Note")
            builder.setMessage("")
            builder.setPositiveButton("YES"){dialog, which ->

                deleteItemList(position)

            }

            builder.setNegativeButton("No"){dialog,which ->
                dialog.dismiss()
            }


            val dialog: AlertDialog = builder.create()

            dialog.show()



            return@setOnLongClickListener true
        }
        holder.textTitle.text = data[position].title
        holder.textNote.text = data[position].notes
        holder.timeUpdate.text = data[position].timeUpdate
        holder.dateCreate.text = data[position].timeCreate

        if (data[position].title == ""){
            holder.textTitle.visibility = View.GONE
            holder.textNote.setLines(5)
        }

    }
    class AdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById<CardView>(R.id.card_view)
        var textTitle = itemView.findViewById<TextView>(R.id.text_title)
        var textNote = itemView.findViewById<TextView>(R.id.text_note)
        var timeUpdate = itemView.findViewById<TextView>(R.id.time_update)
        var dateCreate = itemView.findViewById<TextView>(R.id.date_create)

    }

    private fun deleteItemList(position: Int) {
        val delete = FirebaseDatabase.getInstance().getReference("Topics")
        delete.child(data[position].id).removeValue()
        Toast.makeText(mContext,"Remove Success",Toast.LENGTH_LONG).show()

    }

}


