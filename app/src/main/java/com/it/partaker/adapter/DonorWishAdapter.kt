package com.it.partaker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.classes.Request
import com.it.partaker.classes.WishList
import kotlinx.android.synthetic.main.rv_hdf_donor_item.view.*

private var firebaseUser : FirebaseUser? = null
private var wishReference : DatabaseReference? = null

class DonorWishAdapter(val context: Context, private val HomeDonorItemClickListener: MyRequestsClickListener):RecyclerView.Adapter<DonorWishAdapter.DonorWishViewHolder>()
{

    class DonorWishViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    var wishList = mutableListOf<WishList>()
    private var liked = false

    fun setWish(wish: MutableList<WishList>){
        wishList = wish as MutableList<WishList>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonorWishViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.rv_hdf_donor_item, parent, false)
        return DonorWishViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonorWishViewHolder, position: Int) {
        val wish = wishList[position]
        val wishedId = wish.getWishedPostId()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        wishReference = FirebaseDatabase.getInstance().reference.child("wishList").child(wish.getWisherId()).child(wishedId)

        wishReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Toast.makeText(context, wish.getPostIsLiked().toString(), Toast.LENGTH_SHORT).show()
                if (snapshot.exists())
                {
                    val requests = snapshot.getValue(Request::class.java)

                    holder.itemView.tvRVHDFDonorName.text = requests!!.getName()
                    holder.itemView.tvRVHDFDonorDesc.text = requests.getDesc()

                    Glide.with(context)
                        .load(requests.getImage())
                        .circleCrop()
                        .placeholder(R.drawable.default_profile_pic)
                        .into(holder.itemView.ivRVHDFDonorItem)

                    holder.itemView.setOnClickListener(){
                        HomeDonorItemClickListener.OnMyRequestsItemClickListener(it, requests)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        holder.itemView.ivHDFWish.setOnClickListener {
            firebaseUser = FirebaseAuth.getInstance().currentUser
            wishReference = FirebaseDatabase.getInstance().reference.child("wishList")

            val wishMap = HashMap<String, Any>()

            if(liked){
                liked = false
                holder.itemView.ivHDFWish.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                wishMap["postIsLiked"] = liked
                wishReference!!.child(firebaseUser!!.uid).child(wish.getWishedPostId()).updateChildren(wishMap)
            }
            else
            {
                liked  = true
                holder.itemView.ivHDFWish.setImageResource(R.drawable.ic_baseline_favorite_24)
                wishMap["isLiked"] = liked
                wishReference!!.child(firebaseUser!!.uid).child(wish.getWishedPostId()).updateChildren(wishMap)
            }
        }
//        val textView = holder.itemView.tvRVHDFDonorName
//        textView.text = requests.getName()
//
//        val textView2 = holder.itemView.tvRVHDFDonorDesc
//        textView2.text = requests.getDesc()
//
//        Glide.with(context)
//            .load(requests.getImage())
//            .circleCrop()
//            .placeholder(R.drawable.default_profile_pic)
//            .into(holder.itemView.ivRVHDFDonorItem)
//
//        holder.itemView.ivHDFWish.setOnClickListener {
//            firebaseUser = FirebaseAuth.getInstance().currentUser
//            wishReference = FirebaseDatabase.getInstance().reference.child("wishList")
//
//            val wishMap = HashMap<String, Any>()
//            wishMap["wishId"] = requests.getPostId()
//
//            if(liked){
//                liked = false
//                holder.itemView.ivHDFWish.setImageResource(R.drawable.ic_baseline_favorite_border_24)
//                wishMap["postIsLiked"] = liked
//                wishReference!!.child(firebaseUser!!.uid).child(requests.getPostId()).updateChildren(wishMap)
//            }
//            else
//            {
//                liked  = true
//                holder.itemView.ivHDFWish.setImageResource(R.drawable.ic_baseline_favorite_24)
//                wishMap["isLiked"] = liked
//                wishReference!!.child(firebaseUser!!.uid).child(requests.getPostId()).updateChildren(wishMap)
//            }
//        }
//

    }

    override fun getItemCount(): Int {
        return wishList.size
    }
}