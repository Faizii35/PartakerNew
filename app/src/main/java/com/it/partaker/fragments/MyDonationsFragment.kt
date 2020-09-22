package com.it.partaker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.it.partaker.R
import com.it.partaker.adapter.DonorAdapter
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.fragment_my_donations.*

class MyDonationsFragment : Fragment() {

    private lateinit var adapter : DonorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_donations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val donRef = FirebaseDatabase.getInstance().reference.child("donations")
        val userRef = FirebaseAuth.getInstance().currentUser!!

        val manager = LinearLayoutManager(activity)
        rvMDFDonor.layoutManager = manager
        adapter = DonorAdapter(requireContext())
        rvMDFDonor.adapter = adapter

        donRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val donationList = mutableListOf<Donation>()
                    for(data in snapshot.children)
                    {
                        val donation = data.getValue(Donation::class.java)
                        if(donation!!.getPublisherId() == userRef.uid){
                            donation.let {
                                donationList.add(it)
                            }
                        }
                    }
                    donationList.reverse()
                    adapter.setDonations(donationList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}