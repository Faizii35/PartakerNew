package com.it.partaker.fragments.donor

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.it.partaker.R
import com.it.partaker.models.Request
import kotlinx.android.synthetic.main.rv_frd_on_click.*


class FulfillRequestDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rv_frd_on_click)


        val request = intent.getSerializableExtra("Fulfill Request") as Request

        tv_frd_on_click_nameFB.text = request.getName()
        tv_frd_on_click_descFB.text = request.getDesc()
        tv_frd_on_click_descFB.movementMethod = ScrollingMovementMethod()

        Glide.with(this)
            .load(request.getImage())
            .circleCrop()
            .placeholder(R.drawable.default_profile_pic)
            .into(iv_frd_on_click_image)


    }

}