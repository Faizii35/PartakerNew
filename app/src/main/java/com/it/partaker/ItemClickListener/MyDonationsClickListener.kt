package com.it.partaker.ItemClickListener

import android.view.View
import com.it.partaker.models.Donation

interface MyDonationsClickListener {

    fun OnMyDonationsItemClickListener(view: View, donation: Donation)
}