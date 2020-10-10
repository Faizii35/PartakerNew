package com.it.partaker.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.adapter.ReceiverAdapter
import com.it.partaker.classes.Request
import kotlinx.android.synthetic.main.activity_all_requests_n_g_o.*

class AllRequestsNGO : AppCompatActivity(), MyRequestsClickListener {

    private lateinit var adapter : ReceiverAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_requests_n_g_o)

        val reqRef = FirebaseDatabase.getInstance().reference.child("requests")

        val manager = LinearLayoutManager(this)
        rvAllReqNGO.layoutManager = manager
        adapter = ReceiverAdapter(this, this)
        rvAllReqNGO.adapter = adapter

        reqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val requestList = mutableListOf<Request>()

                    for (data in snapshot.children) {

                        val request = data.getValue(Request::class.java)
                        request.let {
                            requestList.add(it!!)
                        }

                    }
                    requestList.reverse()
                    adapter.setRequests(requestList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AllRequestsNGO, "Error: $error", Toast.LENGTH_SHORT).show()
            }

        }) // End Value Event Listener

    }

    override fun OnMyRequestsItemClickListener(view: View, request: Request) {
        val intent = Intent(this, AllRequestsDetail()::class.java)
        intent.putExtra("All Requests", request)
        startActivity(intent)
    }

}