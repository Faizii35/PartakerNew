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
import com.it.partaker.classes.Request
import com.it.partaker.classes.User
import kotlinx.android.synthetic.main.rv_apv_req_on_click.*
import kotlinx.android.synthetic.main.rv_apv_req_on_click.view.*


class ApproveRequestDetailFragment(val request: Request) : Fragment() {

    private var requestReference : DatabaseReference? = null
    private var userReference : DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.rv_apv_req_on_click, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestReference = FirebaseDatabase.getInstance().reference.child("requests")
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(request.getPublisherId())

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val receiver = snapshot.getValue(User::class.java)
                    view.tv_apv_req_on_click_receiver_nameFB.text = receiver!!.getFullName()
                    view.tv_apv_req_on_click_receiver_contactFB.text = receiver.getPhoneNumber()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

        view.tv_apv_req_on_click_nameFB.text = request.getName()
        view.tv_apv_req_on_click_descFB.text = request.getDesc()
        Glide.with(requireContext())
            .load(request.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(view.iv_apv_req_on_click_image)


        btn_apv_req_on_click_approve.setOnClickListener {
            val reqApv = HashMap<String, Any>()
            reqApv["status"] = "Approved"
            requestReference!!.child(request.getPostId()).updateChildren(reqApv)
        }

        btn_apv_req_on_click_decline.setOnClickListener {
            val reqApv = HashMap<String, Any>()
            reqApv["status"] = "Declined"
            requestReference!!.child(request.getPostId()).updateChildren(reqApv)
        }
    }

}