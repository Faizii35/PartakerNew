package com.it.partaker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.classes.Donation
import com.it.partaker.classes.Request
import kotlinx.android.synthetic.main.rv_hdf_donor_item.view.*
import kotlinx.android.synthetic.main.rv_hrf_receiver_item.view.*
import kotlinx.android.synthetic.main.rv_mdf_donor_item.view.*
import kotlinx.android.synthetic.main.rv_mdf_donor_item.view.tvRVMDFDonorName
import kotlinx.android.synthetic.main.rv_mrf_receiver_item.view.*

class HomeDonorAdapter(val context: Context,val HomeDonorItemClickListener: MyRequestsClickListener):RecyclerView.Adapter<HomeDonorAdapter.HomeDonorViewHolder>()
{
    class HomeDonorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    var receiverList = mutableListOf<Request>()

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
        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()

        val textView = holder.itemView.tvRVHDFDonorName
        textView.text = requests.getName()

        val textView2 = holder.itemView.tvRVHDFDonorDesc
        textView2.text = requests.getDesc()

        Glide.with(context)
            .load(requests.getImage())
            .transform(CircleCrop())
            .placeholder(R.drawable.default_profile_pic)
            .into(holder.itemView.ivRVHDFDonorItem)

        holder.itemView.setOnClickListener(){

            HomeDonorItemClickListener.OnMyRequestsItemClickListener(it, requests)

        }
    }

    override fun getItemCount(): Int {
        return receiverList.size
    }
}