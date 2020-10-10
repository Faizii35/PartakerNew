package com.it.partaker.fragments.donor

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.it.partaker.R
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.rv_mdf_on_click.*


class MyDonationsDetailFragment() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_mdf_on_click)

        val donation = intent.getSerializableExtra("My Donations") as Donation

        tv_mdf_on_click_nameFB.text = donation.getName()
        tv_mdf_on_click_descFB.text = donation.getDesc()
        tv_mdf_on_click_descFB.movementMethod = ScrollingMovementMethod()

        tv_mdf_on_click_statusFB.text = donation.getStatus()
        tv_mdf_on_click_assignedFB.text = donation.getAssigned()

        Glide.with(this)
            .load(donation.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_mdf_on_click_image)

        btn_mdf_on_click_delete.setOnClickListener {

            FirebaseDatabase.getInstance().reference.child("donations").child(donation.getPostId()).removeValue()
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MyDonationsFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

}