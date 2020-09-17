package com.example.postsandroidapp.model.dataapi

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MessageDataAPi(
    @SerializedName("errors")
    var errors: List<List<String?>?>? = null,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("status")
    var status: Boolean? = false

)