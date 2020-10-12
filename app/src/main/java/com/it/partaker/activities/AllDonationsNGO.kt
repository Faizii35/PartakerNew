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
import com.it.partaker.ItemClickListener.MyDonationsClickListener
import com.it.partaker.R
import com.it.partaker.adapter.DonorAdapter
import com.it.partaker.models.Donation
import kotlinx.android.synthetic.main.activity_all_donations_n_g_o.*

class AllDonationsNGO : AppCompatActivity(), MyDonationsClickListener {

    private lateinit var adapter : DonorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_donations_n_g_o)

        val donRef = FirebaseDatabase.getInstance().reference.child("donations")

        val manager = LinearLayoutManager(this)
        rvAllDonNGO.layoutManager = manager
        adapter = DonorAdapter(this, this)
        rvAllDonNGO.adapter = adapter

        donRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val donationList = mutableListOf<Donation>()

                    for (data in snapshot.children) {

                        val donation = data.getValue(Donation::class.java)
                        donation.let {
                            donationList.add(it!!)
                        }

                    }
                    donationList.reverse()
                    adapter.setDonations(donationList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AllDonationsNGO, "Error: $error", Toast.LENGTH_SHORT).show()
            }

        }) // End Value Event Listener

    }

    override fun OnMyDonationsItemClickListener(view: View, donation: Donation) {

        val intent = Intent(this, AllDonationsDetail()::class.java)
        intent.putExtra("All Donations", donation)
        startActivity(intent)
    }

}