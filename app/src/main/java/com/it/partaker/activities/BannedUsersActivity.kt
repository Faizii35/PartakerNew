package com.it.partaker.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.it.partaker.R
import com.it.partaker.adapter.BannedUserAdapter
import com.it.partaker.models.User
import kotlinx.android.synthetic.main.activity_banned_users.*

class BannedUsersActivity : AppCompatActivity() {

    private var firebaseUser : FirebaseUser? = null
    private var userReference : DatabaseReference? = null
    var userList = mutableListOf<User>()

    private lateinit var adapter : BannedUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banned_users)

        val manager = LinearLayoutManager(this)
        rvBannedUser.layoutManager = manager
        adapter = BannedUserAdapter(this)
        rvBannedUser.adapter = adapter

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users")

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    userList.clear()
                    for (data in snapshot.children){
                        val user = data.getValue(User::class.java)
                        val reports = data.child("reports").value.toString()
                        if(reports.toInt()>=3){
                            userList.add(user!!)
                        }
                    }
                    userList.reverse()
                    adapter.setUsers(userList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}