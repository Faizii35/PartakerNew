package com.it.partaker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.it.partaker.R
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.rv_mdf_donor_item.view.*

class DonorAdapter(val context: Context):RecyclerView.Adapter<DonorAdapter.DonorViewHolder>()
{
    class DonorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    var donorList = mutableListOf<Donation>()
    fun setDonations(donation: List<Donation>){
        donorList = donation as MutableList<Donation>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.rv_mdf_donor_item, parent, false)
        return DonorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorViewHolder, position: Int) {
        val donations = donorList[position]
        val name = donations.getName()
        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()

        val textView = holder.itemView.tvRVMDFDonorName
        textView.text = donations.getName()


        val textView2 = holder.itemView.tvRVMDFDonorDesc
        textView2.text = donations.getDesc()

        Glide.with(context)
                .load(donations.getImage())
                .into(holder.itemView.ivRVMDFDonorItem)
    }

    override fun getItemCount(): Int {
      return donorList.size
    }
}