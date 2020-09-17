package com.example.postsandroidapp.model.classes

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("id")
    var id : Int,

    @SerializedName("email")
    var email : String?,
    @SerializedName("user_name")
    var user_name : String?,

    @SerializedName("mobile_number")
    var mobile_number : String?,


    @SerializedName("token")
    var token : String?,

    @SerializedName("fcm_token")
    var fcm_token : String?,

    @SerializedName("user_image")
    var user_image : String?
)