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
import com.it.partaker.classes.Request
import kotlinx.android.synthetic.main.rv_mdf_donor_item.view.*
import kotlinx.android.synthetic.main.rv_mdf_donor_item.view.tvRVMDFDonorName
import kotlinx.android.synthetic.main.rv_mrf_receiver_item.view.*

class ReceiverAdapter(val context: Context):RecyclerView.Adapter<ReceiverAdapter.ReceiverViewHolder>()
{
    class ReceiverViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    var receiverList = mutableListOf<Request>()

    fun setRequests(request: MutableList<Request>){
        receiverList = request as MutableList<Request>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiverViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.rv_mrf_receiver_item, parent, false)
        return ReceiverViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReceiverViewHolder, position: Int) {
        val requests = receiverList[position]
        val name = requests.getName()
        Toast.makeText(context, name, Toast.LENGTH_SHORT).show()

        val textView = holder.itemView.tvRVMRFReceiverName
        textView.text = requests.getName()

        val textView2 = holder.itemView.tvRVMRFReceiverDesc
        textView2.text = requests.getDesc()

        Glide.with(context)
            .load(requests.getImage())
            .into(holder.itemView.ivRVMRFReceiverItem)
    }

    override fun getItemCount(): Int {
        return receiverList.size
    }
}