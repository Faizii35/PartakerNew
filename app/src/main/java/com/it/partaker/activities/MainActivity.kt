package com.it.partaker.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.ui.AppBarConfiguration
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.it.partaker.R
import com.it.partaker.R.id.nav_host_fragment
import com.it.partaker.classes.User
import com.it.partaker.fragments.HomeFragment
import com.it.partaker.fragments.MyDonations
import com.it.partaker.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var mAuth: FirebaseAuth? = null
    private var userReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(nav_host_fragment,HomeFragment()).commit()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())

        //Data Retrieval From Firebase in Profile Fragment
        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user = p0.getValue<User>(User::class.java)

                    tvMainActivityNavHeaderName.text = user!!.getFullName()
                    tvMainActivityNavHeaderEmail.text = user.getEmail()
                    Glide.with(this@MainActivity)
                        .load(user.getProfilePic())
                        .placeholder(R.drawable.default_profile_pic)
                        .transform(CircleCrop())
                        .into(ivMainActivityNavHeaderProfile)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Value Event Listener Failed: ",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        //Drawer Related Code of Main Activity Lies Below
        showEmployeeNavigationDrawer()

        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {

                    toolbar.title = "Donor"
                    supportFragmentManager.beginTransaction().replace(nav_host_fragment,HomeFragment()).commit()
                    closeDrawer()
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction().replace(nav_host_fragment,ProfileFragment()).commit()
//                    loadFragment(ProfileFragment())
                    toolbar.title = "Profile"
                    closeDrawer()
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_myDonations -> {
//                    loadFragment(CompletedCampaignsFragment())
                    toolbar.title = "My Donations"
                    supportFragmentManager.beginTransaction().replace(nav_host_fragment,MyDonations()).commit()
                    closeDrawer()
                    true
                }
                R.id.nav_aboutApp -> {
                    closeDrawer()
                    true
                }
                R.id.nav_reviewApp -> {

                        val uri = Uri.parse("market://details?id=$packageName")
                        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(
                            Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        try {
                            startActivity(goToMarket)
                        } catch (e: ActivityNotFoundException) {
                            startActivity(
                                Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
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
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            Toast.makeText(this@MainActivity, "Logout", Toast.LENGTH_SHORT).show()
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
                this,
                drawer_layout,
                toolbar,
                (R.string.navigation_drawer_open),
                (R.string.navigation_drawer_close)
            ) {

            }
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

    }

    private fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }
}
