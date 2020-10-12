package com.it.partaker.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.text.TextUtils
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.it.partaker.R
import com.it.partaker.models.User
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var refUsers: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        //Intent For Sign In Activity
        tvRegisterSignIn.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java )
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        var gender = "Male"
        var registerAs = "Donor"

        //Gender Radio Button Value
        rgGender.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            gender = radio.text.toString()
        }

        //Register As Radio Button Value
        rgRegisterAs.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = findViewById(checkedId)
            registerAs = radio.text.toString()
        }

        //Register Button Click
        btnRegister.setOnClickListener {
            val fullName: String = etFullName.text.toString().trim()
            val phoneNumber: String  = etPhoneNumber.text.toString().trim()
            val city: String  = etCity.text.toString().trim()
            val email: String  = etEmail.text.toString().trim()
            val password: String  = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()
            val bloodGroup = "Not Known"
            val profilePic = "https://firebasestorage.googleapis.com/v0/b/partaker-1fa76.appspot.com/o/download.png?alt=media&token=f4982ae7-c87e-4c19-8cfd-8f2ad26ba8ff"

            when{
                TextUtils.isEmpty(fullName) -> Toast.makeText(this,"Full Name is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(phoneNumber) -> Toast.makeText(this,"Phone Number is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(city) -> Toast.makeText(this,"City is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(email) -> Toast.makeText(this,"Email is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(password) -> Toast.makeText(this,"Password is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(confirmPassword) -> Toast.makeText(this,"Confirm Password is Required", Toast.LENGTH_SHORT).show()
                confirmPassword!= password -> Toast.makeText(this,"Password & Confirm Password Should Be Same", Toast.LENGTH_SHORT).show()
                else -> {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Registration")
                    progressDialog.setMessage("Please Wait It May Take A While")
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener {
                                if(it.isSuccessful){
                                    val userID = mAuth.currentUser!!.uid
                                    refUsers = FirebaseDatabase.getInstance().reference.child("users").child(userID)

                                    val user = User(userID,fullName,phoneNumber,city,email,password,gender,registerAs,bloodGroup,profilePic)
                                    refUsers.setValue(user).addOnCompleteListener { it1 ->
                                        if (it1.isSuccessful) {
                                            //Progress Dialog Dismiss
                                            progressDialog.dismiss()

                                            val pdVerify = ProgressDialog(this)
                                            pdVerify.setTitle("Verify Your Email")
                                            pdVerify.setMessage("Please Check Your Email For Verification. (Click Outside Of Box To Dismiss Message)")
                                            pdVerify.show()
                                            pdVerify.setCanceledOnTouchOutside(true)
                                            pdVerify.setOnCancelListener {
                                                val intent = Intent(this, LoginActivity::class.java )
                                                intent.flags = FLAG_ACTIVITY_CLEAR_TOP
                                                startActivity(intent)
                                            }

                                        } //End If Update Children
                                    } // End Update Children Function
                                } // End If Send Verification Email
                                else{
                                    Toast.makeText(this, "Verification Email Not Sent: " + it.exception.toString(), Toast.LENGTH_SHORT).show()
                                    progressDialog.dismiss()
                                } // End Else Send Verification Email
                            } // End Send Verification Email Function
                        } // End If Create User
                        else {
                            Toast.makeText(this,"Registration Unsuccessful: " + task.exception!!.toString(),Toast.LENGTH_SHORT).show()
                            progressDialog.dismiss()
                        } // End Else Create User
                    }
                }
            }
             // End Create User Function
        } // End Function Button Register
    } // End On Create
} // End Class Activity