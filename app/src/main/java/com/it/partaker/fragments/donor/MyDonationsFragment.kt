package com.it.partaker.fragments.donor

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
import com.it.partaker.ItemClickListener.MyDonationsClickListener
import com.it.partaker.R
import com.it.partaker.adapter.DonorAdapter
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.fragment_my_donations.*

class MyDonationsFragment : AppCompatActivity() , MyDonationsClickListener{

    private lateinit var adapter : DonorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_my_donations)

        val donRef = FirebaseDatabase.getInstance().reference.child("donations")
        val userRef = FirebaseAuth.getInstance().currentUser!!

        val manager = LinearLayoutManager(this)
        rvMDFDonor.layoutManager = manager
        adapter = DonorAdapter(this, this)
        rvMDFDonor.adapter = adapter

        donRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val donationList = mutableListOf<Donation>()
                    for (data in snapshot.children) {
                        val donation = data.getValue(Donation::class.java)
                        if (donation!!.getPublisherId() == userRef.uid) {
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
                Toast.makeText(baseContext, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun OnMyDonationsItemClickListener(view: View, donation: Donation) {
        Toast.makeText(baseContext, donation.getName(), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MyDonationsDetailFragment()::class.java)
        intent.putExtra("My Donations", donation)
        startActivity(intent)
    }
}