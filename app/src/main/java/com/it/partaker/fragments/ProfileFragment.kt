package com.it.partaker.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.it.partaker.R
import com.it.partaker.activities.ChangePasswordActivity
import com.it.partaker.activities.EditProfileActivity
import com.it.partaker.activities.LoginActivity
import com.it.partaker.classes.User
import com.it.partaker.persistence.PartakerPrefs
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

private var mActivity: Activity? = null

class ProfileFragment : Fragment() {


    private var userReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null

    var textView_name: TextView ?= null
    var textView_phone: TextView ?= null
    var textView_city: TextView ?= null
    var textView_blood: TextView ?= null
    var textView_gender: TextView ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")


        view.tvProfileChangePassword.setOnClickListener {
            val intent = Intent(context, ChangePasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }


        view.btnProfileDelete.setOnClickListener {
            context?.let { it1 ->
                AlertDialog.Builder(it1).apply {
                    setTitle("Are you sure?")
                    setPositiveButton("Yes") { _, _ ->
                        val user = FirebaseAuth.getInstance().currentUser!!
                       userReference?.removeValue()?.addOnCompleteListener {
                           if (it.isSuccessful){
                               user.delete().addOnCompleteListener { task ->
                                   if (task.isSuccessful) {
                                       Toast.makeText(context, "Account Deleted", Toast.LENGTH_LONG).show()
                                       val intent = Intent(context, LoginActivity::class.java)
                                       intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                       startActivity(intent)
                                   } else {
                                       Toast.makeText(
                                           context,
                                           "Error: ${task.exception}",
                                           Toast.LENGTH_SHORT
                                       ).show()
                                   }
                               }
                           }
                           else{
                               Toast.makeText(
                                   context,
                                   "User Deletion Process Doesn't Succeeded",
                                   Toast.LENGTH_LONG
                               ).show()
                           }
                       }
                    }
                    setNegativeButton("Cancel") { _, _ ->
                        Toast.makeText(context, "Process Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }.create().show()
            }
        }

        view.btnProfileEditProfile.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        return view
    }

    override fun onResume() {
        super.onResume()

        val sharedPrefs = context?.let { PartakerPrefs(it) }

        textView_name = view?.findViewById(R.id.tvProfileFullNameFB)
        textView_phone = view?.findViewById(R.id.tvProfilePhoneNumberFB)
        textView_city = view?.findViewById(R.id.tvProfileCityFB)
        textView_blood = view?.findViewById(R.id.tvProfileBloodGroupFB)
        textView_gender = view?.findViewById(R.id.tvProfileGenderFB)

        val name = sharedPrefs?.getNameUser()
        textView_name?.text = name
        val phone = sharedPrefs?.getPhoneUser()
        textView_phone?.text = phone
        val city = sharedPrefs?.getCityUser()
        textView_city?.text = city
        val blood = sharedPrefs?.getBloodUser()
        textView_blood?.text = blood
        val gender = sharedPrefs?.getGenderUser()
        textView_gender?.text = gender
        val profilePic = sharedPrefs?.getProfileUser()
        activity?.applicationContext?.let {
            view?.ivProfilePic?.let { it1 ->
                Glide.with(it)
                    .load(profilePic)
                    .placeholder(R.drawable.default_profile_pic)
                    .transform(CircleCrop())
                    .into(it1)
            }
        }
    }
}