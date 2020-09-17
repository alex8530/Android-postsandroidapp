package com.example.postsandroidapp.model.api

data class APIWrapper<T>  (
    var data: T?=null,
    var error:String ?=null,
    var code: Int?= null
)