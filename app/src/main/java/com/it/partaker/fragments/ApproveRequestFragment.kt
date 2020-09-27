package com.it.partaker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.it.partaker.R
import com.it.partaker.adapter.ReceiverAdapter
import com.it.partaker.classes.Request
import kotlinx.android.synthetic.main.fragment_approve_request.*

class ApproveRequestFragment : Fragment() {

 //   private lateinit var adapter : ReceiverAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_approve_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reqRef = FirebaseDatabase.getInstance().reference.child("requests")

//        val manager = LinearLayoutManager(activity)
//        rv_Apv_Req_NGO.layoutManager = manager
//        adapter = ReceiverAdapter(requireContext())
//        rv_Apv_Req_NGO.adapter = adapter

        reqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val requestList = mutableListOf<Request>()

                    for (data in snapshot.children) {
                        val request = data.getValue(Request::class.java)
                        if (request!!.getStatus() == "Approval Required" && request.getAssigned() == "Pending") {
                            request.let {
                                requestList.add(it)
                            }
                        }
                    }
                    requestList.reverse()
               //     adapter.setRequests(requestList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

    }
}