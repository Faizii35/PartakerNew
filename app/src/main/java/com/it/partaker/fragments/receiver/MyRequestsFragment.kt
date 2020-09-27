package com.it.partaker.fragments.receiver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.it.partaker.ItemClickListener.MyRequestsClickListener
import com.it.partaker.R
import com.it.partaker.adapter.ReceiverAdapter
import com.it.partaker.classes.Request
import kotlinx.android.synthetic.main.fragment_my_requests.*

class MyRequestsFragment : Fragment(), MyRequestsClickListener {

    private lateinit var adapter : ReceiverAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reqRef = FirebaseDatabase.getInstance().reference.child("requests")
        val userRef = FirebaseAuth.getInstance().currentUser!!

        val manager = LinearLayoutManager(activity)
        rvMRFReceiver.layoutManager = manager
        adapter = ReceiverAdapter(requireContext(), this)
        rvMRFReceiver.adapter = adapter

        reqRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    val requestList = mutableListOf<Request>()

                    for(data in snapshot.children)
                    {
                        val request = data.getValue(Request::class.java)
                        if(request!!.getPublisherId() == userRef.uid){
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
                TODO("Not yet implemented")
            }
        })
    }

    override fun OnMyRequestsItemClickListener(view: View, request: Request) {
        Toast.makeText(context, request.getName(), Toast.LENGTH_SHORT).show()
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.nav_host_fragment, MyRequestsDetailFragment(request))?.commit()
    }
}