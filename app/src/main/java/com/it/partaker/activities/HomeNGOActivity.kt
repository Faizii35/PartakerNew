package com.it.partaker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.it.partaker.R
import com.it.partaker.fragments.*
import kotlinx.android.synthetic.main.activity_home_n_g_o.*

class HomeNGOActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_n_g_o)


        btnApproveDonation.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.baseFragmentNGO, ApproveDonationFragment()).commit()
        }

        btnApproveRequest.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.baseFragmentNGO, ApproveRequestFragment()).commit()
        }

        btnApproveRequestToDonation.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.baseFragmentNGO, ApproveDonorRequestFragment()).commit()
        }

        btnApproveRequestForDonation.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.baseFragmentNGO, ApproveReceiverRequestFragment()).commit()
        }

    }
}