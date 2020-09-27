package com.it.partaker.fragments.ngo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.it.partaker.R
import com.it.partaker.classes.Donation
import com.it.partaker.classes.User
import kotlinx.android.synthetic.main.rv_apv_don_on_click.*
import kotlinx.android.synthetic.main.rv_apv_don_on_click.view.*


class ApproveDonationDetailFragment(val donation: Donation) : Fragment() {

    private var donationReference : DatabaseReference? = null
    private var userReference : DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.rv_apv_don_on_click, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        donationReference = FirebaseDatabase.getInstance().reference.child("donations")
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(donation.getPublisherId())

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val donor = snapshot.getValue(User::class.java)
                    view.tv_apv_don_on_click_donor_nameFB.text = donor!!.getFullName()
                    view.tv_apv_don_on_click_donor_contactFB.text = donor.getPhoneNumber()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

        view.tv_apv_don_on_click_nameFB.text = donation.getName()
        view.tv_apv_don_on_click_descFB.text = donation.getDesc()
        Glide.with(requireContext())
            .load(donation.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(view.iv_apv_don_on_click_image)


        btn_apv_don_on_click_approve.setOnClickListener {
            val donApv = HashMap<String, Any>()
            donApv["status"] = "Approved"

            val donationId = donation.getPostId()
            Toast.makeText(context, donationId, Toast.LENGTH_SHORT).show()
            donationReference!!.child(donationId).updateChildren(donApv)
        }

        btn_apv_don_on_click_decline.setOnClickListener {
            val donApv = HashMap<String, Any>()
            donApv["status"] = "Declined"

            donationReference!!.child(donation.getPostId()).updateChildren(donApv)
        }
    }

}