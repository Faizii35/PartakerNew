package com.it.partaker.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.it.partaker.R
import com.it.partaker.activities.AddPostActivity
import com.it.partaker.adapter.DonorAdapter
import com.it.partaker.adapter.ReceiverAdapter
import com.it.partaker.classes.Donation
import com.it.partaker.classes.Request
import com.it.partaker.classes.User
import kotlinx.android.synthetic.main.fragment_home_donor.*
import kotlinx.android.synthetic.main.fragment_home_donor.view.*
import kotlinx.android.synthetic.main.fragment_my_donations.*
import kotlinx.android.synthetic.main.fragment_my_requests.*

class HomeDonorFragment : Fragment() {

    private lateinit var adapter : ReceiverAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_donor, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reqRef = FirebaseDatabase.getInstance().reference.child("requests")

        val manager = LinearLayoutManager(activity)
        rvHDFDonor.layoutManager = manager
        adapter = ReceiverAdapter(requireContext())
        rvHDFDonor.adapter = adapter

        reqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    val requestList = mutableListOf<Request>()

                    for(data in snapshot.children)
                    {
                        val request = data.getValue(Request::class.java)
                        if(request!!.getStatus() == "Approval Required" && request.getAssigned() == "Pending"){
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
                TODO("Not yet implemented")
            }
        })




        view.fa_btn_HDF_add_donation.setOnClickListener {
            val intent = Intent(context, AddPostActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}