package com.it.partaker.classes

data class Donation(
    private var d_id: String = "",
    private var name : String = "",
    private var desc : String = "",
    private var image : String = "",
    private var donorId: String = "",
    private var status: String = "",
    private var assigned: String = ""
) {

    fun Donation(){}
    fun Donation( d_id: String, name: String, desc : String, image: String, donorId: String, status: String, assigned: String){
        this.d_id = d_id
        this.name = name
        this.desc = desc
        this.image = image
        this.donorId = donorId
        this.status = status
        this.assigned = assigned
    }

    fun getDonationId() : String { return d_id }
    fun getName(): String{ return name }
    fun getDesc(): String{ return desc }
    fun getImage(): String{ return image }
    fun getDonorId(): String {return donorId}
    fun getStatus(): String{ return status}
    fun getAssigned() : String{return assigned}
}