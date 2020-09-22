package com.it.partaker.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.it.partaker.R
import com.it.partaker.R.id.ivMainActivityNavHeaderProfile
import com.it.partaker.R.id.nav_host_fragment
import com.it.partaker.classes.User
import com.it.partaker.fragments.HomeDonorFragment
import com.it.partaker.fragments.MyDonationsFragment
import com.it.partaker.fragments.ProfileFragment
import com.it.partaker.persistence.PartakerPrefs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity() {

    private var userReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showEmployeeNavigationDrawer()

        val sharedPrefs = PartakerPrefs(this)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())

        val headerView: View = nav_view.getHeaderView(0)

        userReference!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user : User? = snapshot.getValue<User>(User::class.java)
                headerView.tvMainActivityNavHeaderName.text = user!!.getFullName()
                headerView.tvMainActivityNavHeaderEmail.text = user.getEmail()
                Glide.with(this@MainActivity)
                    .load(user.getProfilePic())
                    .placeholder(R.drawable.default_profile_pic)
                    .into(headerView.ivMainActivityNavHeaderProfile)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val reg = sharedPrefs.getRegisterAsUser().toString()
        Toast.makeText(this, reg, Toast.LENGTH_SHORT).show()

        supportFragmentManager.beginTransaction().replace(nav_host_fragment, HomeDonorFragment()).commit()

        //Drawer Related Code of Main Activity Lies Below
        nav_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    toolbar.title = "Donor"
                    supportFragmentManager.beginTransaction().replace(nav_host_fragment, HomeDonorFragment()).commit()
                    closeDrawer()
                    true
                }
                R.id.nav_profile -> {
                    toolbar.title = "Profile"
                    supportFragmentManager.beginTransaction().replace(nav_host_fragment, ProfileFragment()).commit()
                    closeDrawer()
                    true
                }
                R.id.nav_myDonations -> {
                    toolbar.title = "My Donations"
                    supportFragmentManager.beginTransaction().replace(nav_host_fragment, MyDonationsFragment()).commit()
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

    }

    private fun closeDrawer() {
        drawer_layout.closeDrawer(GravityCompat.START)
    }


}