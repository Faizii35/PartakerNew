package com.it.partaker.fragments.ngo

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
import com.it.partaker.adapter.ApproveDonationAdapter
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.fragment_approve_donation.*

class ApproveDonationFragment : Fragment(), MyDonationsClickListener {

    private lateinit var adapter : ApproveDonationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_approve_donation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val donRef = FirebaseDatabase.getInstance().reference.child("donations")

        val manager = LinearLayoutManager(activity)
        rv_Apv_Don_NGO.layoutManager = manager
        adapter = ApproveDonationAdapter(requireContext(), this)
        rv_Apv_Don_NGO.adapter = adapter

        donRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val donationList = mutableListOf<Donation>()

                    for (data in snapshot.children) {
                        val donation = data.getValue(Donation::class.java)
                        donation!!.setPostId(data.key.toString())
                        if (donation.getStatus() == "Approval Required" && donation.getAssigned() == "Pending") {
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
                Toast.makeText(activity?.applicationContext, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun OnMyDonationsItemClickListener(view: View, donation: Donation) {
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.baseFragmentNGO, ApproveDonationDetailFragment(donation))?.commit()
    }
}