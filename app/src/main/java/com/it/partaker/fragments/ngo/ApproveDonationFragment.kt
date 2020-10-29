package com.it.partaker.fragments.ngo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.it.partaker.ItemClickListener.MyDonationsClickListener
import com.it.partaker.R
import com.it.partaker.adapter.ApproveDonationAdapter
import com.it.partaker.models.Donation
import kotlinx.android.synthetic.main.fragment_approve_donation.*

class ApproveDonationFragment : AppCompatActivity(), MyDonationsClickListener {

    private lateinit var adapter : ApproveDonationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_approve_donation)

        val donRef = FirebaseDatabase.getInstance().reference.child("donations")

        val manager = LinearLayoutManager(this)

        rv_Apv_Don_NGO.layoutManager = manager
        adapter = ApproveDonationAdapter(this, this)
        rv_Apv_Don_NGO.adapter = adapter

        donRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val donationList = mutableListOf<Donation>()

                    for (data in snapshot.children) {
                        val donation = data.getValue(Donation::class.java)
                        donation!!.setPostId(data.key.toString())
                        if (donation.getStatus() == "Approval Required" && donation.getAssigned() == "Pending") {
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
//                Toast.makeText(this@ApproveDonationFragment, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun OnMyDonationsItemClickListener(view: View, donation: Donation) {
        val intent = Intent(this, ApproveDonationDetailFragment::class.java)
        intent.putExtra("Approve Donation", donation)
        startActivity(intent)
    }
}