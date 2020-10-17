package com.it.partaker.models

import java.io.Serializable

data class Donation(
    private var post_id: String = "",
    private var name : String = "",
    private var desc : String = "",
    private var image : String = "",
    private var publisherId: String = "",
    private var status: String = "",
    private var assigned: String = "",
    private var requesterId : String = ""
):Serializable {

    fun Donation(){}
    fun Donation(
        postId: String, name: String, desc : String, image: String,
        publisherId: String, status: String, assigned: String, requesterId: String)
    {
        this.post_id = postId
        this.name = name
        this.desc = desc
        this.image = image
        this.publisherId = publisherId
        this.status = status
        this.assigned = assigned
        this.requesterId = requesterId
    }

    fun setPostId(po_id: String){
        this.post_id = po_id
    }

    fun getPostId() : String { return post_id }
    fun getName(): String{ return name }
    fun getDesc(): String{ return desc }
    fun getImage(): String{ return image }
    fun getPublisherId(): String {return publisherId}
    fun getStatus(): String{ return status}
    fun getAssigned() : String{ return assigned }
    fun getRequesterId() : String{return requesterId }

}