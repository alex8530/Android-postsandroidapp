package com.example.postsandroidapp.view.fragments.postdetails

import AdapterCallback
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.postsandroidapp.R
import com.example.postsandroidapp.databinding.FragmentPostDetailsBinding
import com.example.postsandroidapp.databinding.FragmentPostsListBinding
import com.example.postsandroidapp.view.base.BaseFragment
import com.example.postsandroidapp.view.fragments.postslist.PostsListViewModel
import popNavigation

class PostDetailsFragment :
    BaseFragment<PostDetailsViewModel, FragmentPostDetailsBinding>(PostDetailsViewModel::class.java) {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_post_details
    }

    override fun initViewModel(): PostDetailsViewModel {
        return getViewModelFromFactory (null)
    }

    override fun initialize() {

    }

    override fun setParameter() {

    }

    override fun setupAction() {
        mBinding.backTextView.setOnClickListener {
            popNavigation()
        }

    }


    override fun observeViewModel() {
        mViewModel.mCurrentPostLiveData.observe(viewLifecycleOwner, Observer {
            mBinding.titlePostTextView.text=it.title
        })
    }


}