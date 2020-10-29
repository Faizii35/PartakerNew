package com.it.partaker.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.adapter.HomeDonorAdapter
import com.it.partaker.fragments.AboutAppFragment
import com.it.partaker.fragments.ProfileFragment
import com.it.partaker.fragments.donor.HomeDonorDetailFragment
import com.it.partaker.fragments.donor.MyDonationsFragment
import com.it.partaker.models.Request
import com.it.partaker.models.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity(), MyRequestsClickListener {

    private var userReference : DatabaseReference? = null
    private var firebaseUser : FirebaseUser? = null
    private lateinit var adapter : HomeDonorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        toolbar.title = "Donor"

        rvHDFDonor.visibility = View.VISIBLE
        ll_H_NGO_0.visibility = View.GONE
        rvHRFReceiver.visibility = View.GONE

        showEmployeeNavigationDrawer()

        nav_view.menu.getItem(0).isChecked = true

        navigationWork()

    }

    private fun mainDonationWork() {

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
                        if ((request!!.getStatus() == "Approved" && request.getAssigned() == "Pending") || (request.getStatus() == "Approved" && request.getAssigned() == "Requested" && request.getRequesterId() == firebaseUser!!.uid)) {
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

        fa_btn_HDF_add_donation.setOnClickListener {
            val intent = Intent(this, AddPostActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun OnMyRequestsItemClickListener(view: View, request: Request) {
        Toast.makeText(this, request.getName(), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, HomeDonorDetailFragment()::class.java)
        intent.putExtra("Home Donor", request)
        startActivity(intent)
    }

    private fun navigationWork() {

        //val sharedPrefs = PartakerPrefs(this@MainActivity)
        val headerView: View? = nav_view.getHeaderView(0)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())

        userReference!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(User::class.java)

                val name = user!!.getFullName()
                val email = user.getEmail()
                val profile = user.getProfilePic()

                headerView?.tvMainActivityNavHeaderName?.text = name
                headerView?.tvMainActivityNavHeaderEmail?.text = email
                headerView?.ivMainActivityNavHeaderProfile?.let {
                    Glide.with(applicationContext)
                        .load(profile)
                        .placeholder(R.drawable.default_profile_pic)
                        .transform(CircleCrop())
                        .into(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        //Drawer Related Code of Main Activity Lies Below
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home_donor -> {
                    closeDrawer()
                    true
                }
                R.id.nav_myDonations -> {
                    val intent = Intent(this@MainActivity,MyDonationsFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    closeDrawer()
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this@MainActivity,ProfileFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    closeDrawer()
                    true
                }
                R.id.nav_don_wishList -> {
                    val intent = Intent(this@MainActivity,DonorWishActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    closeDrawer()
                    true
                }
                R.id.nav_aboutApp -> {
                    val intent = Intent(this, AboutAppFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)

                    closeDrawer()
                    true
                }
                R.id.nav_reviewApp -> {

                    val uri = Uri.parse("market://details?id=$packageName")
                    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                    // To count with Play market back_stack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(
                        Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    )
                    try {
                        startActivity(goToMarket)
                    } catch (e: ActivityNotFoundException) {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                            )
                        )
                    }
                    closeDrawer()
                    true
                }
                R.id.nav_shareApp -> {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Partaker")
                    startActivity(Intent.createChooser(shareIntent, null))
                    closeDrawer()
                    true
                }
                R.id.nav_logout -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Are you sure?")
                        setPositiveButton("Yes") { _, _ ->
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(this@MainActivity, "Logout", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton("Cancel") { _, _ ->
                            Toast.makeText(
                                this@MainActivity,
                                "Process Cancelled",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }.create().show()
                    closeDrawer()
                    true
                }

                else -> {
                    Toast.makeText(this, "Item Not Selected", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }

    }

    //Drawer Related Code of Main Activity Lies Below
    private fun showEmployeeNavigationDrawer() {

        setSupportActionBar(toolbar)
        val drawerToggle: androidx.appcompat.app.ActionBarDrawerToggle =
            object : androidx.appcompat.app.ActionBarDrawerToggle(
                this@MainActivity,
                drawer_layout,
                toolbar,
                (R.string.navigation_drawer_open),
                (R.string.navigation_drawer_close)
            ) {

            }
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        nav_view.menu.findItem(R.id.nav_home_ngo).isVisible = false
        nav_view.menu.findItem(R.id.nav_ngo_all_don).isVisible = false
        nav_view.menu.findItem(R.id.nav_ngo_all_req).isVisible = false
        nav_view.menu.findItem(R.id.nav_banned_user).isVisible = false


        nav_view.menu.findItem(R.id.nav_home_receiver).isVisible = false
        nav_view.menu.findItem(R.id.nav_myRequests).isVisible = false

        nav_view.menu.findItem(R.id.nav_home_donor).isVisible = true
        nav_view.menu.findItem(R.id.nav_myDonations).isVisible = true
    }

    private fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
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
                        AlertDialog.Builder(this@MainActivity).apply {
                            setTitle("Your Account Has Been Disabled!")
                            setPositiveButton("OK") { _, _ ->
                                FirebaseAuth.getInstance().signOut()
                                val intent = Intent(this@MainActivity, LoginActivity::class.java)
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