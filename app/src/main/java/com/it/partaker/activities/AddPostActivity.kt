package com.it.partaker.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.it.partaker.R
import com.it.partaker.models.User
import kotlinx.android.synthetic.main.activity_add_post.*

class AddPostActivity : AppCompatActivity() {


    private var donationReference : DatabaseReference? = null
    private var requestReference : DatabaseReference? = null
    private var userReference : DatabaseReference? = null
    private var storageRef: StorageReference? = null
    private var firebaseUser : FirebaseUser? = null
    private var imageUri : Uri? = null
    private val requestCode = 438
    private var url: String = "https://firebasestorage.googleapis.com/v0/b/partaker-1fa76.appspot.com/o/download.png?alt=media&token=f4982ae7-c87e-4c19-8cfd-8f2ad26ba8ff"

    override fun onCreate(savedInstanceState: Bundle?) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())
        donationReference = FirebaseDatabase.getInstance().reference.child("donations")
        requestReference = FirebaseDatabase.getInstance().reference.child("requests")
        storageRef = FirebaseStorage.getInstance().reference.child("Post Images")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        ivAddPostImage.setOnClickListener {
            pickImage()
        }

        btnAddPostPost.setOnClickListener {

            val postName: String = etAddPostDonationName.text.toString()
            val postDesc: String  = etAddPostDonationDesc.text.toString()
            val postImage = url

            when{
                TextUtils.isEmpty(postName) -> Toast.makeText(this,"Post Title is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(postDesc) -> Toast.makeText(this,"Post Description is Required", Toast.LENGTH_SHORT).show()
                else -> {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Posting")
                    progressDialog.setMessage("Please Wait It May Take A While")
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()

                    val postId = donationReference!!.push().key.toString()
                    val publisherId = firebaseUser!!.uid

                    val postStatus: String = "Approval Required"
                    val postAssigned : String = "Pending"
                    val requesterId : String = "Not Requested"

                    val postHashMap = HashMap<String, Any>()
                    postHashMap["postId"] = postId
                    postHashMap["name"] = postName
                    postHashMap["desc"] = postDesc
                    postHashMap["image"] = postImage
                    postHashMap["publisherId"] = publisherId
                    postHashMap["status"] = postStatus
                    postHashMap["assigned"] = postAssigned
                    postHashMap["requesterId"] = requesterId

                    userReference!!.addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()){
                                val user = p0.getValue<User>(User::class.java)

                                if(user?.getRegisterAs() == "Donor"){

                                    donationReference!!.child(postId).updateChildren(postHashMap).addOnCompleteListener { it ->
                                        if (it.isSuccessful) {
                                            Toast.makeText(applicationContext, "Post Added", Toast.LENGTH_LONG).show()

                                            val intent = Intent(this@AddPostActivity, MainActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)
                                            //Progress Dialog Dismiss
                                            progressDialog.dismiss()

                                        } //End If Update Children
                                        else {
                                            Toast.makeText(applicationContext,"Donation Post Upload Unsuccessful: " + it.exception!!.toString(),Toast.LENGTH_SHORT).show()
                                            progressDialog.dismiss()
                                        } // End Else Set Value Function
                                    } // End Set Value Function
                                }

                                else {

                                    requestReference!!.child(postId).updateChildren(postHashMap).addOnCompleteListener { it ->
                                        if (it.isSuccessful) {
                                            Toast.makeText(applicationContext, "Post Added", Toast.LENGTH_LONG).show()

                                            val intent = Intent(this@AddPostActivity, MainReceiverActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)
                                            //Progress Dialog Dismiss
                                            progressDialog.dismiss()

                                        } //End If Update Children
                                        else {
                                            Toast.makeText(applicationContext,"Request Post Upload Unsuccessful: " + it.exception!!.toString(),Toast.LENGTH_SHORT).show()
                                            progressDialog.dismiss()
                                        } // End Else Set Value Function
                                    } // End Update Receiver Function

                                } // End Else User is Receiver
                            } // End If Within Data Change
                        } // End On Data Change
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(this@AddPostActivity,"Value Event Listener Failed: ", Toast.LENGTH_LONG).show()
                        }
                    }) // End Value Event Listener

                } // End Else Body of When Block
            } // End When Block
        } // End Function Button Register
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == requestCode && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageUri = data.data
            Glide.with(this)
                .load(imageUri!!)
                .placeholder(R.drawable.default_profile_pic)
                .into(ivAddPostImage)
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
} // End Class