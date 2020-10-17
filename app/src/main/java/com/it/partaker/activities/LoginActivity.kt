package com.it.partaker.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.it.partaker.R
import com.it.partaker.fragments.ForgotPasswordFragment
import com.it.partaker.models.User
import com.it.partaker.persistence.PartakerPrefs
import kotlinx.android.synthetic.main.activity_login.*

private var userReference : DatabaseReference? = null
private var firebaseUser : FirebaseUser? = null

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPrefs = PartakerPrefs(this)
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        tvLoginSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            when {
                TextUtils.isEmpty(email) -> Toast.makeText(
                    this,
                    "Email is Required",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(password) -> Toast.makeText(
                    this,
                    "Password is Required",
                    Toast.LENGTH_SHORT
                ).show()

                else -> {

                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Login")
                    progressDialog.setMessage("Please Wait! It May Take A While")
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {

                            if (email == "m.faizii0634@gmail.com" && password == "Fuck_India69*") {
                                val intent = Intent(this@LoginActivity, HomeNGOActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                finish()
                                progressDialog.dismiss()
                            }
                            else{

                                if (mAuth.currentUser!!.isEmailVerified) {

                                    firebaseUser = FirebaseAuth.getInstance().currentUser
                                    userReference =
                                        FirebaseDatabase.getInstance().reference.child("users").child(
                                            firebaseUser?.uid.toString()
                                        )
                                    userReference!!.addValueEventListener(object :
                                        ValueEventListener {
                                        override fun onDataChange(p0: DataSnapshot) {
                                            if (p0.exists()) {
                                                val user = p0.getValue(User::class.java)

                                                user!!.setReport(p0.child("reports").value.toString())
                                                sharedPrefs.clearUserPref()
                                                sharedPrefs.saveNameUser(user.getFullName())
                                                sharedPrefs.savePhoneUser(user.getPhoneNumber())
                                                sharedPrefs.saveCityUser(user.getCity())
                                                sharedPrefs.saveEmailUser(user.getEmail())
                                                sharedPrefs.saveProfileUser(user.getProfilePic())
                                                sharedPrefs.saveGenderUser(user.getGender())
                                                sharedPrefs.saveBloodUser(user.getBloodGroup())
                                                sharedPrefs.saveReportUser(user.getReport())
                                                sharedPrefs.saveRegisterAsUser(user.getRegisterAs())


                                                if (user.getRegisterAs() == "Donor" && user.getReport().toInt() < 3) {

                                                    Toast.makeText(
                                                        this@LoginActivity,
                                                        "Login Successful",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    progressDialog.dismiss()
                                                    val intent = Intent(
                                                        this@LoginActivity,
                                                        MainActivity::class.java
                                                    )
                                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                    startActivity(intent)
                                                    finish()
                                                } else if (user.getRegisterAs() == "Receiver" && user.getReport()
                                                        .toInt() < 3
                                                ) {
                                                    Toast.makeText(
                                                        this@LoginActivity,
                                                        "Login Successful",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    progressDialog.dismiss()
                                                    val intent = Intent(
                                                        this@LoginActivity,
                                                        MainReceiverActivity::class.java
                                                    )
                                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                    startActivity(intent)
                                                    finish()

                                                } else {

                                                    progressDialog.dismiss()
                                                    AlertDialog.Builder(this@LoginActivity).apply {
                                                        setTitle("Your Account Has Been Disabled!")
                                                        setPositiveButton("OK") { _, _ -> }
                                                    }.create().show()
                                                }
                                            }
                                        }

                                        override fun onCancelled(p0: DatabaseError) {
                                            progressDialog.dismiss()
                                        }
                                    })

                                } // End If Email Is Verified
                                else {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        this,
                                        "Please Verify Your Email First!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } // End Else Is Verified Email

                            }

                        } // End If SignIn

                        else {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
                        } // End Else SignIn

                    } // End SignIn Function

                } // End Else Block

            } //End When Block

        } // End Button Login Function

        tvLoginForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    } // End OnCreate Function

} // End Activity