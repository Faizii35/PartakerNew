package com.it.partaker.fragments.receiver

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.it.partaker.R
import com.it.partaker.models.Request
import kotlinx.android.synthetic.main.rv_mrf_on_click.*


class MyRequestsDetailFragment : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_mrf_on_click)

        val request = intent.getSerializableExtra("My Request") as Request

        work()

        btn_mrf_on_click_delete.setOnClickListener {

            FirebaseDatabase.getInstance().reference.child("requests").child(request.getPostId()).removeValue()
            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MyRequestsFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btn_mrf_on_click_edit.setOnClickListener {
            FragmentEditMyRequests(request).show(supportFragmentManager,"")
        }

    }

    override fun onResume() {
        super.onResume()
        work()
    }

    fun work(){


        val request = intent.getSerializableExtra("My Request") as Request

        tv_mrf_on_click_nameFB.text = request.getName()
        tv_mrf_on_click_descFB.text = request.getDesc()
        tv_mrf_on_click_descFB.movementMethod = ScrollingMovementMethod()
        tv_mrf_on_click_statusFB.text = request.getStatus()
        tv_mrf_on_click_assignedFB.text = request.getAssigned()

        Glide.with(this)
            .load(request.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_mrf_on_click_image)

    }

}
