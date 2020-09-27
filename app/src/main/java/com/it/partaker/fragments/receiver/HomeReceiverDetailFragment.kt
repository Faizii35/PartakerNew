package com.it.partaker.fragments.receiver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.it.partaker.R
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.rv_hrf_on_click.*


class HomeReceiverDetailFragment(val donation: Donation) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.rv_hrf_on_click, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Toast.makeText(context, donation.getStatus(), Toast.LENGTH_SHORT).show()
        tv_hrf_on_click_nameFB.text = donation.getName()
        tv_hrf_on_click_descFB.text = donation.getDesc()

        Glide.with(requireContext())
            .load(donation.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_hrf_on_click_image)

    }

}