package com.it.partaker.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.it.partaker.R
import com.it.partaker.activities.AddPostActivity
import com.it.partaker.classes.User
import kotlinx.android.synthetic.main.fragment_home_donor.view.*

class HomeDonorFragment : Fragment() {

    private var userReference : DatabaseReference? = null
    private var firebaseUser : FirebaseUser? = null

    var tv_name: TextView?= null
    var tv_email: TextView?= null
    var textProfile : String ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_donor, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.fa_btn_HDF_add_donation.setOnClickListener {
            val intent = Intent(context, AddPostActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}