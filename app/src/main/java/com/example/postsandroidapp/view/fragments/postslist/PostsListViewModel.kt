package com.example.postsandroidapp.view.fragments.postslist

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.postsandroidapp.R
import com.example.postsandroidapp.model.api.APIUtils
import com.example.postsandroidapp.model.api.APIWrapper
import com.example.postsandroidapp.model.classes.Event
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

class PostsListViewModel(var context: Context?) : BaseViewModel() {

    var mPage = 1
    var mTotal = 1
    var mIsRefresh = false
    var isResetPaginationLiveData: MutableLiveData<Boolean> = MutableLiveData()

    var mListPosts: MutableList<Post> = mutableListOf()
    var selectedPosItem: Post? = null

    var isGetPostsListLoading: MutableLiveData<Boolean> = MutableLiveData()
    var apiWrapperGetPostsList: MutableLiveData<APIWrapper<PostsDataAPI>> =
        MutableLiveData()
    var localPostsList: MutableLiveData<MutableList<Post>> = MutableLiveData()

    var isRemovePostLoading: MutableLiveData<Event<Boolean>> = MutableLiveData()
    var apiWrapperRemovePost: MutableLiveData<Event<APIWrapper<MessageDataAPi>>> =
        MutableLiveData()

    var isAddPostLoading: MutableLiveData<Event<Boolean>> = MutableLiveData()
    var apiWrapperAddPost: MutableLiveData<Event<APIWrapper<MessageDataAPi>>> =
        MutableLiveData()


    init {
        //CHECK IF THERE IS AN INTERNET OR NOT
        if (JavaUtils.isInternetAvailable(context)) {
            //get data from web
            getListPosts(true)

        } else {
            //get data from local storage
            getListPostsLocal()
        }
    }

   fun getListPostsLocal() {

        localPostsList.value = Hawk.get(SPUtils.POST_LIST_KEY, mutableListOf<Post>())

    }

    fun getListPosts(isResetPagination: Boolean) {

        isGetPostsListLoading.value?.let {
            if (it) {
                return
            }
        }

        if (isResetPagination) {
            isResetPaginationLiveData.value = true
        }

        getListPosts(this.context, mPage)
    }


    private fun getListPosts(
        context: Context?,
        page: Int
    ) {

        if (!JavaUtils.isInternetAvailable(context)) {
            apiWrapperGetPostsList.value =
                APIWrapper(
                    null,
                    context?.getString(R.string.please_make_sure_you_are_connected_to_the_internet),
                    ConstantVal.NO_INTERNET_ERROR_CODE
                )
            return
        }

        isGetPostsListLoading.value = true

        val retrofitResponseCall =
            JavaUtils.postsAPI.getPostsList(
                APIUtils.TEMP_USER_TOKEN_KEY,
                page
            )

        retrofitResponseCall.enqueue(object : Callback<PostsDataAPI> {
            override fun onFailure(call: Call<PostsDataAPI>, t: Throwable) {
                isGetPostsListLoading.value = false
                apiWrapperGetPostsList.value =
                    APIWrapper(
                        null, JavaUtils.checkErrorRequest(t.message),
                        ConstantVal.SERVER_ERROR_CODE
                    )
            }

            override fun onResponse(
                call: Call<PostsDataAPI>,
                response: Response<PostsDataAPI>
            ) {
                isGetPostsListLoading.value = false
                Log.d(
                    "okHttp",
                    "getListPosts: " + GsonBuilder().setPrettyPrinting().create()
                        .toJson(response.body())
                )

                if (response.body() != null && response.body()?.errors?.size ?: 0 > 0) {
                    apiWrapperGetPostsList.value =
                        APIWrapper(
                            null
                            ,
                            JavaUtils.getMessagesErrorFromErrorValidation(response.body()?.errors.toString())
                            ,
                            response.code()
                        )
                } else if (response.errorBody() != null) {
                    try {
                        val errorBodyJson = response.errorBody()!!.string()
                        if (errorBodyJson.contains("errors")) {
                            val errorList: List<String>? =
                                GsonBuilder().create().fromJson<List<String>>(
                                    JSONObject(errorBodyJson).getJSONArray("errors").toString(),
                                    List::class.java
                                )
                            apiWrapperGetPostsList.value =
                                APIWrapper(
                                    null
                                    ,
                                    JavaUtils.getMessagesErrorFromErrorValidation(errorList?.toString())
                                    ,
                                    response.code()
                                )
                        } else {
                            val messageDataAPi: MessageDataAPi = GsonBuilder().create().fromJson(
                                JSONObject(errorBodyJson).toString(),
                                MessageDataAPi::class.java
                            )
                            apiWrapperGetPostsList.value =
                                APIWrapper(
                                    null
                                    , messageDataAPi.message
                                    , response.code()
                                )
                        }

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else if (response.isSuccessful && response.body() != null) {

                    apiWrapperGetPostsList.value =
                        APIWrapper(response.body(), null, response.code())
                } else {
                    apiWrapperGetPostsList.value =
                        APIWrapper(null, context?.getString(R.string.there_is_some_problem), -1)
                }
            }

        })


    }


    fun removePosts(
        context: Context?,
        postId: Int
    ) {

        if (!JavaUtils.isInternetAvailable(context)) {
            apiWrapperRemovePost.value = Event(
                APIWrapper<MessageDataAPi>(
                    null,
                    context?.getString(R.string.please_make_sure_you_are_connected_to_the_internet),
                    ConstantVal.NO_INTERNET_ERROR_CODE
                )
            )

            return
        }

        isRemovePostLoading.value = Event(true)
        val retrofitResponseCall =
            JavaUtils.postsAPI.removePosts(
                postId,
                APIUtils.TEMP_USER_TOKEN_KEY
            )

        retrofitResponseCall.enqueue(object : Callback<MessageDataAPi> {
            override fun onFailure(call: Call<MessageDataAPi>, t: Throwable) {
                isRemovePostLoading.value = Event(false)
                Event(
                    APIWrapper<MessageDataAPi>(
                        null, JavaUtils.checkErrorRequest(t.message),
                        ConstantVal.SERVER_ERROR_CODE
                    )
                )


            }

            override fun onResponse(
                call: Call<MessageDataAPi>,
                response: Response<MessageDataAPi>
            ) {
                isRemovePostLoading.value = Event(false)
                Log.d(
                    "okHttp",
                    "removePosts: " + GsonBuilder().setPrettyPrinting().create()
                        .toJson(response.body())
                )

                if (response.body() != null && response.body()?.errors?.size ?: 0 > 0) {
                    apiWrapperRemovePost.value = Event(
                        APIWrapper<MessageDataAPi>(
                            null
                            ,
                            JavaUtils.getMessagesErrorFromErrorValidation(response.body()?.errors.toString())
                            ,
                            response.code()
                        )
                    )

                } else if (response.errorBody() != null) {
                    try {
                        val errorBodyJson = response.errorBody()!!.string()
                        if (errorBodyJson.contains("errors")) {
                            val errorList: List<String>? =
                                GsonBuilder().create().fromJson<List<String>>(
                                    JSONObject(errorBodyJson).getJSONArray("errors").toString(),
                                    List::class.java
                                )
                            apiWrapperRemovePost.value = Event(
                                APIWrapper<MessageDataAPi>(
                                    null
                                    ,
                                    JavaUtils.getMessagesErrorFromErrorValidation(errorList?.toString())
                                    ,
                                    response.code()
                                )
                            )

                        } else {
                            try {
                                val messageDataAPi: MessageDataAPi =
                                    GsonBuilder().create().fromJson(
                                        JSONObject(errorBodyJson).toString(),
                                        MessageDataAPi::class.java
                                    )
                                apiWrapperRemovePost.value = Event(
                                    APIWrapper<MessageDataAPi>(
                                        null
                                        , messageDataAPi.message
                                        , response.code()
                                    )
                                )


                            } catch (e: Exception) {
                                apiWrapperRemovePost.value = Event(
                                    APIWrapper<MessageDataAPi>(
                                        null,
                                        context?.getString(R.string.there_is_some_problem),
                                        -1
                                    )
                                )


                            }
                        }

                    } catch (e: IOException) {
                        apiWrapperRemovePost.value = Event(
                            APIWrapper<MessageDataAPi>(
                                null,
                                context?.getString(R.string.there_is_some_problem),
                                -1
                            )
                        )


                    }
                } else if (response.isSuccessful && response.body() != null) {

                    apiWrapperRemovePost.value =
                        Event(APIWrapper(response.body(), null, response.code()))

                } else {
                    apiWrapperRemovePost.value = Event(
                        APIWrapper<MessageDataAPi>(
                            null,
                            context?.getString(R.string.there_is_some_problem),
                            -1
                        )
                    )

                }
            }

        })


    }

    fun addPost(context: Context?, name: String) {


        if (!JavaUtils.isInternetAvailable(context)) {
            apiWrapperAddPost.value = Event(
                APIWrapper<MessageDataAPi>(
                    null,
                    context?.getString(R.string.please_make_sure_you_are_connected_to_the_internet),
                    ConstantVal.NO_INTERNET_ERROR_CODE
                )
            )

            return
        }

        isAddPostLoading.value = Event(true)
        val retrofitResponseCall =
            JavaUtils.postsAPI.addPost(
                APIUtils.TEMP_USER_TOKEN_KEY, name, 0.0, 0.0
            )

        retrofitResponseCall.enqueue(object : Callback<MessageDataAPi> {
            override fun onFailure(call: Call<MessageDataAPi>, t: Throwable) {
                isAddPostLoading.value = Event(false)
                apiWrapperAddPost.value = Event(
                    APIWrapper<MessageDataAPi>(

                        null, JavaUtils.checkErrorRequest(t.message),
                        ConstantVal.SERVER_ERROR_CODE
                    )
                )
            }

            override fun onResponse(
                call: Call<MessageDataAPi>,
                response: Response<MessageDataAPi>
            ) {
                isAddPostLoading.value = Event(false)
                Log.d(
                    "okHttp",
                    "add post: " + GsonBuilder().setPrettyPrinting().create()
                        .toJson(response.body())
                )

                if (response.body() != null && response.body()?.errors?.size ?: 0 > 0) {
                    apiWrapperAddPost.value =
                        Event(
                            APIWrapper<MessageDataAPi>(
                                null
                                ,
                                JavaUtils.getMessagesErrorFromErrorValidation(response.body()?.errors.toString())
                                ,
                                response.code()
                            )
                        )
                } else if (response.errorBody() != null) {
                    try {
                        val errorBodyJson = response.errorBody()!!.string()
                        if (errorBodyJson.contains("errors")) {
                            val errorList: List<String>? =
                                GsonBuilder().create().fromJson<List<String>>(
                                    JSONObject(errorBodyJson).getJSONArray("errors").toString(),
                                    List::class.java
                                )
                            apiWrapperAddPost.value =
                                Event(
                                    APIWrapper<MessageDataAPi>(
                                        null
                                        ,
                                        JavaUtils.getMessagesErrorFromErrorValidation(errorList?.toString())
                                        ,
                                        response.code()
                                    )
                                )
                        } else {
                            try {
                                val messageDataAPi: MessageDataAPi =
                                    GsonBuilder().create().fromJson(
                                        JSONObject(errorBodyJson).toString(),
                                        MessageDataAPi::class.java
                                    )
                                apiWrapperAddPost.value =
                                    Event(
                                        APIWrapper<MessageDataAPi>(
                                            null
                                            , messageDataAPi.message
                                            , response.code()
                                        )
                                    )

                            } catch (e: Exception) {
                                apiWrapperAddPost.value =
                                    Event(
                                        APIWrapper<MessageDataAPi>(
                                            null,
                                            context?.getString(R.string.there_is_some_problem),
                                            -1
                                        )
                                    )

                            }
                        }

                    } catch (e: IOException) {
                        apiWrapperAddPost.value =
                            Event(
                                APIWrapper<MessageDataAPi>(
                                    null,
                                    context?.getString(R.string.there_is_some_problem),
                                    -1
                                )
                            )

                    }
                } else if (response.isSuccessful && response.body() != null) {

                    apiWrapperAddPost.value =
                        Event(
                            APIWrapper<MessageDataAPi>(response.body(), null, response.code())
                        )
                } else {
                    apiWrapperAddPost.value =
                        Event(
                            APIWrapper<MessageDataAPi>(
                                null,
                                context?.getString(R.string.there_is_some_problem),
                                -1
                            )
                        )
                }
            }

        })


    }


}