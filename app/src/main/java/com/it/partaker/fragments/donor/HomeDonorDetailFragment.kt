package com.it.partaker.fragments.donor

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
import com.it.partaker.activities.MainActivity
import com.it.partaker.classes.Request
import kotlinx.android.synthetic.main.rv_hdf_on_click.*


class HomeDonorDetailFragment() : AppCompatActivity() {

    private var firebaseUser : FirebaseUser? = null
    private var requestReference : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_hdf_on_click)


        val request = intent.getSerializableExtra("Home Donor") as Request

        tv_hdf_on_click_nameFB.text = request.getName()
        tv_hdf_on_click_descFB.text = request.getDesc()

        Glide.with(this)
            .load(request.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_hdf_on_click_image)

        btn_hdf_on_click_donate.setOnClickListener {
            firebaseUser = FirebaseAuth.getInstance().currentUser
            requestReference = FirebaseDatabase.getInstance().reference.child("requests")

            val req_id = firebaseUser!!.uid
            val reqIdMap = HashMap<String, Any>()
            reqIdMap["requesterId"] = req_id
            reqIdMap["assigned"] = "Requested"
            requestReference!!.child(request.getPostId()).updateChildren(reqIdMap)

            Toast.makeText(this, "Requested", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)

        }

    }

}