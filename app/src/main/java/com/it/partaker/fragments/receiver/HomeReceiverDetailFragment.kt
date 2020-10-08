package com.it.partaker.fragments.receiver

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.it.partaker.R
import com.it.partaker.activities.MainReceiverActivity
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.rv_hrf_on_click.*


class HomeReceiverDetailFragment() : AppCompatActivity() {

    private var firebaseUser : FirebaseUser? = null
    private var donationReference : DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_hrf_on_click)

        val donation = intent.getSerializableExtra("Home Receiver") as Donation

        tv_hrf_on_click_nameFB.text = donation.getName()
        tv_hrf_on_click_descFB.text = donation.getDesc()

        Glide.with(this)
            .load(donation.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_hrf_on_click_image)

        btn_hrf_on_click_request.setOnClickListener {
            firebaseUser = FirebaseAuth.getInstance().currentUser
            donationReference = FirebaseDatabase.getInstance().reference.child("donations")

            val req_id = firebaseUser!!.uid

            val req_id_map = HashMap<String, Any>()
            req_id_map["requesterId"] = req_id
            req_id_map["assigned"] = "Requested"
            donationReference!!.child(donation.getPostId()).updateChildren(req_id_map)

            Toast.makeText(this, "Requested", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainReceiverActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)


        }


    }


}