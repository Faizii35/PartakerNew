package com.it.partaker.fragments.receiver

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.adapter.ReceiverAdapter
import com.it.partaker.models.Request
import kotlinx.android.synthetic.main.fragment_my_requests.*

class MyRequestsFragment : AppCompatActivity(), MyRequestsClickListener {

    private lateinit var adapter : ReceiverAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_my_requests)

        val reqRef = FirebaseDatabase.getInstance().reference.child("requests")
        val userRef = FirebaseAuth.getInstance().currentUser

        val manager = LinearLayoutManager(this)
        rvMRFReceiver.layoutManager = manager
        adapter = ReceiverAdapter(this, this)
        rvMRFReceiver.adapter = adapter

        reqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val requestList = mutableListOf<Request>()

                    for (data in snapshot.children) {
                        val request = data.getValue(Request::class.java)
                        val userID = userRef!!.uid
                        if (request!!.getPublisherId() == userID) {
                            request.let {
                                requestList.add(it)
                            }
                        }
                    }
                    requestList.reverse()
                    adapter.setRequests(requestList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@MyRequestsFragment, "", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun OnMyRequestsItemClickListener(view: View, request: Request) {
        Toast.makeText(baseContext, request.getName(), Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MyRequestsDetailFragment::class.java)
        intent.putExtra("My Request", request)
        startActivity(intent)
    }
}