package com.it.partaker.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.it.partaker.R
import com.it.partaker.fragments.ProfileFragment
import com.it.partaker.persistence.PartakerPrefs
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlin.collections.set

class EditProfileActivity : AppCompatActivity() {

    private var userReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null
    private var bloodGroup: String = ""
    private var url: String = "https://firebasestorage.googleapis.com/v0/b/partaker-1fa76.appspot.com/o/download.png?alt=media&token=f4982ae7-c87e-4c19-8cfd-8f2ad26ba8ff"


    private var imageUri : Uri? = null
    private val RequestCode = 438

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val sharedPrefs = PartakerPrefs(this@EditProfileActivity)

        val categories = ArrayList<String>()
        categories.add(0,sharedPrefs.getBloodUser().toString())
        categories.add(1,"A+")
        categories.add(2,"A-")
        categories.add(3,"B+")
        categories.add(4,"B-")
        categories.add(5,"AB+")
        categories.add(6,"AB-")
        categories.add(7,"O+")
        categories.add(8,"O-")

        val dataAdapter : ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnEditProfileBloodGroup.adapter = dataAdapter

        spnEditProfileBloodGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                bloodGroup = sharedPrefs.getBloodUser().toString()
                Toast.makeText(this@EditProfileActivity,"None Selected",Toast.LENGTH_LONG).show()
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) =
                if(parent?.getItemAtPosition(position).toString().equals("Not Known")){
                    bloodGroup = "Not Known"
                }
                else {
                    val item  = parent?.getItemAtPosition(position).toString()
                    bloodGroup = item
                }
        }

        etEditProfileName.setText(sharedPrefs.getNameUser())
        etEditProfilePhoneNum.setText(sharedPrefs.getPhoneUser())
        etEditProfileCity.setText(sharedPrefs.getCityUser())
        tvEditProfileBloodGroupFB.text = sharedPrefs.getBloodUser()
        Glide.with(this)
            .load(sharedPrefs.getProfileUser())
            .placeholder(R.drawable.default_profile_pic)
            .transform(CircleCrop())
            .into(ivEditProfilePic)


        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())

        ivEditProfilePic.setOnClickListener{
            pickImage()
        }

        btnEditProfileConfirm.setOnClickListener {

            val name = etEditProfileName.text.toString()
            val phone =  etEditProfilePhoneNum.text.toString()
            val city = etEditProfileCity.text.toString()
            val blood = bloodGroup

            when{
//                imageUri == null -> Toast.makeText(this, "Image is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(name) -> Toast.makeText(this, "Name is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(phone) -> Toast.makeText(this, "Phone Number is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(city) -> Toast.makeText(this, "City is Required", Toast.LENGTH_SHORT).show()

                else->{

                    val progressBar = ProgressDialog(this)
                    progressBar.setTitle("Updating Profile")
                    progressBar.setCanceledOnTouchOutside(false)
                    progressBar.setMessage("Updating Profile. Please Wait A While")
                    progressBar.show()

                    sharedPrefs.saveNameUser(name)
                    sharedPrefs.savePhoneUser(phone)
                    sharedPrefs.saveCityUser(city)
                    sharedPrefs.saveBloodUser(blood)
                    sharedPrefs.saveProfileUser(url)

                    val user = HashMap<String,Any>()
                    user["fullName"] = name
                    user["phoneNumber"] = phone
                    user["city"] = city
                    user["bloodGroup"] = blood
                    user["profilePic"] = url

                    userReference!!.updateChildren(user).addOnCompleteListener {
                        if(it.isSuccessful)
                        {
                            Toast.makeText(this@EditProfileActivity, "Profile Updated", Toast.LENGTH_SHORT).show()
                            progressBar.dismiss()
                            val intent = Intent(this@EditProfileActivity, ProfileFragment::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)

                        }
                        else{
                            progressBar.dismiss()
                            Toast.makeText(this, "Update Unsuccessful", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

            }

        } // End Confirm Button Click Listener
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RequestCode && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageUri = data.data
            ivEditProfilePic.setImageURI(imageUri)
            uploadImageDatabase()
        }
    }

    private fun uploadImageDatabase() {

        if(imageUri!= null) {
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.addOnCompleteListener {
                if (it.isSuccessful) {
                    url = it.result.toString()
                    fileRef.downloadUrl.addOnCompleteListener { it1: Task<Uri> ->
                        if (it1.isSuccessful) {
                            url = it1.result.toString()
                        }
                        else{
                            Toast.makeText(this, "Error: "+ it.exception.toString(), Toast.LENGTH_LONG).show()
                        } // End Else Upload Task Complete Listener
                    } // End Download Url On Complete Listener
                } // End If Upload Task is Successful
            } // End Upload Task Complete Listener
        } // End If Image Uri is Not Equals To Null
    } // End Upload Image Database Function

}