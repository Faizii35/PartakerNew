package com.it.partaker.fragments

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.it.partaker.R
import com.it.partaker.activities.ChangePasswordActivity
import com.it.partaker.activities.EditProfileActivity
import com.it.partaker.activities.LoginActivity
import com.it.partaker.persistence.PartakerPrefs
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : AppCompatActivity() {

    private var userReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null

    private var textViewName: TextView ?= null
    private var textViewPhone: TextView ?= null
    private var textViewCity: TextView ?= null
    private var textViewBlood: TextView ?= null
    private var textViewGender: TextView ?= null
    private var textViewEmail: TextView ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")


        tvProfileChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }


        btnProfileDelete.setOnClickListener {
            this.let { it1 ->
                AlertDialog.Builder(it1).apply {
                    setTitle("Are you sure?")
                    setPositiveButton("Yes") { _, _ ->
                        val user = FirebaseAuth.getInstance().currentUser!!
                        userReference?.removeValue()?.addOnCompleteListener {
                            if (it.isSuccessful){
                                user.delete().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        FirebaseAuth.getInstance().currentUser!!.delete()
                                        Toast.makeText(baseContext, "Account Deleted", Toast.LENGTH_LONG).show()
                                        val intent = Intent(baseContext, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                            baseContext,
                                            "Error: ${task.exception}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else{
                                Toast.makeText(
                                    baseContext,
                                    "User Deletion Process Doesn't Succeeded",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    setNegativeButton("Cancel") { _, _ ->
                        Toast.makeText(baseContext, "Process Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }.create().show()
            }
        }

        btnProfileEditProfile.setOnClickListener {
            val intent = Intent(baseContext, EditProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }



    override fun onResume() {
        super.onResume()

        val sharedPrefs = PartakerPrefs(this)

        textViewName = findViewById(R.id.tvProfileFullNameFB)
        textViewPhone = findViewById(R.id.tvProfilePhoneNumberFB)
        textViewCity = findViewById(R.id.tvProfileCityFB)
        textViewBlood = findViewById(R.id.tvProfileBloodGroupFB)
        textViewGender = findViewById(R.id.tvProfileGenderFB)
        textViewEmail = findViewById(R.id.tvProfileEmailFB)

        val name = sharedPrefs.getNameUser()
        textViewName?.text = name
        val phone = sharedPrefs.getPhoneUser()
        textViewPhone?.text = phone
        val city = sharedPrefs.getCityUser()
        textViewCity?.text = city
        val blood = sharedPrefs.getBloodUser()
        textViewBlood?.text = blood
        val gender = sharedPrefs.getGenderUser()
        textViewGender?.text = gender
        val email = sharedPrefs.getEmailUser()
        textViewEmail?.text = email
        val profilePic = sharedPrefs.getProfileUser()
        this.applicationContext?.let {
            ivProfilePic?.let { it1 ->
                Glide.with(it)
                    .load(profilePic)
                    .placeholder(R.drawable.default_profile_pic)
                    .transform(CircleCrop())
                    .into(it1)
            }
        }
    }
}