package com.example.postsandroidapp.model.classes

import com.google.gson.annotations.SerializedName

data class Pagination(

    @SerializedName("total")
    var total: Int,

    @SerializedName("count")
    var count: Int,

    @SerializedName("per_page")
    var per_page: Int,

    @SerializedName("current_page")
    var current_page: Int,

    @SerializedName("total_pages")
    var total_pages: Int
)