package com.it.partaker.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.it.partaker.R
import com.it.partaker.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : AppCompatActivity() {

    private var userReference : DatabaseReference? = null
    private var firebaseUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser?.uid.toString())


        btnCPConfirm.setOnClickListener {

            val currentPass = etCPCurrentPassword.text.toString()
            val newPass = etCPNewPassword.text.toString()
            val confirmNewPass = etCPConfirmNewPassword.text.toString()

            when{
                TextUtils.isEmpty(currentPass) -> Toast.makeText(this,"Current Password is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(newPass) -> Toast.makeText(this,"Password is Required", Toast.LENGTH_SHORT).show()
                TextUtils.isEmpty(confirmNewPass) -> Toast.makeText(this,"Confirm Password is Required", Toast.LENGTH_SHORT).show()

                currentPass == newPass -> Toast.makeText(this,"New & Current Password Shouldn't Be Same.", Toast.LENGTH_LONG).show()
                confirmNewPass != newPass -> Toast.makeText(this,"New & Confirm Password Doesn't Match.", Toast.LENGTH_LONG).show()

                else->{
                    firebaseUser?.updatePassword(newPass)?.addOnCompleteListener {
                        if(it.isSuccessful) {
                            val user = HashMap<String, Any>()
                            user["password"] = newPass

                            userReference?.updateChildren(user)?.addOnCompleteListener {
                                if(it.isSuccessful){
                                    Toast.makeText(this,"Password Successfully Updated", Toast.LENGTH_SHORT).show()
                                        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, ProfileFragment()).commit()
                                }
                                else {
                                    Toast.makeText(this,"Error: " + it.exception.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else {
                            Toast.makeText(this,"Password Didn't Updated: ${it.exception.toString()}", Toast.LENGTH_SHORT).show()
                        }
                    } // End Update Password
                } // End Else Block
            } // End When Block
        }
    }
}