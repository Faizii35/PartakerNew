package com.it.partaker.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.it.partaker.R
import com.it.partaker.classes.User
import com.it.partaker.fragments.ForgotPasswordFragment
import com.it.partaker.persistence.PartakerPrefs
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

private var userReference : DatabaseReference? = null
private var firebaseUser : FirebaseUser? = null

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val sharedPrefs = PartakerPrefs(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

        tvLoginSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java )
            intent.flags = FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = etLoginEmail.text.toString().trim()
            val password = etLoginPassword.text.toString().trim()

            when {
                TextUtils.isEmpty(email) -> Toast.makeText(this,"Email is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(password) -> Toast.makeText(this,"Password is Required", Toast.LENGTH_SHORT).show()

                else -> {

                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Login")
                    progressDialog.setMessage("Please Wait! It May Take A While")
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (mAuth.currentUser!!.isEmailVerified) {


                                firebaseUser = FirebaseAuth.getInstance().currentUser
                                userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())
                                userReference!!.addValueEventListener(object: ValueEventListener {
                                    override fun onDataChange(p0: DataSnapshot) {
                                        if (p0.exists()){
                                            val user = p0.getValue<User>(User::class.java)
                                            sharedPrefs.saveRegisterAsUser(user!!.getRegisterAs())
                                            Toast.makeText(this@LoginActivity,"User is " + user.getRegisterAs(), Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    override fun onCancelled(p0: DatabaseError) {
                                        Toast.makeText(this@LoginActivity,"Value Event Listener Failed: ", Toast.LENGTH_LONG).show()
                                    }
                                })

                                Toast.makeText(this,"Login Successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)

                            } // End If Email Is Verified
                            else {
                                progressDialog.dismiss()
                                Toast.makeText(this,"Please Verify Your Email First!", Toast.LENGTH_SHORT).show()
                            } // End Else Is Verified Email
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
            supportFragmentManager.beginTransaction().replace(R.id.loginLayout, ForgotPasswordFragment()).commit()
        }

    } // End OnCreate Function
} // End Activity