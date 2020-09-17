package com.example.postsandroidapp

import android.app.Application
import com.example.postsandroidapp.model.utils.JavaUtils
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/*
 val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
We don’t need to cancel this scope since we want it to remain active as long as
the application process is alive, so we don’t hold a reference to the SupervisorJob.
We can use this scope to run coroutines that need a longer lifetime than the calling
 scope might offer in our app.
 */
class MyApp : Application() {
    companion object{
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    override fun onCreate() {
        super.onCreate()


        Hawk.init(this).build()
        JavaUtils.CreateUrlAPI()
    }
}