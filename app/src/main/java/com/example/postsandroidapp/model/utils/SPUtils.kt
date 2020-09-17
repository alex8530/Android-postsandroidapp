package com.example.postsandroidapp.model.utils

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.postsandroidapp.model.classes.User
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import org.json.JSONObject
import java.lang.Exception

object SPUtils {


    const val USER_KEY = "USER_KEY"
    const val POST_KEY = "POST"
    const val POST_LIST_KEY = "POST_LIST"




    fun saveUser(user: User): Boolean {
        return Hawk.put(USER_KEY, user)
    }

    fun deleteUser() {
        Hawk.delete(USER_KEY)
    }

    fun getUser(): User? {
        return Hawk.get(USER_KEY, null)
    }


}