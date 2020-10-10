 package com.it.partaker.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.adapter.HomeDonorAdapter
import com.it.partaker.classes.Request
import com.it.partaker.fragments.donor.HomeDonorDetailFragment
import kotlinx.android.synthetic.main.activity_donor_wish.*

 class DonorWishActivity : AppCompatActivity(), MyRequestsClickListener {
     private var firebaseUser : FirebaseUser? = null
     private var reqReference : DatabaseReference? = null
     private var wishReference : DatabaseReference? = null

     var receiverList = mutableListOf<Request>()
     var wishList = mutableListOf<String>()

     private lateinit var adapter : HomeDonorAdapter

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donor_wish)

         val manager = LinearLayoutManager(this)
         rvDonorWish.layoutManager = manager
         adapter = HomeDonorAdapter(this, this)
         rvDonorWish.adapter = adapter



         firebaseUser = FirebaseAuth.getInstance().currentUser
         reqReference = FirebaseDatabase.getInstance().reference.child("requests")
         wishReference = FirebaseDatabase.getInstance().reference.child("wishList")

         wishReference!!.child(firebaseUser!!.uid).addValueEventListener(object: ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 if (snapshot.exists()){
                     for (wishes in snapshot.children){
                         wishList.add(wishes.key.toString())
                     }
                 }
             }
             override fun onCancelled(error: DatabaseError) { TODO("Not yet implemented") }
         })

         reqReference!!.addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 if (snapshot.exists()){
                     for (data in snapshot.children){
                         val request = data.getValue(Request::class.java)
                         for (key in wishList){
                             if (key == request!!.getPostId()){
                                 receiverList.add(request)
                             }
                         }
                     }
                     receiverList.reverse()
                     adapter.setRequests(receiverList)
                 }
             }
             override fun onCancelled(error: DatabaseError) { TODO("Not yet implemented") }
         })
     }

     override fun OnMyRequestsItemClickListener(view: View, request: Request) {
         Toast.makeText(this, request.getName(), Toast.LENGTH_SHORT).show()
         val intent = Intent(this, HomeDonorDetailFragment()::class.java)
         intent.putExtra("Home Donor", request)
         startActivity(intent)
     }

 }