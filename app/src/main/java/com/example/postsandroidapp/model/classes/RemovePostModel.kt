package com.example.postsandroidapp.model.classes

data class RemovePostModel(
    var user_id:String?="",
    var customer_id:String?="",

    var token:String?="",
    var products:MutableList<RemovePost>?= mutableListOf()

)

data class RemovePost(
    var id:String?=""
)
