package com.it.partaker.fragments.ngo

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.it.partaker.R
import com.it.partaker.models.Donation
import com.it.partaker.models.User
import kotlinx.android.synthetic.main.rv_apv_don_on_click.*


class ApproveDonationDetailFragment : AppCompatActivity() {

    private var donationReference : DatabaseReference? = null
    private var userReference : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_apv_don_on_click)

        val donation = intent.getSerializableExtra("Approve Donation") as Donation

        donationReference = FirebaseDatabase.getInstance().reference.child("donations")
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(donation.getPublisherId())

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val donor = snapshot.getValue(User::class.java)
                    tv_apv_don_on_click_donor_nameFB.text = donor!!.getFullName()
                    tv_apv_don_on_click_donor_contactFB.text = donor.getPhoneNumber()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ApproveDonationDetailFragment, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

        tv_apv_don_on_click_nameFB.text = donation.getName()
        tv_apv_don_on_click_descFB.text = donation.getDesc()
        tv_apv_don_on_click_descFB.movementMethod = ScrollingMovementMethod()
        Glide.with(this)
            .load(donation.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_apv_don_on_click_image)


        btn_apv_don_on_click_approve.setOnClickListener {
            val donApv = HashMap<String, Any>()
            donApv["status"] = "Approved"

            val donationId = donation.getPostId()
            donationReference!!.child(donationId).updateChildren(donApv)

            Toast.makeText(this@ApproveDonationDetailFragment, "Approved", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ApproveDonationFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btn_apv_don_on_click_decline.setOnClickListener {



            AlertDialog.Builder(this).apply {
                setTitle("Are you sure?")
                setPositiveButton("Yes") { _, _ ->

                    val donApv = HashMap<String, Any>()
                    donApv["status"] = "Declined"
                    donationReference!!.child(donation.getPostId()).updateChildren(donApv)

                    Toast.makeText(this@ApproveDonationDetailFragment, "Declined", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@ApproveDonationDetailFragment, ApproveDonationFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(
                        this@ApproveDonationDetailFragment,
                        "Process Cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.create().show()

        }

    }

}