package com.it.partaker.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.it.partaker.R
import com.it.partaker.models.Request
import com.it.partaker.models.User
import kotlinx.android.synthetic.main.rv_all_req_on_click.*

class AllRequestsDetail : AppCompatActivity() {

    private var donationReference : DatabaseReference? = null
    private var userReference : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_all_req_on_click)

        val donation = intent.getSerializableExtra("All Requests") as Request

        donationReference = FirebaseDatabase.getInstance().reference.child("donations")
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(donation.getPublisherId())

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val donor = snapshot.getValue(User::class.java)
                    tv_all_req_on_click_requester_nameFB.text = donor!!.getFullName()
                    tv_all_req_on_click_requester_contactFB.text = donor.getPhoneNumber()
                }
            }
            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@AllRequestsDetail, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

        tv_all_req_on_click_nameFB.text = donation.getName()
        tv_all_req_on_click_descFB.text = donation.getDesc()

        tv_all_req_on_click_request_statusFB.text = donation.getStatus()
        tv_all_req_on_click_request_assignedFB.text = donation.getAssigned()

        Glide.with(this)
            .load(donation.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_all_req_on_click_image)
    }

}