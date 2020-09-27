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
import com.it.partaker.classes.Request
import kotlinx.android.synthetic.main.rv_mrf_receiver_item.view.*

class ApproveRequestAdapter(val context: Context,  val myRequestItemClickListener: MyRequestsClickListener):RecyclerView.Adapter<ApproveRequestAdapter.ApproveRequestViewHolder>()
{
    class ApproveRequestViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    var receiverList = mutableListOf<Request>()

    fun setRequests(request: MutableList<Request>){
        receiverList = request as MutableList<Request>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApproveRequestViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.rv_mrf_receiver_item, parent, false)
        return ApproveRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApproveRequestViewHolder, position: Int) {
        val requests = receiverList[position]
        val name = requests.getName()
        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()

        val textView = holder.itemView.tvRVMRFReceiverName
        textView.text = requests.getName()

        val textView2 = holder.itemView.tvRVMRFReceiverDesc
        textView2.text = requests.getDesc()

        Glide.with(context)
            .load(requests.getImage())
            .transform(CircleCrop())
            .placeholder(R.drawable.default_profile_pic)
            .into(holder.itemView.ivRVMRFReceiverItem)

        holder.itemView.setOnClickListener(){
            myRequestItemClickListener.OnMyRequestsItemClickListener(it, requests)
        }
    }

    override fun getItemCount(): Int {
        return receiverList.size
    }
}