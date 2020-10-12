package com.it.partaker.fragments.ngo

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
import com.it.partaker.adapter.ApproveRequestAdapter
import com.it.partaker.models.Request
import kotlinx.android.synthetic.main.fragment_approve_request.*

class ApproveRequestFragment : AppCompatActivity(), MyRequestsClickListener {

    private lateinit var adapter : ApproveRequestAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_approve_request)

        val reqRef = FirebaseDatabase.getInstance().reference.child("requests")

        val manager = LinearLayoutManager(this)
        rv_Apv_Req_NGO.layoutManager = manager
        adapter = ApproveRequestAdapter(this,this)
        rv_Apv_Req_NGO.adapter = adapter

        reqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val requestList = mutableListOf<Request>()

                    for (data in snapshot.children) {
                        val request = data.getValue(Request::class.java)
                        request!!.setPostId(data.key.toString())
                        if (request.getStatus() == "Approval Required" && request.getAssigned() == "Pending") {
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
                Toast.makeText(this@ApproveRequestFragment, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun OnMyRequestsItemClickListener(view: View, request: Request) {
        val intent = Intent(this, ApproveRequestDetailFragment::class.java)
        intent.putExtra("Approve Request", request)
        startActivity(intent)

    }
}