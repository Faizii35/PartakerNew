package com.it.partaker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.it.partaker.ItemClickListener.MyDonationsClickListener
import com.it.partaker.R
import com.it.partaker.models.Donation
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.rv_mdf_donor_item.view.*

class ApproveDonationAdapter(val context: Context, private val myDonationItemClickListener: MyDonationsClickListener):RecyclerView.Adapter<ApproveDonationAdapter.ApproveDonationViewHolder>()
{
    class ApproveDonationViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private var donorList = mutableListOf<Donation>()

    fun setDonations(donation: List<Donation>){
        donorList = donation as MutableList<Donation>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApproveDonationViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.rv_mdf_donor_item, parent, false)

        return ApproveDonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApproveDonationViewHolder, position: Int) {
        val donations = donorList[position]

        if(itemCount<1)
            holder.itemView.tv_Main_Activity_NoPost.visibility = VISIBLE
        else
            holder.itemView.tv_Main_Activity_NoPost.visibility = GONE


        val textView = holder.itemView.tvRVMDFDonorName
        textView.text = donations.getName()

        val textView2 = holder.itemView.tvRVMDFDonorDesc
        textView2.text = donations.getDesc()

        Glide.with(context)
                .load(donations.getImage())
                .transform(CircleCrop())
                .placeholder(R.drawable.default_profile_pic)
                .into(holder.itemView.ivRVMDFDonorItem)

        holder.itemView.setOnClickListener {
            myDonationItemClickListener.OnMyDonationsItemClickListener(it, donations)
        }
    }

    override fun getItemCount(): Int {
      return donorList.size
    }
}