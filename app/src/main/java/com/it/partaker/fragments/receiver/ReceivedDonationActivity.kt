package com.it.partaker.fragments.receiver

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.it.partaker.ItemClickListener.MyDonationsClickListener
import com.it.partaker.R
import com.it.partaker.activities.LoginActivity
import com.it.partaker.adapter.HomeReceiverAdapter
import com.it.partaker.models.Donation
import kotlinx.android.synthetic.main.activity_received_donations.*

class ReceivedDonationActivity : AppCompatActivity(), MyDonationsClickListener {

    private var userReference : DatabaseReference? = null
    private var firebaseUser : FirebaseUser? = null
    private lateinit var adapter : HomeReceiverAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_received_donations)

    }

    private fun mainDonationWork() {

        val donRef = FirebaseDatabase.getInstance().reference.child("donations")

        val manager = LinearLayoutManager(this)
        rvRDReceiver.layoutManager = manager
        adapter = HomeReceiverAdapter(this, this)
        rvRDReceiver.adapter = adapter

        donRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val donationList = mutableListOf<Donation>()

                    for (data in snapshot.children) {
                        val donation = data.getValue(Donation::class.java)
                        if (donation!!.getStatus() == "Approved" && donation.getAssigned() == "Assigned" && donation.getRequesterId() == firebaseUser!!.uid) {
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
            }
        })

    }

    override fun OnMyDonationsItemClickListener(view: View, donation: Donation) {
        Toast.makeText(this, donation.getName(), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ReceivedDonationDetail()::class.java)
        intent.putExtra("Received Donation", donation)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        mainDonationWork()
        checkReports()
    }

    private fun checkReports(){

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())

        userReference!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    if(p0.child("reports").value == "3"){
                        AlertDialog.Builder(this@ReceivedDonationActivity).apply {
                            setTitle("Your Account Has Been Disabled!")
                            setPositiveButton("OK") { _, _ ->
                                FirebaseAuth.getInstance().signOut()
                                val intent = Intent(this@ReceivedDonationActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                finish()
                            }
                        }.create().show()

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}