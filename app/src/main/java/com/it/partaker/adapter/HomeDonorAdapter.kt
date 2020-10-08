package com.it.partaker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.classes.Request
import kotlinx.android.synthetic.main.rv_hdf_donor_item.view.*

private var firebaseUser : FirebaseUser? = null
private var wishReference : DatabaseReference? = null

class HomeDonorAdapter(val context: Context, private val HomeDonorItemClickListener: MyRequestsClickListener):RecyclerView.Adapter<HomeDonorAdapter.HomeDonorViewHolder>()
{

    class HomeDonorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    var receiverList = mutableListOf<Request>()
    private var liked = false

    fun setRequests(request: MutableList<Request>){
        receiverList = request as MutableList<Request>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeDonorViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.rv_hdf_donor_item, parent, false)
        return HomeDonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeDonorViewHolder, position: Int) {
        val requests = receiverList[position]
        val name = requests.getName()

        val textView = holder.itemView.tvRVHDFDonorName
        textView.text = requests.getName()

        val textView2 = holder.itemView.tvRVHDFDonorDesc
        textView2.text = requests.getDesc()

        Glide.with(context)
            .load(requests.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(holder.itemView.ivRVHDFDonorItem)

        holder.itemView.ivHDFWish.setOnClickListener {
            firebaseUser = FirebaseAuth.getInstance().currentUser
            wishReference = FirebaseDatabase.getInstance().reference.child("wishList")

            val wishMap = HashMap<String, Any>()
            wishMap["wishId"] = requests.getPostId()

            if(liked){
                liked = false
                holder.itemView.ivHDFWish.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                wishMap["postIsLiked"] = liked
                wishReference!!.child(firebaseUser!!.uid).child(requests.getPostId()).updateChildren(wishMap)
            }
            else
            {
                liked  = true
                holder.itemView.ivHDFWish.setImageResource(R.drawable.ic_baseline_favorite_24)
                wishMap["isLiked"] = liked
                wishReference!!.child(firebaseUser!!.uid).child(requests.getPostId()).updateChildren(wishMap)
            }
        }

        holder.itemView.setOnClickListener(){

            HomeDonorItemClickListener.OnMyRequestsItemClickListener(it, requests)

        }
    }

    override fun getItemCount(): Int {
        return receiverList.size
    }
}