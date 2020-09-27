package com.it.partaker.fragments.receiver

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.it.partaker.ItemClickListener.MyDonationsClickListener
import com.it.partaker.R
import com.it.partaker.activities.AddPostActivity
import com.it.partaker.adapter.HomeReceiverAdapter
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.fragment_home_receiver.*
import kotlinx.android.synthetic.main.fragment_home_receiver.view.*

class HomeReceiverFragment : Fragment(), MyDonationsClickListener {

    private lateinit var adapter : HomeReceiverAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_receiver, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val donRef = FirebaseDatabase.getInstance().reference.child("donations")

        val manager = LinearLayoutManager(activity)
        rvHRFReceiver.layoutManager = manager
        adapter = HomeReceiverAdapter(requireContext(), this)
        rvHRFReceiver.adapter = adapter

        donRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val donationList = mutableListOf<Donation>()

                    for (data in snapshot.children) {
                        val donation = data.getValue(Donation::class.java)
                        if (donation!!.getStatus() == "Approved") {
                            donation.let {
                                donationList.add(it)
                            }
                        }
                    }
                    donationList.reverse()
                    adapter.setDonations(donationList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })



        view.fa_btn_HRF_add_receiver.setOnClickListener {
            val intent = Intent(context, AddPostActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun OnMyDonationsItemClickListener(view: View, donation: Donation) {
        Toast.makeText(context, donation.getName(), Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.nav_host_fragment, HomeReceiverDetailFragment(donation))?.commit()

    }
}