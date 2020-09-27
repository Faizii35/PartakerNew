package com.it.partaker.fragments.donor

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
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.activities.AddPostActivity
import com.it.partaker.adapter.HomeDonorAdapter
import com.it.partaker.classes.Request
import kotlinx.android.synthetic.main.fragment_home_donor.*
import kotlinx.android.synthetic.main.fragment_home_donor.view.*

class HomeDonorFragment : Fragment(), MyRequestsClickListener {

    private lateinit var adapter : HomeDonorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_donor, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reqRef = FirebaseDatabase.getInstance().reference.child("requests")

        val manager = LinearLayoutManager(activity)
        rvHDFDonor.layoutManager = manager
        adapter = HomeDonorAdapter(requireContext(), this)
        rvHDFDonor.adapter = adapter

        reqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val requestList = mutableListOf<Request>()

                    for (data in snapshot.children) {
                        val request = data.getValue(Request::class.java)
                        if (request!!.getStatus() == "Approved" && request.getAssigned() == "Pending") {
                            request.let {
                                requestList.add(it)
                            }
                        }
                    }
                    requestList.reverse()
                    adapter.setRequests(requestList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        })

        view.fa_btn_HDF_add_donation.setOnClickListener {
            val intent = Intent(context, AddPostActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun OnMyRequestsItemClickListener(view: View, request: Request) {
        Toast.makeText(context, request.getName(), Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.nav_host_fragment, HomeDonorDetailFragment(request))?.commit()
    }
}