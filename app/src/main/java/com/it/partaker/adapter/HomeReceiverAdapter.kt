package com.it.partaker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.it.partaker.ItemClickListener.MyDonationsClickListener
import com.it.partaker.R
import com.it.partaker.models.Donation
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.rv_hrf_receiver_item.view.*

class HomeReceiverAdapter(val context: Context, private val donationItemClickListener: MyDonationsClickListener):RecyclerView.Adapter<HomeReceiverAdapter.HomeReceiverViewHolder>()
{

    class HomeReceiverViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    
    private var firebaseUser : FirebaseUser? = null
    private var donorList = mutableListOf<Donation>()

    fun setDonations(donation: List<Donation>){
        donorList = donation as MutableList<Donation>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeReceiverViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.rv_hrf_receiver_item, parent, false)
        return HomeReceiverViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeReceiverViewHolder, position: Int) {
        val donations = donorList[position]
        firebaseUser = FirebaseAuth.getInstance().currentUser

        if(itemCount<1)
            holder.itemView.tv_Main_Activity_NoPost.visibility = View.VISIBLE
        else
            holder.itemView.tv_Main_Activity_NoPost.visibility = View.GONE

        val textView = holder.itemView.tvRVHRFReceiverName
        textView.text = donations.getName()

        val textView2 = holder.itemView.tvRVHRFReceiverDesc
        textView2.text = donations.getDesc()

        Glide.with(context)
                .load(donations.getImage())
                .transform(CircleCrop())
                .placeholder(R.drawable.default_profile_pic)
                .into(holder.itemView.ivRVHRFReceiverItem)

        if(donations.getRequesterId() == firebaseUser!!.uid){
            holder.itemView.ivHRFReq.visibility = View.VISIBLE
        }
        else{
            holder.itemView.ivHRFReq.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            donationItemClickListener.OnMyDonationsItemClickListener(it, donations)
        }
    }

    override fun getItemCount(): Int {
      return donorList.size
    }
}