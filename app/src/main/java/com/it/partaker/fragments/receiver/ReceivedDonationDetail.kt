package com.it.partaker.fragments.receiver

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.it.partaker.R
import com.it.partaker.models.Donation
import kotlinx.android.synthetic.main.rv_rd_on_click.*


class ReceivedDonationDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_rd_on_click)


        val donation = intent.getSerializableExtra("Received Donation") as Donation

        tv_rd_on_click_nameFB.text = donation.getName()
        tv_rd_on_click_descFB.text = donation.getDesc()
        tv_rd_on_click_descFB.movementMethod = ScrollingMovementMethod()

        Glide.with(this)
            .load(donation.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_rd_on_click_image)


    }

}