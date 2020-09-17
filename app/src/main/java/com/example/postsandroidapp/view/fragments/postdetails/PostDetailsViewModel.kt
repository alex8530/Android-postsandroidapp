package com.example.postsandroidapp.view.fragments.postdetails

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.postsandroidapp.R
import com.example.postsandroidapp.model.api.APIUtils
import com.example.postsandroidapp.model.api.APIWrapper
import com.example.postsandroidapp.model.classes.Post
import com.example.postsandroidapp.model.classes.RemovePostModel
import com.example.postsandroidapp.model.dataapi.MessageDataAPi
import com.example.postsandroidapp.model.dataapi.PostsDataAPI
import com.example.postsandroidapp.model.utils.JavaUtils
import com.example.postsandroidapp.model.utils.SPUtils
import com.example.postsandroidapp.viewmodels.base.BaseViewModel
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

class PostDetailsViewModel : BaseViewModel() {

    var mCurrentPostLiveData:   MutableLiveData<Post> = MutableLiveData()

    init {
        mCurrentPostLiveData.value = Hawk.get(SPUtils.POST_KEY, Post())
    }
}