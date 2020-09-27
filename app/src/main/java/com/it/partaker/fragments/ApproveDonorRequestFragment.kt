package com.it.partaker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.it.partaker.R
import com.it.partaker.adapter.ReceiverAdapter
import com.it.partaker.classes.Request
import kotlinx.android.synthetic.main.fragment_approve_donor_request.*


class ApproveDonorRequestFragment : Fragment() {

   // private lateinit var adapter : ReceiverAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_approve_donor_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reqRef = FirebaseDatabase.getInstance().reference.child("requests")

//        val manager = LinearLayoutManager(activity)
//        rv_Apv_Don_Req_NGO.layoutManager = manager
//        adapter = ReceiverAdapter(requireContext(), this)
//        rv_Apv_Don_Req_NGO.adapter = adapter

        reqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    val requestList = mutableListOf<Request>()

                    for(data in snapshot.children)
                    {
                        val request = data.getValue(Request::class.java)
                        if(request!!.getStatus() == "Approved" && request.getAssigned() == "Requested"){
                            request.let {
                                requestList.add(it)
                            }
                        }
                    }
                    requestList.reverse()
                 //   adapter.setRequests(requestList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}