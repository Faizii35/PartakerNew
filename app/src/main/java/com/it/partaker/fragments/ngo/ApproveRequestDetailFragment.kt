package com.it.partaker.fragments.ngo

import android.content.Intent
import android.net.Uri
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
import com.it.partaker.models.Request
import com.it.partaker.models.User
import com.it.partaker.notifications.*
import kotlinx.android.synthetic.main.rv_apv_req_on_click.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApproveRequestDetailFragment : AppCompatActivity() {

    private var requestReference : DatabaseReference? = null
    private var userReference : DatabaseReference? = null
    private var firebaseUser : FirebaseUser? = null
    private var notify = false
    private var receiverId : String = ""
    private var title : String = "Post (Request) Approval"
    private val message: String = "Your Request Post Has Been Approved!"
    private var apiService : APIService? = null
    private  var reports = "0"
    private var number = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_apv_req_on_click)

        val request = intent.getSerializableExtra("Approve Request") as Request

        requestReference = FirebaseDatabase.getInstance().reference.child("requests").child(request.getPostId())
        userReference = FirebaseDatabase.getInstance().reference.child("users").child(request.getPublisherId())
        firebaseUser = FirebaseAuth.getInstance().currentUser
        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        receiverId = request.getPublisherId()

        userReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val receiver = snapshot.getValue(User::class.java)

                    receiver!!.setReport(snapshot.child("reports").value.toString())

                    tv_apv_req_on_click_receiver_nameFB.text = receiver.getFullName()
                    tv_apv_req_on_click_receiver_contactFB.text = receiver.getPhoneNumber()
                    reports = receiver.getReport()
                }
            }
            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@ApproveRequestDetailFragment, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

        tv_apv_req_on_click_nameFB.text = request.getName()
        tv_apv_req_on_click_descFB.text = request.getDesc()
        tv_apv_req_on_click_descFB.movementMethod = ScrollingMovementMethod()
        Glide.with(this)
            .load(request.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_apv_req_on_click_image)

        iv_apv_req_on_click_receiver_contactFB.setOnClickListener {
            callIntent(number)
        }

        btn_apv_req_on_click_approve.setOnClickListener {
            notify = true
            val reqApv = HashMap<String, Any>()
            reqApv["status"] = "Approved"
            requestReference!!.updateChildren(reqApv)

            if(notify){
                sendNotification(receiverId, message,title)
            }
            notify = false

            Toast.makeText(this@ApproveRequestDetailFragment, "Approved", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, ApproveRequestFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        btn_apv_req_on_click_decline.setOnClickListener {

            AlertDialog.Builder(this).apply {
                setTitle("Are you sure?")
                setPositiveButton("Yes") { _, _ ->

                    val reqApv = HashMap<String, Any>()
                    reqApv["status"] = "Declined"
                    requestReference!!.updateChildren(reqApv)

                    Toast.makeText(this@ApproveRequestDetailFragment, "Declined", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@ApproveRequestDetailFragment, ApproveRequestFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(
                        this@ApproveRequestDetailFragment,
                        "Process Cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.create().show()
        }

        btn_apv_req_on_click_report.setOnClickListener {

            AlertDialog.Builder(this).apply {
                setTitle("Are you sure?")
                setPositiveButton("Yes") { _, _ ->

                    notify = true

                    if(notify && reports.toInt() == 3){
                        sendNotification(receiverId, "Your Account Has Been Disabled Due To Several Reports","Account Reported")
                    }
                    notify = false

                    val recReport = HashMap<String, Any>()
                    recReport["reports"] = (reports.toInt()+1).toString()
                    userReference!!.updateChildren(recReport)

                    val reqApv = HashMap<String, Any>()
                    reqApv["status"] = "Declined"
                    requestReference!!.updateChildren(reqApv)

                    Toast.makeText(this@ApproveRequestDetailFragment, "User Reported and Post Declined", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@ApproveRequestDetailFragment, ApproveRequestFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)

                }
                setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(
                        this@ApproveRequestDetailFragment,
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
                                        Toast.makeText(this@ApproveRequestDetailFragment, "Failed, Nothing Happened", Toast.LENGTH_SHORT).show()
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

    private fun callIntent(number: String){
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.data = Uri.parse("tel:$number")
        startActivity(dialIntent)
    }
}