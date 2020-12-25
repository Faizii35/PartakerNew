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
import com.google.firebase.iid.FirebaseInstanceId
import com.it.partaker.ItemClickListener.MyDonationsClickListener
import com.it.partaker.R
import com.it.partaker.adapter.HomeReceiverAdapter
import com.it.partaker.fragments.AboutAppFragment
import com.it.partaker.fragments.ProfileFragment
import com.it.partaker.fragments.receiver.HomeReceiverDetailFragment
import com.it.partaker.fragments.receiver.MyRequestsFragment
import com.it.partaker.fragments.receiver.ReceivedDonationActivity
import com.it.partaker.models.Donation
import com.it.partaker.notifications.Token
import com.it.partaker.persistence.PartakerPrefs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainReceiverActivity : AppCompatActivity(), MyDonationsClickListener {

    private var userReference : DatabaseReference? = null
    private var firebaseUser : FirebaseUser? = null

    private lateinit var adapter : HomeReceiverAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        toolbar.title = "Receiver"

        tv_Main_Activity_NoPost.visibility = View.GONE
        rvHRFReceiver.visibility = View.VISIBLE
        rvHDFDonor.visibility = View.GONE
        ll_H_NGO_0.visibility = View.GONE


        showNavigationDrawer()

        nav_view.menu.getItem(1).isChecked = true

        navigationWork()

        updateToken(FirebaseInstanceId.getInstance().token)

    }

    private fun checkReports(){

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())

        userReference!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    if(p0.child("reports").value == "3"){
                        AlertDialog.Builder(this@MainReceiverActivity).apply {
                            setTitle("Your Account Has Been Disabled!")
                            setPositiveButton("OK") { _, _ ->
                                FirebaseAuth.getInstance().signOut()
                                val intent = Intent(this@MainReceiverActivity, LoginActivity::class.java)
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

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun mainRequestWork() {

        val donRef = FirebaseDatabase.getInstance().reference.child("donations")

        val manager = LinearLayoutManager(this)
        rvHRFReceiver.layoutManager = manager
        adapter = HomeReceiverAdapter(this, this)
        rvHRFReceiver.adapter = adapter

        donRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val donationList = mutableListOf<Donation>()

                    for (data in snapshot.children) {
                        val donation = data.getValue(Donation::class.java)
                        if ((donation!!.getStatus() == "Approved" && donation.getAssigned() == "Pending") || (donation.getStatus() == "Approved" && donation.getAssigned() == "Requested" && donation.getRequesterId() == firebaseUser!!.uid)) {
                            donation.let {
                                donationList.add(it)
                            }
                        }
                    }
                    donationList.reverse()
                    adapter.setDonations(donationList)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        fa_btn_HDF_add_donation.setOnClickListener {
            val intent = Intent(baseContext, AddPostActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }

    override fun OnMyDonationsItemClickListener(view: View, donation: Donation) {
        Toast.makeText(this, donation.getName(), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, HomeReceiverDetailFragment()::class.java)
        intent.putExtra("Home Receiver", donation)
        startActivity(intent)

    }

    //Drawer Related Code of Main Activity Lies Below
    private fun showNavigationDrawer() {

        setSupportActionBar(toolbar)
        val drawerToggle: androidx.appcompat.app.ActionBarDrawerToggle =
            object : androidx.appcompat.app.ActionBarDrawerToggle(
                this@MainReceiverActivity,
                drawer_layout,
                toolbar,
                (R.string.navigation_drawer_open),
                (R.string.navigation_drawer_close)
            ) {

            }
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        nav_view.menu.findItem(R.id.nav_home_receiver).isVisible = true
        nav_view.menu.findItem(R.id.nav_myRequests).isVisible = true

        nav_view.menu.findItem(R.id.nav_home_donor).isVisible = false
        nav_view.menu.findItem(R.id.nav_don_wishList).isVisible = false
        nav_view.menu.findItem(R.id.nav_myDonations).isVisible = false
        nav_view.menu.findItem(R.id.nav_fulfilled_request).isVisible = false


        nav_view.menu.findItem(R.id.nav_ngo_all_don).isVisible = false
        nav_view.menu.findItem(R.id.nav_ngo_all_req).isVisible = false
        nav_view.menu.findItem(R.id.nav_home_ngo).isVisible = false
        nav_view.menu.findItem(R.id.nav_banned_user).isVisible = false

    }

    private fun navigationWork() {

        val sharedPrefs = PartakerPrefs(this@MainReceiverActivity)
        val headerView: View? = nav_view.getHeaderView(0)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())

        userReference!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val name = sharedPrefs.getNameUser()
                val email = sharedPrefs.getEmailUser()
                val profile = sharedPrefs.getProfileUser()

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

                R.id.nav_home_receiver -> {
                    val intent = Intent(this@MainReceiverActivity,MainReceiverActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    closeDrawer()
                    true
                }
                R.id.nav_myRequests -> {
                    val intent = Intent(this@MainReceiverActivity,MyRequestsFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    closeDrawer()
                    true
                }
                R.id.nav_profile -> {
                    val intent = Intent(this@MainReceiverActivity,ProfileFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    closeDrawer()
                    true
                }
                R.id.nav_fulfilled_donation -> {
                    val intent = Intent(this, ReceivedDonationActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    closeDrawer()
                    true
                }
                R.id.nav_aboutApp -> {
                    toolbar.title = "About App"
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
                            Toast.makeText(this@MainReceiverActivity, "Logout", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainReceiverActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton("Cancel") { _, _ ->
                            Toast.makeText(
                                this@MainReceiverActivity,
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

    private fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }

    override fun onResume() {
        super.onResume()

        mainRequestWork()
        checkReports()

        toolbar.title = "Receiver"
    }

}