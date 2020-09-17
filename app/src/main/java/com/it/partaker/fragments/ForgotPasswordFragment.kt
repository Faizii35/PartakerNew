package com.it.partaker.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.it.partaker.R
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.android.synthetic.main.fragment_forgot_password.view.*

class ForgotPasswordFragment : Fragment() {

    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_forgot_password, container, false)

        view.btnResetPassword.setOnClickListener {

            val recEmail =  etRecoveryEmail.text.toString().trim()

            when{
                TextUtils.isEmpty(recEmail) -> Toast.makeText(context,"Password Reset Email Required", Toast.LENGTH_LONG).show()
                else ->{
                    mAuth.sendPasswordResetEmail(recEmail).addOnCompleteListener {
                        if(it.isSuccessful) {
                            Toast.makeText(context,"Password Reset Email Sent: $recEmail", Toast.LENGTH_LONG).show()
                        }
                        else {
                            Toast.makeText(context,"Error: " + it.exception, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        return view
    }
}