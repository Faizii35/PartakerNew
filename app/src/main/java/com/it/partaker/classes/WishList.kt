package com.it.partaker.classes

import java.io.Serializable

data class WishList(
    private var wish_id: String,
    private var p_id: String = "",
    private var p_isLiked: Boolean = false,
    private var wisher_id : String = ""
):Serializable {

    fun WishList(){}
    fun WishList( wishId:String, postId: String, postIsLiked: Boolean,wisherId: String){
        this.wish_id = wishId
        this.p_id = postId
        this.p_isLiked = postIsLiked
        this.wisher_id = wisherId
    }

    fun setPostId(po_id: String){
        this.p_id = po_id
    }

    fun getWishId() : String{ return wish_id }
    fun getWishedPostId() : String { return p_id }
    fun getWisherId(): String {return wisher_id}
    fun getPostIsLiked(): Boolean{ return p_isLiked}
}