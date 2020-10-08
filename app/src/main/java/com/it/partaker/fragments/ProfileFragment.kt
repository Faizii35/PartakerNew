package com.it.partaker.fragments

import android.app.Activity
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

private var mActivity: Activity? = null

class ProfileFragment : AppCompatActivity() {


    private var userReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null

    private var textView_name: TextView ?= null
    private var textView_phone: TextView ?= null
    private var textView_city: TextView ?= null
    private var textView_blood: TextView ?= null
    private var textView_gender: TextView ?= null
    private var textView_email: TextView ?= null


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
            this?.let { it1 ->
                AlertDialog.Builder(it1).apply {
                    setTitle("Are you sure?")
                    setPositiveButton("Yes") { _, _ ->
                        val user = FirebaseAuth.getInstance().currentUser!!
                        userReference?.removeValue()?.addOnCompleteListener {
                            if (it.isSuccessful){
                                user.delete().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
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
                            }
                            else{
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

        textView_name = findViewById(R.id.tvProfileFullNameFB)
        textView_phone = findViewById(R.id.tvProfilePhoneNumberFB)
        textView_city = findViewById(R.id.tvProfileCityFB)
        textView_blood = findViewById(R.id.tvProfileBloodGroupFB)
        textView_gender = findViewById(R.id.tvProfileGenderFB)
        textView_email = findViewById(R.id.tvProfileEmailFB)

        val name = sharedPrefs.getNameUser()
        textView_name?.text = name
        val phone = sharedPrefs.getPhoneUser()
        textView_phone?.text = phone
        val city = sharedPrefs.getCityUser()
        textView_city?.text = city
        val blood = sharedPrefs.getBloodUser()
        textView_blood?.text = blood
        val gender = sharedPrefs.getGenderUser()
        textView_gender?.text = gender
        val email = sharedPrefs.getEmailUser()
        textView_email?.text = email
        val profilePic = sharedPrefs.getProfileUser()
        this?.applicationContext?.let {
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