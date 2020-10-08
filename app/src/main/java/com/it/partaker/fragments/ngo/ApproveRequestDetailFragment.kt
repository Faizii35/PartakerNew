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
import kotlinx.android.synthetic.main.rv_apv_req_on_click.*

class ApproveRequestDetailFragment() : AppCompatActivity() {

    private var requestReference : DatabaseReference? = null
    private var userReference : DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_apv_req_on_click)

        val request = intent.getSerializableExtra("Approve Request") as Request

        requestReference = FirebaseDatabase.getInstance().reference.child("requests")
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(request.getPublisherId())

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val receiver = snapshot.getValue(User::class.java)
                    tv_apv_req_on_click_receiver_nameFB.text = receiver!!.getFullName()
                    tv_apv_req_on_click_receiver_contactFB.text = receiver.getPhoneNumber()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ApproveRequestDetailFragment, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

        tv_apv_req_on_click_nameFB.text = request.getName()
        tv_apv_req_on_click_descFB.text = request.getDesc()
        Glide.with(this)
            .load(request.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_apv_req_on_click_image)

        btn_apv_req_on_click_approve.setOnClickListener {
            val reqApv = HashMap<String, Any>()
            reqApv["status"] = "Approved"
            requestReference!!.child(request.getPostId()).updateChildren(reqApv)

            Toast.makeText(this@ApproveRequestDetailFragment, "Approved", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ApproveRequestFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btn_apv_req_on_click_decline.setOnClickListener {
            val reqApv = HashMap<String, Any>()
            reqApv["status"] = "Declined"
            requestReference!!.child(request.getPostId()).updateChildren(reqApv)

            Toast.makeText(this@ApproveRequestDetailFragment, "Declined", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ApproveRequestFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

}