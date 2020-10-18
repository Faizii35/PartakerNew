package com.it.partaker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.it.partaker.R
import com.it.partaker.fragments.APIService
import com.it.partaker.models.User
import com.it.partaker.notifications.*
import kotlinx.android.synthetic.main.rv_banned_users_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private var userReference : DatabaseReference? = null
private var firebaseUser : FirebaseUser? = null
private var notify = false
private var receiverId : String = ""
private var title : String = "Account Enabled"
private val message: String = "Your Account Has Been Enabled! You Can Now Use he Partaker App"
private var apiService : APIService? = null

class BannedUserAdapter(val context: Context):RecyclerView.Adapter<BannedUserAdapter.BannedUserViewHolder>()
{
    class BannedUserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private var userList = mutableListOf<User>()

    fun setUsers(user: MutableList<User>){
        userList = user
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannedUserViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.rv_banned_users_item, parent, false)

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        firebaseUser = FirebaseAuth.getInstance().currentUser

        return BannedUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannedUserViewHolder, position: Int) {
        val users = userList[position]
        receiverId = users.getId()

        val textView = holder.itemView.tvRVBannedUserName
        textView.text = users.getFullName()

        val textView2 = holder.itemView.tvRVBannedUserContact
        textView2.text = users.getPhoneNumber()

        Glide.with(context)
            .load(users.getProfilePic())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(holder.itemView.ivRVBannedUserImage)

        holder.itemView.ivBannedUserRemove.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle("Are You Sure To Remove This User From The List?")
                setPositiveButton("Yes") { _, _ ->
                    userReference = FirebaseDatabase.getInstance().reference.child("users").child(users.getId())
                    val reportMap = HashMap<String, Any>()
                    reportMap["reports"] = "0"
                    userReference!!.updateChildren(reportMap).addOnCompleteListener {
                        if(it.isSuccessful) {

                            notify = true

                            if(notify){
                                sendNotification(receiverId, message, title)
                            }
                            notify = false
                            Toast.makeText(
                                context,
                                "User Removed From The List",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                setNegativeButton("Cancel") { _, _ ->
                    Toast.makeText(context,"Process Cancelled", Toast.LENGTH_SHORT).show()
                }
            }.create().show()
        }

    }
    override fun getItemCount(): Int {
        return userList.size
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
                                        Toast.makeText(context, "Failed, Nothing Happened", Toast.LENGTH_SHORT).show()
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