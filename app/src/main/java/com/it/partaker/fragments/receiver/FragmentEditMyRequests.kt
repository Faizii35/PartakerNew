package com.it.partaker.fragments.receiver

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.FirebaseDatabase
import com.it.partaker.R
import com.it.partaker.models.Request
import kotlinx.android.synthetic.main.fragment_edit_my_donation.view.*
import kotlinx.android.synthetic.main.fragment_edit_my_requests.*
import kotlinx.android.synthetic.main.fragment_edit_my_requests.view.*


class FragmentEditMyRequests(val request : Request) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_my_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.etEditDonDescription.movementMethod = ScrollingMovementMethod()

        val title = request.getName()
        val desc = request.getDesc()

        view.etEditReqTitle.setText(title)
        view.etEditReqDescription.setText(desc)

        btnEditReqUpdate.setOnClickListener {

            val titleNew = etEditReqTitle.text.toString()
            val descNew = etEditReqDescription.text.toString()

            val editHash = HashMap<String, Any>()
            editHash["name"] = titleNew
            editHash["desc"] = descNew

            FirebaseDatabase.getInstance().reference.child("requests").child(request.getPostId()).updateChildren(editHash).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    dismiss()

                    val intent = Intent(context, MyRequestsFragment::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                else{
                    dismiss()
                    Toast.makeText(context, "Update Unsuccessful", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}