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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.it.partaker.R
import com.it.partaker.fragments.AboutAppFragment
import com.it.partaker.fragments.ngo.ApproveDonationFragment
import com.it.partaker.fragments.ngo.ApproveDonorRequestFragment
import com.it.partaker.fragments.ngo.ApproveReceiverRequestFragment
import com.it.partaker.fragments.ngo.ApproveRequestFragment
import com.it.partaker.models.User
import com.it.partaker.notifications.Token
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class HomeNGOActivity : AppCompatActivity() {

    private var userReference : DatabaseReference? = null
    private var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.title = "Home"

        ll_H_NGO_0.visibility = View.VISIBLE
        rvHDFDonor.visibility = View.GONE
        rvHRFReceiver.visibility = View.GONE
        fa_btn_HDF_add_donation.visibility = View.GONE

        mainHomeNGOWork()

        showEmployeeNavigationDrawer()

        nav_view.menu.getItem(2).isChecked = true

        navigationWork()

        updateToken(FirebaseInstanceId.getInstance().token)

    }
    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }
    private fun mainHomeNGOWork() {

        btnApproveDonation.setOnClickListener {
            val intent = Intent(this, ApproveDonationFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btnApproveRequest.setOnClickListener {
            val intent = Intent(this, ApproveRequestFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btnApproveDonorRequest.setOnClickListener {
            val intent = Intent(this, ApproveDonorRequestFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btnApproveReceiverRequest.setOnClickListener {
            val intent = Intent(this, ApproveReceiverRequestFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

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
                R.id.nav_home_ngo -> {
                    toolbar.title = "Home"
                    closeDrawer()
                    true
                }
                R.id.nav_ngo_all_don -> {
                    toolbar.title = "All Donations"
                    val intent = Intent(this@HomeNGOActivity, AllDonationsNGO::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    closeDrawer()
                    true
                }
                R.id.nav_ngo_all_req -> {
                    toolbar.title = "All Requests"
                    val intent = Intent(this@HomeNGOActivity, AllRequestsNGO::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    closeDrawer()
                    true
                }
                R.id.nav_banned_user -> {
                    toolbar.title = "Banned Users"
                    val intent = Intent(this@HomeNGOActivity, BannedUsersActivity::class.java)
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
                            Toast.makeText(this@HomeNGOActivity, "Logout", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@HomeNGOActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }
                        setNegativeButton("Cancel") { _, _ ->
                            Toast.makeText(
                                this@HomeNGOActivity,
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
                this@HomeNGOActivity,
                drawer_layout,
                toolbar,
                (R.string.navigation_drawer_open),
                (R.string.navigation_drawer_close)
            ) {

            }
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        nav_view.menu.findItem(R.id.nav_ngo_all_don).isVisible = true
        nav_view.menu.findItem(R.id.nav_ngo_all_req).isVisible = true
        nav_view.menu.findItem(R.id.nav_home_ngo).isVisible = true

        nav_view.menu.findItem(R.id.nav_profile).isVisible = false

        nav_view.menu.findItem(R.id.nav_home_receiver).isVisible = false
        nav_view.menu.findItem(R.id.nav_myRequests).isVisible = false

        nav_view.menu.findItem(R.id.nav_home_donor).isVisible = false
        nav_view.menu.findItem(R.id.nav_don_wishList).isVisible = false
        nav_view.menu.findItem(R.id.nav_myDonations).isVisible = false
    }

    private fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }

}