package com.it.partaker.fragments.ngo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.it.partaker.R
import com.it.partaker.classes.Request
import com.it.partaker.classes.User
import kotlinx.android.synthetic.main.rv_apv_complete_req_on_click.*


class ApproveDonorRequestDetailFragment() : AppCompatActivity() {

    private var requestReference : DatabaseReference? = null
    private var donReference : DatabaseReference? = null
    private var reqReference : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_apv_complete_req_on_click)

        val request = intent.getSerializableExtra("Approve Donor Request") as Request

        requestReference = FirebaseDatabase.getInstance().reference.child("requests")
        reqReference = FirebaseDatabase.getInstance().reference.child("users").child(request.getPublisherId())
        donReference = FirebaseDatabase.getInstance().reference.child("users").child(request.getRequesterId())

        val reqId = request.getRequesterId()
        Toast.makeText(this, reqId, Toast.LENGTH_SHORT).show()

        reqReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val requester = snapshot.getValue(User::class.java)
                    tv_apv_complete_req_on_click_receiver_nameFB.text = requester!!.getFullName()
                    tv_apv_complete_req_on_click_receiver_contactFB.text = requester.getPhoneNumber()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ApproveDonorRequestDetailFragment, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })
        donReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val donor = snapshot.getValue(User::class.java)
                    tv_apv_complete_req_on_click_donor_nameFB.text = donor!!.getFullName()
                    tv_apv_complete_req_on_click_donor_contactFB.text = donor.getPhoneNumber()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ApproveDonorRequestDetailFragment, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

        tv_apv_complete_req_on_click_nameFB.text = request.getName()
        tv_apv_complete_req_on_click_descFB.text = request.getDesc()
        Glide.with(this)
            .load(request.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_apv_complete_req_on_click_image)

        btn_apv_complete_req_on_click_approve.setOnClickListener {
            val donApv = HashMap<String, Any>()
            donApv["assigned"] = "Assigned"

            val requestId = request.getPostId()
            requestReference!!.child(requestId).updateChildren(donApv)

            Toast.makeText(this@ApproveDonorRequestDetailFragment, "Assigned", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ApproveDonorRequestFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btn_apv_complete_req_on_click_decline.setOnClickListener {
            val reqApv = HashMap<String, Any>()
            reqApv["assigned"] = "Pending"
            val requestId = request.getPostId()
            requestReference!!.child(requestId).updateChildren(reqApv)
            Toast.makeText(this@ApproveDonorRequestDetailFragment, "Declined", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ApproveDonorRequestFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }

}