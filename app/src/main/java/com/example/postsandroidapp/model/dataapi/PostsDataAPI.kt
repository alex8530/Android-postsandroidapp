package com.example.postsandroidapp.model.dataapi

import com.example.postsandroidapp.model.classes.Pagination
import com.example.postsandroidapp.model.classes.Post
import com.google.gson.annotations.SerializedName

data class PostsDataAPI(

    @SerializedName("status")
    var status: Boolean?,
    @SerializedName("data")
    var dataList: MutableList<Post>,

    @SerializedName("pagination")
    var pagination: Pagination,

    @SerializedName("message")
    var message: String?,

    @SerializedName("errors")
    var errors: List<List<String>>?
)