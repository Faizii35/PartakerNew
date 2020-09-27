package com.it.partaker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.it.partaker.R
import com.it.partaker.classes.Donation
import kotlinx.android.synthetic.main.rv_apv_don_on_click.*
import kotlinx.android.synthetic.main.rv_apv_don_on_click.view.*
import kotlinx.android.synthetic.main.rv_mdf_on_click.*
import kotlinx.android.synthetic.main.rv_mdf_on_click.tv_mdf_on_click_nameFB


class ApproveDonationsDetailFragment(val donation: Donation) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.rv_apv_don_on_click, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Toast.makeText(context, donation.getStatus(), Toast.LENGTH_SHORT).show()
        view.tv_apv_don_on_click_nameFB.text = donation.getName()
        view.tv_apv_don_on_click_descFB.text = donation.getDesc()
        view.tv_apv_don_on_click_donor_nameFB.text = ""
        view.tv_apv_don_on_click_donor_contactFB.text = ""

        Glide.with(requireContext())
            .load(donation.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(view.iv_apv_don_on_click_image)


    }

}