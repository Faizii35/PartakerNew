package com.it.partaker.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.adapter.HomeDonorAdapter
import com.it.partaker.classes.Request
import com.it.partaker.fragments.donor.HomeDonorDetailFragment
import kotlinx.android.synthetic.main.content_main.*

class DonorWishListActivity : AppCompatActivity(), MyRequestsClickListener {

    private var userReference : DatabaseReference? = null
    private var firebaseUser : FirebaseUser? = null
    private lateinit var adapter : HomeDonorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_wish_list)

        val reqRef = FirebaseDatabase.getInstance().reference.child("requests")

        val manager = LinearLayoutManager(this)
        rvHDFDonor.layoutManager = manager
        adapter = HomeDonorAdapter(this, this)
        rvHDFDonor.adapter = adapter

        reqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val requestList = mutableListOf<Request>()

                    for (data in snapshot.children) {
                        val request = data.getValue(Request::class.java)
                        if (request!!.getStatus() == "Approved" && request.getAssigned() == "Pending" && request.getIsLiked()) {
                            request.let {
                                requestList.add(it)
                            }
                        }
                    }
                    requestList.reverse()
                    adapter.setRequests(requestList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun OnMyRequestsItemClickListener(view: View, request: Request) {
        val intent = Intent(this, HomeDonorDetailFragment()::class.java)
        intent.putExtra("Home Donor", request)
        startActivity(intent)
    }
}