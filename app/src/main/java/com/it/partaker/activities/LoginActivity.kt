package com.it.partaker.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.it.partaker.R
import com.it.partaker.fragments.ForgotPasswordFragment
import com.it.partaker.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_forgot_password.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

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
                                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                                etLoginEmail.setText("")
                                etLoginPassword.setText("")

                                progressDialog.dismiss()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            }
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