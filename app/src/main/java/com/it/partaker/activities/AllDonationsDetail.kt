package com.it.partaker.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.it.partaker.R
import com.it.partaker.classes.Donation
import com.it.partaker.classes.User
import kotlinx.android.synthetic.main.rv_all_don_on_click.*

class AllDonationsDetail : AppCompatActivity() {

    private var donationReference : DatabaseReference? = null
    private var userReference : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_all_don_on_click)

        val donation = intent.getSerializableExtra("All Donations") as Donation

        donationReference = FirebaseDatabase.getInstance().reference.child("donations")
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(donation.getPublisherId())

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val donor = snapshot.getValue(User::class.java)
                    tv_all_don_on_click_donor_nameFB.text = donor!!.getFullName()
                    tv_all_don_on_click_donor_contactFB.text = donor.getPhoneNumber()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AllDonationsDetail, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

        tv_all_don_on_click_nameFB.text = donation.getName()
        tv_all_don_on_click_descFB.text = donation.getDesc()

        tv_all_don_on_click_donation_statusFB.text = donation.getStatus()
        tv_all_don_on_click_donation_assignedFB.text = donation.getAssigned()

        Glide.with(this)
            .load(donation.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_all_don_on_click_image)
    }

}