package com.example.postsandroidapp.model.classes

import com.example.postsandroidapp.R
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.fragment_posts_list.view.*

data class Post(

    @SerializedName("id")
    var id: Int?=0,
    @SerializedName("title")
    var title: String?="",
    var isSelected: Boolean? = false
    )