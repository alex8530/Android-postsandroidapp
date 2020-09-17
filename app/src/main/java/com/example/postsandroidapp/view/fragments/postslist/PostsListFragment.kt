package com.example.postsandroidapp.view.fragments.postslist

import AdapterCallback
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.postsandroidapp.R
import com.example.postsandroidapp.databinding.FragmentPostsListBinding
import com.example.postsandroidapp.model.api.APIWrapper
import com.example.postsandroidapp.model.classes.Post
import com.example.postsandroidapp.model.dataapi.MessageDataAPi
import com.example.postsandroidapp.model.utils.JavaUtils
import com.example.postsandroidapp.model.utils.SPUtils
import com.example.postsandroidapp.model.utils.Utils
import com.example.postsandroidapp.view.base.BaseFragment
import com.example.postsandroidapp.view.bottomsheets.addpost.AddEPostBottomSheetDialogFragmentDelegate
import com.example.postsandroidapp.view.bottomsheets.addpost.AddPostBottomSheetDialogFragment
import com.orhanobut.hawk.Hawk
import navigateTo


class PostsListFragment :
    BaseFragment<PostsListViewModel, FragmentPostsListBinding>(PostsListViewModel::class.java),

    AdapterCallback {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_posts_list
    }

    override fun initViewModel(): PostsListViewModel {
        return getViewModelFromFactory { PostsListViewModel(context) }
    }

    var mAdapter: PostListAdapter<Post>? = null

    override fun initialize() {
        mAdapter =
            context?.let { PostListAdapter(it, mViewModel.mListPosts, this) }

    }

    override fun setParameter() {
        mAdapter?.setAnimation(mBinding.postsRecyclerView)
        mBinding.postsRecyclerView.adapter = mAdapter
    }

    override fun setupAction() {

        mBinding.refreshSwipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        loadNextProducts()

        mBinding.deletePostImageView.setOnClickListener {
            if (JavaUtils.isInternetAvailable(context)) {
                removeSelectedPosts()
            } else {
                Utils.showFailAlert(activity,getString(R.string.must_internet_to_remove))
            }

        }
        mBinding.addPostImageView.setOnClickListener {
            //CHECK IF THERE IS AN INTERNET OR NOT
            if (JavaUtils.isInternetAvailable(context)) {
                val addPostDialog =
                    AddPostBottomSheetDialogFragment()
                addPostDialog.delegateCallBack = object :
                    AddEPostBottomSheetDialogFragmentDelegate {
                    override fun onPerformClick(name: String) {
                        mViewModel.addPost(context, name)
                        addPostDialog.dismiss()
                    }

                }
                addPostDialog.show(childFragmentManager, addPostDialog.tag)

            } else {
                Utils.showFailAlert(activity,getString(R.string.must_internet_to_add))

            }



        }
    }

    private fun removeSelectedPosts() {

        val selectedPosts = getSelectedPosts()
        if (selectedPosts.size == 0) {
            Toast.makeText(
                context,
                getString(R.string.must_select_at_least_one_item),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (selectedPosts.size > 1) {
            Toast.makeText(
                context,
                getString(R.string.must_select_one_item_only),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        mViewModel.selectedPosItem = selectedPosts[0]
        mViewModel.apiWrapperGetPostsList.value?.data?.dataList?.remove(selectedPosts[0])
        mViewModel.removePosts(context, selectedPosts[0].id!!)
    }


    private fun getSelectedPosts(): MutableList<Post> {
        val selectedPosts = mutableListOf<Post>()
        mAdapter?.let { adapter ->
            for (product in adapter.mListModel) {
                if (product.isSelected!!) {
                    selectedPosts.add(product)
                }
            }
        }
        return selectedPosts

    }

    private fun refreshData() {
        mAdapter?.clear()
        mViewModel.mIsRefresh = true
        //CHECK IF THERE IS AN INTERNET OR NOT
        if (JavaUtils.isInternetAvailable(context)) {
            //get data from web
            mViewModel.getListPosts(true)
        } else {
            //get data from local storage
            mViewModel.getListPostsLocal()
            mBinding.refreshSwipeRefreshLayout.isRefreshing = false
            mViewModel.mIsRefresh = false
        }
    }

    private fun resetPagination() {
        mViewModel.mPage = 1
        mViewModel.mTotal = 1
        mAdapter?.clear()
        Hawk.delete(SPUtils.POST_LIST_KEY)
    }

    private fun loadNextProducts() {
        mBinding.postsRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastVisibleItemPosition =
                    (mBinding.postsRecyclerView.layoutManager as LinearLayoutManager)
                        .findLastVisibleItemPosition()

                mViewModel.isGetPostsListLoading.value?.let {
                    if (lastVisibleItemPosition + 1 >= mAdapter!!.itemCount - 1
                        &&
                        mViewModel.mPage <= mViewModel.mTotal
                        &&
                        !it
                    ) {
                        mBinding.loadMorePostsProgressBar.visibility = View.VISIBLE
                        mBinding.postsRecyclerView.scrollToPosition(
                            mAdapter!!.itemCount
                        )
                        mViewModel.getListPosts(false)
                    }
                }

            }
        })
    }


    override fun getData() {

    }

    override fun observeViewModel() {
        mViewModel.isResetPaginationLiveData.observe(viewLifecycleOwner, Observer {
            resetPagination()
        })


        mViewModel.isGetPostsListLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (!mViewModel.mIsRefresh && mViewModel.mPage == 1) {
                    customDialogLoading?.show()
                }

            } else {
                mBinding.loadMorePostsProgressBar.visibility = View.GONE
                customDialogLoading?.let {
                    if (customDialogLoading!!.isShowing) {
                        customDialogLoading!!.dismiss()
                    }
                }
                mBinding.refreshSwipeRefreshLayout.isRefreshing = false
                mViewModel.mIsRefresh = false
            }
        })


        mViewModel.apiWrapperGetPostsList.observe(viewLifecycleOwner, Observer {

            if (it.data != null) {
                // when success code 200
                mViewModel.mTotal = it.data!!.pagination.total_pages
                mViewModel.mPage = it.data!!.pagination.current_page + 1
                mAdapter?.addAll(it.data?.dataList)

                if (mAdapter!!.itemCount < 1) {
                    mBinding.emptyOrderImageView.visibility = View.VISIBLE
                    mBinding.postsRecyclerView.visibility = View.GONE
                } else {
                    mBinding.emptyOrderImageView.visibility = View.GONE
                    mBinding.postsRecyclerView.visibility =
                        View.VISIBLE
                }

                //save to local storage
                var oldList=Hawk.get(SPUtils.POST_LIST_KEY, mutableListOf<Post>())
                oldList.addAll(it.data?.dataList!!)
                Hawk.put(SPUtils.POST_LIST_KEY, oldList)

            } else if (it.code == ConstantVal.UNAUTHORIZED_CODE) {
                //should restart the app and delete user data ,then go to login page

            } else if (it.code == ConstantVal.NO_INTERNET_ERROR_CODE) {
                Utils.showFailAlert(activity, it.data?.message!!)
            } else if (it.error != null) {
                Utils.showFailAlert(activity, it.error!!)
            }

        })


        mViewModel.isRemovePostLoading.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()
                ?.let { content -> // Only proceed if the event has never been handled
                    if (content) {
                        customDialogLoading?.show()
                    } else {
                        customDialogLoading?.dismiss()
                    }
                }

        })


        mViewModel.apiWrapperRemovePost.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()
                ?.let { content -> // Only proceed if the event has never been handled
                    if (content.data != null) {
                        // when success code 200
                        if (content.data?.status == true) {

                            Utils.showSuccessAlert(activity, getString(R.string.delete_done))
                            //update data
                            mViewModel.selectedPosItem?.let { post ->
                                mAdapter?.removeItem(post)

                                if (mAdapter!!.itemCount < 1) {
                                    mBinding.emptyOrderImageView.visibility = View.VISIBLE
                                    mBinding.postsRecyclerView.visibility = View.GONE
                                } else {
                                    mBinding.emptyOrderImageView.visibility = View.GONE
                                    mBinding.postsRecyclerView.visibility =
                                        View.VISIBLE

                                }
                            }

                        } else {
                            Utils.showFailAlert(activity, content.data?.message!!)
                        }

                    } else if (content.code == ConstantVal.UNAUTHORIZED_CODE) {
                        //should restart the app and delete user data ,then go to login page

                    } else if (content.code == ConstantVal.NO_INTERNET_ERROR_CODE) {
                        Utils.showFailAlert(activity, content.data?.message!!)
                    } else if (content.error != null) {
                        Utils.showFailAlert(activity, content.error!!)
                    } else {

                    }

                }

        })



        mViewModel.isAddPostLoading.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()
                ?.let { content -> // Only proceed if the event has never been handled
                    if (content) {
                        customDialogLoading?.show()
                    } else {
                        customDialogLoading?.dismiss()
                    }
                }

        })


        mViewModel.apiWrapperAddPost.observe(this, Observer {
            it.getContentIfNotHandled()
                ?.let { it -> // Only proceed if the event has never been handled
                    if (it.data != null) {
                        // when success code 200
                        if (it.data?.status == true) {

                            Utils.showSuccessAlert(activity, getString(R.string.add_done))
                            mViewModel.getListPosts(true)
                        } else {
                            Utils.showFailAlert(activity, it.data?.message!!)
                        }

                    } else if (it.code == ConstantVal.UNAUTHORIZED_CODE) {
                        //should restart the app and delete user data ,then go to login page

                    } else if (it.code == ConstantVal.NO_INTERNET_ERROR_CODE) {
                        Utils.showFailAlert(activity, it.data?.message!!)
                    } else if (it.error != null) {
                        Utils.showFailAlert(activity, it.error!!)
                    }


                }

        })

        mViewModel.localPostsList.observe(viewLifecycleOwner, Observer {

            mAdapter?.addAll(it)

            if (mAdapter!!.itemCount < 1) {
                mBinding.emptyOrderImageView.visibility = View.VISIBLE
                mBinding.postsRecyclerView.visibility = View.GONE
            } else {
                mBinding.emptyOrderImageView.visibility = View.GONE
                mBinding.postsRecyclerView.visibility =
                    View.VISIBLE
            }
        })


    }

    override fun onClickAdapterItem(position: Int, tag: String, item: Any?) {
        if (item is Post) {
            Hawk.put(SPUtils.POST_KEY, item)
            navigateTo(R.id.postsListFragment, R.id.action_postsListFragment_to_postDetailsFragment)
        }
    }


    override fun onStop() {
        /*
          because the viewModel..when i go out from fragment, and back to it,,
          the observer of data will re trigger and add the data to previous.. so  ..
           */
        resetPagination()

        super.onStop()
    }


}