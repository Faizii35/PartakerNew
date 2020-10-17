package com.it.partaker.fragments.ngo

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.it.partaker.R
import com.it.partaker.fragments.APIService
import com.it.partaker.models.Donation
import com.it.partaker.models.User
import com.it.partaker.notifications.*
import kotlinx.android.synthetic.main.rv_apv_don_on_click.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ApproveDonationDetailFragment : AppCompatActivity() {

    private var donationReference : DatabaseReference? = null
    private var userReference : DatabaseReference? = null
    private var firebaseUser : FirebaseUser? = null
    private var notify = false
    private var receiverId : String = ""
    private var title : String = "Post Approval"
    private val message: String = "Your Post Has Been Approved!"
    private var apiService : APIService? = null
    private var reports : String= "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_apv_don_on_click)

        val donation = intent.getSerializableExtra("Approve Donation") as Donation

        donationReference = FirebaseDatabase.getInstance().reference.child("donations").child(donation.getPostId())
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(donation.getPublisherId())
        firebaseUser = FirebaseAuth.getInstance().currentUser
        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        receiverId = donation.getPublisherId()


        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val donor = snapshot.getValue(User::class.java)

                    donor!!.setReport(snapshot.child("reports").value.toString())

                    tv_apv_don_on_click_donor_nameFB.text = donor.getFullName()
                    tv_apv_don_on_click_donor_contactFB.text = donor.getPhoneNumber()
                    reports = donor.getReport()
                    Toast.makeText(this@ApproveDonationDetailFragment, reports, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ApproveDonationDetailFragment,
                    "Error: $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        tv_apv_don_on_click_nameFB.text = donation.getName()
        tv_apv_don_on_click_descFB.text = donation.getDesc()
        tv_apv_don_on_click_descFB.movementMethod = ScrollingMovementMethod()
        Glide.with(this)
            .load(donation.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_apv_don_on_click_image)


        btn_apv_don_on_click_approve.setOnClickListener {
            notify = true
            val donApv = HashMap<String, Any>()
            donApv["status"] = "Approved"
            donationReference!!.updateChildren(donApv)

            if(notify){
                sendNotification(receiverId, message, title)
            }
            notify = false

            Toast.makeText(this@ApproveDonationDetailFragment, "Approved", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ApproveDonationFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btn_apv_don_on_click_decline.setOnClickListener {

            AlertDialog.Builder(this).apply {
                setTitle("Are you sure?")
                setPositiveButton("Yes") { _, _ ->

                    val donApv = HashMap<String, Any>()
                    donApv["status"] = "Declined"
                    donationReference!!.updateChildren(donApv)

                    Toast.makeText(this@ApproveDonationDetailFragment, "Declined", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@ApproveDonationDetailFragment, ApproveDonationFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(
                        this@ApproveDonationDetailFragment,
                        "Process Cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.create().show()

        }

        btn_apv_don_on_click_report.setOnClickListener {

            AlertDialog.Builder(this).apply {
                setTitle("Are you sure?")
                setPositiveButton("Yes") { _, _ ->

                    notify = true

                    if(notify && reports.toInt() == 3){
                        sendNotification(receiverId, "Your Account Has Been Disabled Due To Several Reports","Account Reported")
                    }
                    notify = false

                    val reportTest = (reports.toInt())+1

                    val donReport = HashMap<String, Any>()
                    donReport["reports"] = reportTest.toString()
                    userReference!!.updateChildren(donReport)

                    val donApv = HashMap<String, Any>()
                    donApv["status"] = "Declined"
                    donationReference!!.updateChildren(donApv)

                    Toast.makeText(this@ApproveDonationDetailFragment, "User Reported And Post Declined", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@ApproveDonationDetailFragment, ApproveDonationFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)

                }
                setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(
                        this@ApproveDonationDetailFragment,
                        "Process Cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.create().show()

        }

        updateToken(FirebaseInstanceId.getInstance().token)
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun sendNotification(receiverId: String, message: String,title:String) {

        val reference = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = reference.orderByKey().equalTo(receiverId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (datasnapshot in p0.children) {
                    val token: Token? = datasnapshot.getValue(Token::class.java)
                    val data = Data(
                        firebaseUser!!.uid,
                        R.mipmap.ic_launcher,
                        message,
                        title,
                        receiverId
                    )

                    val sender = Sender(data, token!!.getToken().toString())
                    apiService!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()!!.success !== 1) {
                                        Toast.makeText(this@ApproveDonationDetailFragment, "Failed, Nothing Happened", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                            }
                        })
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

}