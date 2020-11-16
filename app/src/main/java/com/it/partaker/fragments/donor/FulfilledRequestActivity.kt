package com.it.partaker.fragments.donor

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.activities.LoginActivity
import com.it.partaker.adapter.HomeDonorAdapter
import com.it.partaker.models.Request
import kotlinx.android.synthetic.main.activity_fulfill_request.*

class FulfilledRequestActivity : AppCompatActivity(), MyRequestsClickListener {

    private var userReference : DatabaseReference? = null
    private var firebaseUser : FirebaseUser? = null
    private lateinit var adapter : HomeDonorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fulfill_request)

    }

    private fun mainDonationWork() {

        val reqRef = FirebaseDatabase.getInstance().reference.child("requests")

        val manager = LinearLayoutManager(this)
        rvFRDDonor.layoutManager = manager
        adapter = HomeDonorAdapter(this, this)
        rvFRDDonor.adapter = adapter

        reqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val requestList = mutableListOf<Request>()

                    for (data in snapshot.children) {
                        val request = data.getValue(Request::class.java)
                        if (request!!.getStatus() == "Approved" && request.getAssigned() == "Assigned" && request.getRequesterId() == firebaseUser!!.uid) {
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
            }
        })

    }

    override fun OnMyRequestsItemClickListener(view: View, request: Request) {
        Toast.makeText(this, request.getName(), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, FulfillRequestDetail()::class.java)
        intent.putExtra("Fulfill Request", request)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        mainDonationWork()
        checkReports()
    }

    private fun checkReports(){

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())

        userReference!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    if(p0.child("reports").value == "3"){
                        AlertDialog.Builder(this@FulfilledRequestActivity).apply {
                            setTitle("Your Account Has Been Disabled!")
                            setPositiveButton("OK") { _, _ ->
                                FirebaseAuth.getInstance().signOut()
                                val intent = Intent(this@FulfilledRequestActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                finish()
                            }
                        }.create().show()

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}